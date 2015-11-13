package storm.xml;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import storm.base.MacroDef;

/**
 * @author blogchong
 * @Blog www.blogchong.com
 * @email blogchong@gmail.com
 * @QQ_G 191321336
 * @version 2014��11��9�� ����11:26:29
 */

public class MysqlXml {
	// xml·��
	private String fd;
	// Mysql����
	// mysql��ַ���˿�
	public static String Host_port;
	// ���ݿ���
	public static String Database;
	// ���ݿ���
	public static String From;
	// �û���
	public static String Username;
	// ����
	public static String Password;

	public MysqlXml(String str) {
		this.fd = str;
	}

	public void read() {
		try {
			File file = new File(this.fd);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);

			NodeList nl = doc.getElementsByTagName(MacroDef.Parameter);

			Element e = (Element) nl.item(0);

			Host_port = e.getElementsByTagName(MacroDef.Host_port).item(0)
					.getFirstChild().getNodeValue();
			Database = e.getElementsByTagName(MacroDef.Database).item(0)
					.getFirstChild().getNodeValue();
			Username = e.getElementsByTagName(MacroDef.Username).item(0)
					.getFirstChild().getNodeValue();
			Password = e.getElementsByTagName(MacroDef.Password).item(0)
					.getFirstChild().getNodeValue();
			From = e.getElementsByTagName(MacroDef.From).item(0)
					.getFirstChild().getNodeValue();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
