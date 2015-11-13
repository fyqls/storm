package storm.xml;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import storm.base.MacroDef;

/** 
 * @author blogchong
 * @Blog   www.blogchong.com
 * @email  blogchong@gmail.com
 * @QQ_G   191321336
 * @version 2014��11��9�� ����11:26:29
 */

public class MonitorXml {

	// xml·��
	private String fd = null;
	// MetaBolt����
	// ƥ����������߼���ϵ
	public static String MatchLogic; 
	// !--ƥ�������б�
	public static String MatchType;
	// !--ƥ���ֶ��б�-
	public static String MatchField; 
	// !--�ֶ�ֵ�б�-
	public static String FieldValue; 

	public MonitorXml(String str) {
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

			MatchLogic = e.getElementsByTagName(MacroDef.MatchLogic).item(0)
					.getFirstChild().getNodeValue();
			MatchType = e.getElementsByTagName(MacroDef.MatchType).item(0)
					.getFirstChild().getNodeValue();
			MatchField = e.getElementsByTagName(MacroDef.MatchField).item(0)
					.getFirstChild().getNodeValue();
			FieldValue = e.getElementsByTagName(MacroDef.FieldValue).item(0)
					.getFirstChild().getNodeValue();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
