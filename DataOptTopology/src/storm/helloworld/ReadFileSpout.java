package storm.helloworld;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

import storm.base.MacroDef;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

/** 
 * @author blogchong
 * @Blog   www.blogchong.com
 * @email  blogchong@gmail.com
 * @QQ_G   191321336
 * @version 2014��11��9�� ����11:26:29
 */

@SuppressWarnings("serial")
public class ReadFileSpout implements IRichSpout {
	
	/**
	 * ����ֱ�Ӱ�spout�µ�spout���������ˣ���Ϊstorm��helloworld��spout
	 * ����ֻͳ��domain�ļ��е�����ֵ����һ�к����һ�м��������ֲ���
	 */

	private SpoutOutputCollector collector;
	FileInputStream fis;
	InputStreamReader isr;
	BufferedReader br;

	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {

		this.collector = collector;
		
		String file = "domain.log";

		try {

			this.fis = new FileInputStream(file);
			this.isr = new InputStreamReader(fis, MacroDef.ENCODING);
			this.br = new BufferedReader(isr);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void close() {

	}

	@Override
	public void activate() {

	}

	@Override
	public void deactivate() {

	}

	@Override
	public void nextTuple() {
		String str = "";
		try {
			while ((str = this.br.readLine()) != null) {
				this.collector.emit(new Values(str));
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void ack(Object msgId) {

	}

	@Override
	public void fail(Object msgId) {

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("str"));

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
