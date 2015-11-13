package storm.helloworld;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

/**
 * @author blogchong
 * @Blog www.blogchong.com
 * @email blogchong@gmail.com
 * @QQ_G 191321336
 * @version 2014��11��9�� ����21:50:29
 */

// ����һ����ʵ����ͳ�ƴ�Ƶwordcoutn��storm�е�helloworld
public class HelloWorldTopology {

	// ʵ����TopologyBuilder�ࡣ
	private static TopologyBuilder builder = new TopologyBuilder();

	public static void main(String[] args) throws InterruptedException,
			AlreadyAliveException, InvalidTopologyException {
		Config config = new Config();

		// �����緢�ڵ㲢���䲢�������ò�����������Ƹö����ڼ�Ⱥ�е��߳�����
		builder.setSpout("Random", new ReadFileSpout(), 1);

		// ��׼������
		builder.setBolt("Norm", new WordNormalizerBolt(), 2).shuffleGrouping(
				"Random");

		// ���ʼ���
		builder.setBolt("Count", new WordCountBolt(), 3).fieldsGrouping("Norm",
				new Fields("word"));

		builder.setBolt("print", new PrintWorldCountBolt(), 1).shuffleGrouping(
				"Count");

		config.setDebug(false);

		if (args != null && args.length > 0) {
			config.setNumWorkers(1);
			StormSubmitter.submitTopology(args[0], config,
					builder.createTopology());
		} else {
			// �����Ǳ���ģʽ�����е��������롣
			config.setMaxTaskParallelism(1);
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("helloword", config,
					builder.createTopology());
		}

	}
}
