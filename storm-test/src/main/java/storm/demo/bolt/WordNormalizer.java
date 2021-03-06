
package storm.demo.bolt;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
public class WordNormalizer implements IRichBolt {
    private OutputCollector collector;
    @Override
    public void prepare(Map stormConf, TopologyContext context,
                        OutputCollector collector) {
        this.collector = collector;
    }
    /**这是bolt中最重要的方法，每当接收到一个tuple时，此方法便被调用
     * 这个方法的作用就是把文本文件中的每一行切分成一个个单词，并把这些单词发射出去（给下一个bolt处理）
     * **/
    @Override
    public void execute(Tuple input) {
        String sentence = input.getString(0);
        System.out.println("========normal string  "+input.getString(0)+" sid: "+
                  input.getSourceStreamId() +" val: "+ input.getValue(0) +"====");
        String[] words = sentence.split(" ");
        for (String word : words) {
            word = word.trim();
            if (!word.isEmpty()) {
                word = word.toLowerCase();
                // Emit the word
                List a = new ArrayList();
                a.add(input);
                collector.emit(a, new Values(word));
            }
        }
        //确认成功处理一个tuple
        collector.ack(input);
    }
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));

    }
    @Override
    public void cleanup() {
        // TODO Auto-generated method stub

    }
    @Override
    public Map<String, Object> getComponentConfiguration() {
        // TODO Auto-generated method stub
        return null;
    }
}