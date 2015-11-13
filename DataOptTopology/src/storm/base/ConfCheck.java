package storm.base;

import java.io.File;

import storm.bolt.MetaBolt;
import storm.bolt.MonitorBolt;
import storm.bolt.MysqlBolt;
import storm.spout.MetaSpout;

/** 
 * @author blogchong
 * @Blog   www.blogchong.com
 * @email  blogchong@gmail.com
 * @QQ_G   191321336
 * @version 2014��11��15�� ����22:22:25
 */

/**
 * �ú�����ʱ��������ļ��Ƿ����ı䣬
 * ʵ����Topology�Ķ�̬���� ���ڲ�����top������£�ʵ�����ݴ���Ķ�̬����
 */

public class ConfCheck extends Thread {

	private String xmlpath = "xxx.xml";
	private int heartbeat = 1000;
	private String type = "type";

	public ConfCheck(String XmlPath, int HeartBeat, String type) {
		this.xmlpath = XmlPath;
		this.heartbeat = HeartBeat;
		this.type = type;
	}

	public void run() {
		long init_time = 0;// hash��ʼֵ

		for (int i = 0;; i++) {

			try {

				File file = new File(this.xmlpath);
				// ���ʱ����Ƿ�һ��
				long lasttime = file.lastModified();

				if (i == 0) {
					init_time = lasttime;
				} else {
					if (init_time != lasttime) {

						init_time = lasttime;

						if (this.type.equals(MacroDef.Thread_type_metaqspout)) {
							MetaSpout.isload();
						} else if (this.type
								.equals(MacroDef.Thread_type_monitorbolt)) {
							MonitorBolt.isload();
						} else if (this.type
								.equals(MacroDef.Thread_type_mysqlbolt)) {
							MysqlBolt.isload();
						} else if (this.type
								.equals(MacroDef.Thread_type_metaqbolt)) {
							MetaBolt.isload();
						}
					}
				}

				Thread.sleep(this.heartbeat);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		new ConfCheck("MysqlBolt.xml", 1000, "test").start();
		Thread.sleep(100000);
	}

}
