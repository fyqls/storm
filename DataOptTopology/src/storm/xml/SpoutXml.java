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
 * @version 2014��11��15�� ����22:43:11
 */

public class SpoutXml {
	// xml·��
	private static String fd;
	// MetaBolt����
	public static String MetaRevTopic; // !--MetaQ��Ϣ����--
	public static String MetaZkConnect; // !--MetaQ�����ַ--
	public static String MetaZkRoot; // !--MetaQ����·��--
	public static String MetaConsumerConf; // !--MetaQ��������ID--

	@SuppressWarnings("static-access")
	public SpoutXml(String str) {
		this.fd = str;
	}

	@SuppressWarnings("static-access")
	public void read() {
		try {
			File file = new File(this.fd);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);

			NodeList nl = doc.getElementsByTagName(MacroDef.Parameter);

			Element e = (Element) nl.item(0);

			MetaRevTopic = e.getElementsByTagName(MacroDef.MetaRevTopic).item(0)
					.getFirstChild().getNodeValue();
			
			MetaZkConnect = e.getElementsByTagName(MacroDef.MetaZkConnect).item(0)
					.getFirstChild().getNodeValue();
			
			MetaZkRoot = e.getElementsByTagName(MacroDef.MetaZkRoot).item(0)
					.getFirstChild().getNodeValue();
			
			MetaConsumerConf = e.getElementsByTagName(MacroDef.MetaConsumerConf)
					.item(0).getFirstChild().getNodeValue();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
