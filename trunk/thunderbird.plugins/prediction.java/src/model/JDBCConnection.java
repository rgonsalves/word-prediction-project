package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JDBC actions that extends the generic database actions
 */
public class JDBCConnection extends GenericWrapper {
	public static String CONNECTION_STRING = "jdbc:hsqldb:hsql://localhost/wordsdb";//"jdbc:hsqldb:file:c:/hsql/words";//"jdbc:hsqldb:hsql://localhost:9500";//

	private Log log = LogFactory.getLog(this.getClass());
	/** database connection */
	private Connection m_Connection;

	public JDBCConnection() {

	}

//	public JDBCConnection(String connectionString) {
//		CONNECTION_STRING = connectionString;
//	}

	public String closeConnection() {
		try {
			finalize();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
		return "";
	}
	
	/**
	 * retrieves a connection from the database
	 * 
	 * @return a connection
	 */
	
	protected Connection getConnection() throws Exception {
		if (m_Connection == null) {
			Class.forName("org.hsqldb.jdbcDriver");
			Properties connProperties = new Properties();
			connProperties.put("user", "sa");// Constants.USER_STRING);
			connProperties.put("password", "");// Constants.PASSWORD_STRING);
			connProperties.put("autoReconnect", "true");
			connProperties.put("zeroDateTimeBehavior", "convertToNull");

			m_Connection = DriverManager.getConnection(CONNECTION_STRING,
					connProperties);
			m_IsConnected = true;

		}
		return m_Connection;
	}
}
