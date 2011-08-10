package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class JDBCWrapper extends GenericWrapper{
	private static JDBCWrapper connection = null;
	private Connection m_Connection;


	public JDBCWrapper(String dataSourceName) throws Exception {
		super();
		m_dataSourceName = dataSourceName;
	}
	
	public static JDBCWrapper getConnectionInstance() throws Exception{
		if(connection==null){
			 
			try {
				connection = new JDBCWrapper(CONNECTION_STRING);
			} catch (Exception e) {
				throw e;
			}
		}
		return connection;
	}
	/**
	 * db connection
	 * 
	 * @return a connection
	 */
	protected Connection getConnection() throws Exception {
		if (m_Connection == null) {
			try {

//					try {
						Class.forName("org.hsqldb.jdbcDriver");
					Properties connProperties=new Properties();
					connProperties.put("user", "sa");//Constants.USER_STRING);
					connProperties.put("password", "");//Constants.PASSWORD_STRING);
					connProperties.put("autoReconnect", "true");
					connProperties.put("zeroDateTimeBehavior","convertToNull");
				
					m_Connection = DriverManager.getConnection(CONNECTION_STRING,connProperties);
					m_IsConnected = true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return m_Connection;
	}
	
	protected int getRowsNumber(){
		return qRowsNumber;
	}
}
