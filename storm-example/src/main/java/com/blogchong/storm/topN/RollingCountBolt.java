package com.blogchong.storm.topN;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

/**
 * 1 在这里我们需要去实现一个滑动窗口，请注意，在我们实现滑动窗口的过程之中清空的是当前滑动窗口的下一个
 *
 *
 *
 * @author Yin Shuai
 *
 */
public class RollingCountBolt implements IRichBolt {

    private static final long serialVersionUID = 1765379339552134320L;

    private HashMap<Object, long[]> _objectCounts = new HashMap<Object, long[]>();
    private int _numBuckets;
    private transient Thread cleaner;
    private OutputCollector _collector;

    /**
     * _trackMinute
     * 是我们整个滑动窗口的大小，滑动窗口的大小，本质上决定了我们的时间区间，也就是说，假设我们目前滑动窗口的总体大小为15分钟。
     * 那我们的商品点击的实时排序的指标值，好比商品浏览量的计算值，也就是15分钟
     *
     * 而单个窗口的大小也就是我，我们这个三十分钟在随着时间不断的在推移
     *
     * 举例说明：在最初的构造过程之中,如果我们的桶的数目为10，那么单个窗口的时间长度为3.
     *
     * [0,30],[3,33],[6,36],[9,39],[12,42] 统计的数值处在不断的变化之中
     *
     */
    private int _trackMinutes;

    public RollingCountBolt(int numBuckets, int trackMinutes) {
        this._numBuckets = numBuckets;
        this._trackMinutes = trackMinutes;
    }

    public long totalObjects(Object obj) {
        long[] curr = _objectCounts.get(obj);
        long total = 0;
        for (long l : curr) {
            total += l;
        }
        return total;
    }

    public int currentBucket(int buckets) {
        return currentSecond() / secondsPerBucket(buckets) % buckets;
    }

    public int currentSecond() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    /**
     *
     * @param buckets
     *            你设定的桶的数量
     * @return 依据我们默认的_trackMinutes / buckets 得到每一个桶的数量
     */
    public int secondsPerBucket(int buckets) {
        return _trackMinutes * 60 / buckets;
    }

    public long millisPerBucket(int buckets) {
        return (long) 1000 * secondsPerBucket(buckets);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void prepare(Map stormConf, TopologyContext context,
                        OutputCollector collector) {
        // TODO Auto-generated method stub
        _collector = collector;
        cleaner = new Thread(new Runnable() {

            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                // TODO Auto-generated method stub
                int lastBucket = currentBucket(_numBuckets);

                while (true) {

                    int currBucket = currentBucket(_numBuckets);
                    p("线程while循环： 当前的桶为：" + currBucket);

                    if (currBucket != lastBucket) {
                        p("线程while循环:之前的桶数为：" + lastBucket);

                        int bucketToWipe = (currBucket + 1) % _numBuckets;
                        p("线程while循环:要擦除掉的桶为：" + bucketToWipe);

                        synchronized (_objectCounts) {
                            Set objs = new HashSet(_objectCounts.keySet());

                            for (Object obj : objs) {

                                long[] counts = _objectCounts.get(obj);
                                long currBucketVal = counts[bucketToWipe];
                                p("线程while循环:擦除掉的值为:" + currBucketVal);
                                counts[bucketToWipe] = 0;
                                long total = totalObjects(obj);
                                if (currBucketVal != 0) {
                                    p("线程while循环:擦除掉的值为不为0：那就发射数据：obj total"
                                            + obj + ":" + total);
                                    _collector.emit(new Values(obj, total));

                                }
                                if (total == 0) {

                                    p("线程while循环: 总数为0以后,将obj对象删除");
                                    _objectCounts.remove(obj);

                                }
                            }
                        }
                        lastBucket = currBucket;
                    }

                    long delta = millisPerBucket(_numBuckets)
                            - (System.currentTimeMillis() % millisPerBucket(_numBuckets));
                    Utils.sleep(delta);

                    p("\n");
                }
            }
        });
        cleaner.start();
    }

    @Override
    public void execute(Tuple input) {

        Object obj1 = input.getValue(0);
        Object obj = input.getValue(1);

        int currentBucket = currentBucket(_numBuckets);

        p("execute方法:当前桶：bucket： " + currentBucket);

        synchronized (_objectCounts) {

            long[] curr = _objectCounts.get(obj);

            if (curr == null) {
                curr = new long[_numBuckets];
                _objectCounts.put(obj, curr);
            }

            curr[currentBucket]++;

            System.err
                    .print(("execute方法：接受到的merchandiseIDS:" + obj.toString() + ",long数组："));

            for (long number : curr) {
                System.err.print(number + ":");
            }

            p("execute方法:发射的数据： " + obj + ":" + totalObjects(obj));

            /**
             * 我们不断的发射的也就是我们某一个商品id，在当前滑动窗口，也就是我们的时间周期内的指标计算值
             * 要注意，在排序的过程之中，我们只针对key， 也就是我们的商品id，由此发射给后续的排序bolt依据包含了时间区间的信息
             */

            // 每来一条数据，就会发射一次
            _collector.emit(new Values(obj, totalObjects(obj)));
            _collector.ack(input);
        }

        p("\n");
    }

    @Override
    public void cleanup() {
        // TODO Auto-generated method stub

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // TODO Auto-generated method stub
        declarer.declare(new Fields("merchandiseID", "count"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        // TODO Auto-generated method stub
        return null;
    }

    public void p(Object o) {
        System.err.println(o.toString());
    }
}
