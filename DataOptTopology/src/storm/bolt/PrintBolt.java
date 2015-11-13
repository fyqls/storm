package storm.bolt;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

/** 
 * @author blogchong
 * @Blog   www.blogchong.com
 * @email  blogchong@gmail.com
 * @QQ_G   191321336
 * @version 2014��11��9�� ����11:26:29
 */

@SuppressWarnings("serial")
public class PrintBolt extends BaseBasicBolt {

	public static void main(String[] args) {

	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		try {
			String mesg = input.getString(0);
			if (mesg != null)
				// ��ӡ����
				System.out.println("Tuple��" + mesg);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("mesg"));

	}

}
