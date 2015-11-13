package storm.base;

/** 
 * @author blogchong
 * @Blog   www.blogchong.com
 * @email  blogchong@gmail.com
 * @QQ_G   191321336
 * @version 2014��11��9�� ����11:31:25
 */

public  class MacroDef {
	
	////////////////////����ָ��־//////////////////////
	//�ָ��־','
	public static final String FLAG_COMMA = ",";
	//�ָ��־'\t'
	public static final String FLAG_TABS = "\t";
	//�ָ��־'::'
	public static final String FLAG_COLON = "::";
	//�ָ��־"\n"
	public final static String FLAG_ROW = "\n";//��¼�����
	
	////////////////////////������/////////////////////
	public static final String RULE_AND = "AND";
	public static final String RULE_OR = "OR";
	public static final String RLUE_REGULAR = "regular";
	public static final String RULE_RANGE = "range";
	public static final String RULE_ROUTINE0 = "routine0";
	public static final String RULE_ROUTINE1 = "routine1";
	
	/////////////////����UTF-8///////////////
	public static final String ENCODING = "UTF-8";
	
	//////////////////XML��������///////////////////
	public static final String Parameter = "Parameter";
	//MonitorBoltXml
	public static final String MatchLogic = "MatchLogic";
	public static final String MatchType = "MatchType";
	public static final String MatchField = "MatchField";
	public static final String FieldValue = "FieldValue";
	//MysqlBoltXml
	public static final String Host_port = "Host_port";
	public static final String Database = "Database";
	public static final String Username = "Username";
	public static final String Password = "Password";
	public static final String From = "From";	
	//SpoutXml
	public static final String MetaRevTopic = "MetaRevTopic";
	public static final String MetaZkConnect = "MetaZkConnect";
	public static final String MetaZkRoot = "MetaZkRoot";
	public static final String MetaConsumerConf = "MetaConsumerConf";
	//MetaXml
	public static final String MetaTopic = "MetaTopic";

	
	
	////////////////////�������ͳ��/////////////////////
	//MetaSpout����ͳ�����
	public final static long SPOUT_DEBUG = 1000;//Tupleͳ�������
	public final static boolean SPOUT_FLAG = false;
	//MetaBolt����ͳ�����
	public final static long meta_debug = 1000;//
	public final static boolean meta_flag = false;
	
	/////////////��������ļ���HeartBeat�������(ms)////////////////////
	public final static int  HEART_BEAT = 1000;
	
	//////////////////��������̶߳���//////////////////////////
	public final static String Thread_type_metaqspout = "metaqspout";
	public final static String Thread_type_monitorbolt = "monitorbolt";
	public final static String Thread_type_mysqlbolt = "mysqlbolt";
	public final static String Thread_type_metaqbolt = "metaqbolt";
	
}
