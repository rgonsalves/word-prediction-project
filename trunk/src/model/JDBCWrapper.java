package model;

import javax.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Clase que extiende del GenericWrapper que empaqueta todas 
 * las acciones jdbc especificas relativas a la conexion BD. 
 */
public class JDBCWrapper extends GenericWrapper {
	private static JDBCWrapper connection = null;
	private Log log = LogFactory.getLog(this.getClass());

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
	
	public DataSource getDataSource() throws Exception {
		try {
			
//			isTransactional = false;
//			if (m_DataSource == null) {
//				synchronized (this) {
//					if (m_DataSource == null) {
//					//	Context context = new InitialContext();
//					//	context.bind(Constants.MYSQL_DATASOURCE,"jdbc/POUMmysqlDB");
//
//						m_DataSource = (DataSource) context.lookup ("jdbc:mysql://localhost/titran");//m_dataSourceName);//("jdbc/POUMmysqlDB");"jdbc:mysql://localhost/titran"

//							InitialContext initialContext = new InitialContext ();
//							Context envContext = (Context) initialContext.lookup ("java:comp/env");
//							m_DataSource = (DataSource) envContext.lookup (m_dataSourceName);//("jdbc/POUMmysqlDB");

//					}
//				}
//			}
		}
		catch (Exception e) {
			log.error( "Error al crear la conexion "+ e.toString());
			throw e;
		}
		return m_DataSource;
	}

}
