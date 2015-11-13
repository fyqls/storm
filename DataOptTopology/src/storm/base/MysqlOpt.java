package storm.base;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** 
 * @author blogchong
 * @Blog   www.blogchong.com
 * @email  blogchong@gmail.com
 * @QQ_G   191321336
 * @version 2014��11��9�� ����11:26:29
 */

@SuppressWarnings("serial")
public class MysqlOpt implements Serializable {

	public Connection conn = null;
	PreparedStatement statement = null;

	// �������ݿ�
	public boolean connSQL(String host_p, String database, String username,
			String password) {

		String url = "jdbc:mysql://" + host_p + "/" + database
				+ "?characterEncoding=UTF-8";

		try {

			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, username, password);
			return true;

		} catch (ClassNotFoundException cnfex) {

			System.out
					.println("MysqlBolt-- Error: Loading JDBC/ODBC dirver failed!");
			cnfex.printStackTrace();

		} catch (SQLException sqlex) {

			System.out.println("MysqlBolt-- Error: Connect database failed!");
			sqlex.printStackTrace();

		}
		return false;
	}

	// ��������
	public boolean insertSQL(String sql) {
		try {

			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			return true;

		} catch (SQLException e) {

			System.out.println("MysqlBolt-- Error: Insert database failed!");
			e.printStackTrace();

		} catch (Exception e) {

			System.out.println("MysqlBolt-- Error: Insert failed!");
			e.printStackTrace();
		}

		return false;
	}

	// �ر�����
	public void deconnSQL() {
		try {

			if (conn != null)
				conn.close();

		} catch (Exception e) {

			System.out.println("MysqlBolt-- Error: Deconnect database failed!");
			e.printStackTrace();
		}
	}

}
