package storm.demo;

import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import storm.demo.bolt.WordCounter;
import storm.demo.bolt.WordNormalizer;
import storm.demo.spout.WordReader;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
public class WordCountTopologyMain {
    public static void main(String[] args) throws InterruptedException {
        //定义一个Topology
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("word-reader",new WordReader());
        builder.setBolt("word-normalizer", new WordNormalizer(),3)
                .shuffleGrouping("word-reader");
        builder.setBolt("word-counter", new WordCounter(),4)
                .fieldsGrouping("word-normalizer", new Fields("word"));
        //配置
        Config conf = new Config();
        conf.put("wordsFile", "/tmp/test.txt");
        conf.setDebug(true);
        //提交Topology
        conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
        //创建一个本地模式cluster
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("Getting-Started-Toplogie", conf,
                builder.createTopology());
//        try {
//            StormSubmitter.submitTopology("Getting-Started-Toplogie", conf,
//                    builder.createTopology());
//        } catch (AlreadyAliveException e) {
//            e.printStackTrace();
//        } catch (InvalidTopologyException e) {
//            e.printStackTrace();
//        }
       Thread.sleep(10000);
       cluster.killTopology("Getting-Started-Toplogie");
       cluster.shutdown();
    }
}