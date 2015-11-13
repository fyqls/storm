package storm;

import storm.bolt.MonitorBolt;
import storm.bolt.MysqlBolt;
import storm.bolt.PrintBolt;
import storm.spout.ReadLogSpout;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;

/**
 * @author blogchong
 * @Blog www.blogchong.com
 * @email blogchong@gmail.com
 * @QQ_G 191321336
 * @version 2014��11��9�� ����11:26:29
 */

/**
 * ���ֻ࣬Ҫspout��bolt���еģ������������top
 */

public class Topology {

	// ʵ����TopologyBuilder�ࡣ
	private static TopologyBuilder builder = new TopologyBuilder();

	public static void main(String[] args) throws InterruptedException,
			AlreadyAliveException, InvalidTopologyException {
		Config config = new Config();

		// �����緢�ڵ㲢���䲢�������ò�����������Ƹö����ڼ�Ⱥ�е��߳�����
		builder.setSpout("readlog", new ReadLogSpout(), 1);

		// ����monitor��ع��˽ڵ�
		builder.setBolt("monitor", new MonitorBolt("MonitorBolt.xml"), 3)
				.shuffleGrouping("readlog");

		// ����mysql���ݴ洢�ڵ�
		builder.setBolt("mysql", new MysqlBolt("MysqlBolt.xml"), 3)
				.shuffleGrouping("monitor");

		builder.setBolt("print", new PrintBolt(), 3).shuffleGrouping("monitor");

		config.setDebug(false);

		if (args != null && args.length > 0) {
			config.setNumWorkers(1);
			StormSubmitter.submitTopology(args[0], config,
					builder.createTopology());
		} else {
			// �����Ǳ���ģʽ�����е��������롣
			config.setMaxTaskParallelism(1);
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("simple", config, builder.createTopology());
		}

	}

}
