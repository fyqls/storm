package storm.bolt;

import java.util.Map;

import storm.base.ConfCheck;
import storm.base.MacroDef;
import storm.base.MysqlOpt;
import storm.xml.MysqlXml;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

/**
 * @author blogchong
 * @Blog www.blogchong.com
 * @email blogchong@gmail.com
 * @QQ_G 191321336
 * @version 2014��11��9�� ����11:26:29
 */

@SuppressWarnings("serial")
public class MysqlBolt implements IRichBolt {

	@SuppressWarnings("unused")
	private OutputCollector collector;
	
	//�Ƿ�������ñ�־λ
	private static boolean flag_load = false;
	
	private long register = 0;

	String mysqlXml = "Mysql.xml";

	MysqlOpt mysql = new MysqlOpt();

	private boolean flag_par = true;
	private boolean flag_xml = true;

	String from = "monitor"; // ����

	// ���캯��
	public MysqlBolt(String MysqlXML) {
		if (MysqlXML == null) {
			flag_par = false;
		} else {
			this.mysqlXml = MysqlXML;
		}
	}

	public static void main(String[] args) {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {

		System.out.println("MysqlBolt	--	Start!");
		this.collector = collector;
		
		if (this.flag_par == false) {
			System.out
					.println("MetaSpout-- Erre: can't get the path of Spout.xml!");
		} else {
			// ���ü���߳�
			new ConfCheck(this.mysqlXml, MacroDef.HEART_BEAT,
					MacroDef.Thread_type_mysqlbolt).start();
		}

	}

	// ���ı�־λ
	public static void isload() {
		flag_load = false;
	}
	
	// ������ʼ��
	public void Loading() {

		new MysqlXml(this.mysqlXml).read();
		// mysql��ַ���˿�
		String host_port = MysqlXml.Host_port;
		// ���ݿ���
		String database = MysqlXml.Database;
		// �û���
		String username = MysqlXml.Username;
		// ����
		String password = MysqlXml.Password;
		// ����
		this.from = MysqlXml.From;

		if (this.mysql.connSQL(host_port, database, username, password) == false) {

			System.out
					.println("MysqlBolt--Config errer, Please check Mysql-conf: "
							+ this.mysqlXml);
			flag_xml = false;
		} else {
			System.out.println("MysqlBolt-- Conf Loaded: " + this.mysqlXml);
		}
	}

	@Override
	public void execute(Tuple input) {

		String str = input.getString(0);

		if (this.flag_par == false) {
			System.out
					.println("MysqlBolt-- Erre: can't get the path of Mysql.xml!");
		} else {
			
			// ��������ļ��Ƿ����
			if (flag_load == false) {
				// �����ļ�������������м��ز�������
				Loading();
				if (register != 0) {
					System.out.println("MysqlBolt-- Conf Change: "
							+ this.mysqlXml);
				} else {
					System.out.println("MysqlBolt-- Conf Loaded: "
							+ this.mysqlXml);
				}
			}

			if (this.flag_xml == true) {

				String sql = send_str(str);

				if (this.mysql.insertSQL(sql) == false) {
					System.out
							.println("MysqlBolt-- Erre: can't insert tuple into database!");
					System.out.println("MysqlBolt-- Error Tuple: " + str);
					System.out.println("SQL: " + sql);
				}
			}
		}

	}

	public String send_str(String str) {

		String send_tmp = null;
		String field[] = str.split(MacroDef.FLAG_TABS);

		for (int i = 0; i < field.length; i++) {

			if (i == 0) {
				send_tmp = "'" + field[0] + "', '";
			} else if (i == (field.length - 1)) {
				send_tmp = send_tmp + field[i] + "'";
			} else {
				send_tmp = send_tmp + field[i] + "', '";
			}
		}
		String send = "insert into " + this.from
				+ "(domain, value, time, validity, seller) values (" + send_tmp
				+ ");";

		return send;
	}

	@Override
	public void cleanup() {
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
