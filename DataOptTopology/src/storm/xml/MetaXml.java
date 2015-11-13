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
 * @Blog   www.blogchong.com
 * @email  blogchong@gmail.com
 * @QQ_G   191321336
 * @version 2014��11��16�� ����19:54:29
 */

public class MetaXml {
	
	//xml·��
	private static String fd;
	//MetaBolt����
	//!--MetaQ��Ϣ����--
	public static String MetaTopic; 
	//!--MetaQ�����ַ--
	public static String MetaZkConnect;	
	//!--MetaQ����·��--
	public static String MetaZkRoot;			

	
	@SuppressWarnings("static-access")
	public MetaXml(String str){
		this.fd = str;
	}
	
	@SuppressWarnings("static-access")
	public void read(){
		try {
			File file = new File(this.fd);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			
			NodeList nl = doc.getElementsByTagName(MacroDef.Parameter);
			
			Element e = (Element)nl.item(0);

			MetaTopic = e.getElementsByTagName(MacroDef.MetaTopic).item(0).getFirstChild().getNodeValue();
			MetaZkConnect = e.getElementsByTagName(MacroDef.MetaZkConnect).item(0).getFirstChild().getNodeValue();
			MetaZkRoot = e.getElementsByTagName(MacroDef.MetaZkRoot).item(0).getFirstChild().getNodeValue();
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
