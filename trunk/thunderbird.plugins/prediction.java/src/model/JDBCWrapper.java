package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Clase que extiende del GenericWrapper que empaqueta todas 
 * las acciones jdbc especificas relativas a la conexion BD. 
 */
public class JDBCWrapper extends GenericWrapper{
	private static JDBCWrapper connection = null;
	private Log log = LogFactory.getLog(this.getClass());
	/** Conexion a la base de datos */
	private Connection m_Connection;
	/**
	 * Constructor. Used in poum package for mysql
	 * @param dataSourceName El datasource donde nos queremos conectar
	 Constants.CONNECTION_STRING="jdbc:mysql://"+serverIp+":"+serverPort+"/"+dbName;
	 */
	public JDBCWrapper(String dataSourceName) {
		m_dataSourceName = dataSourceName;
	}
	
	public static JDBCWrapper getConnectionInstance() throws Exception{
		if(connection==null){
			 
			try {
				connection = new JDBCWrapper(CONNECTION_STRING);
//				log.debug("JDBCWrapper. Getting Connection Instance::CONNECTION STRING::"+CONNECTION_STRING);
				
			} catch (Exception e) {
//				log.error("Error " + e.toString());
				throw e;
			}
		}
		return connection;
	}
	/**
	 * Recupera una conexion a la base de datos
	 * 
	 * @return una conexion
	 */
	protected Connection getConnection() throws Exception {
		if (m_Connection == null) {
			try {

//					try {
						Class.forName("org.hsqldb.jdbcDriver");//DriverManager.registerDriver(new com.mysql.jdbc.Driver());
//						} catch (SQLException e) {
//							System.out.println("Oops! Got a MySQL error: " + e.getMessage());
//						}
//						catch (Exception e) {
//							System.out.println("Oops! Got a error: " + e.getMessage());
//						}
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
}
