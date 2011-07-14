package model;

import java.sql.*;
import java.text.*;
import java.util.*;
import javax.sql.*;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;




/**
 * Clase generica que empaqueta todas las acciones relativas a la 
 * conexion y ejecucion de consultas en la BD.
 */
public abstract class GenericWrapper {


	boolean isTransactional;
	String ODBCPath;
	String DBUser;
	String DBPassword;
	boolean isODBC = false;

	protected Log log = LogFactory.getLog(this.getClass());
	
	static 	String CONNECTION_STRING	 =		"jdbc:hsqldb:hsql://localhost:9500";//"jdbc:mysql://localhost:3306/wordnet30";
	/** Data source JDBC 2.0 */
	static DataSource m_DataSource;
	static DataSource m_DataSourceXA;
	
//	protected DBValueObject dbvo;

	/** Recordset que contiene los datos recuperados de la peticion*/
	protected ResultSet m_Rset;

	/** Variable donde se recoge el resulta de una Consulta de tipo NonQuery*/
	public int ResultadoNonQuery =0;

	/** SQL Statement */
	protected PreparedStatement m_Prep;

	/** Store Procedure Statement */
	protected CallableStatement m_Cs;

	/** Conexion a la base de datos */
	private Connection m_Connection;

	/** Si es true entonces el JDBCWrapper tiene ya una conexion abierta a la base de datos */
	protected boolean m_IsConnected = false;

	static String m_dataSourceName;

	/** Constructor del objeto <code>JDBCWrapper</code>. */
	public GenericWrapper() {}

	/**
	 * Este m_todo permite preparar la ejecuci_n de una petici_n que cierra eventualmente
	 * el PreparedStatement y/o el RecordSet previamente abiertos 
	 * @throws Excepcion
	 */
	void beforeExecute() throws Exception {
		try {
			if (m_Rset != null) {
				m_Rset.close();
				m_Rset = null;
			}
			closePreparedStatement(m_Prep);
			closeCallableStatement(m_Cs);
		}
		catch (Exception e) {
			String message = "Error durante la inicializaci_n del recordset antes de un ejecuci_n de petici_n. ";
			log.error(message+":"+e.toString());
			throw e;
		}
	}

	/**
	 * Cierra la conexion con la base de datos y los objetos usados (ResultSet, PreparedStatement, CallableStatement)
	 * @exception Exception	Si hay algun problema al cerrar la conexion
	 */
	public void close() throws Exception {
		try {
			// Close ResultSet
			if (m_Rset != null) {
				m_Rset.close();
				m_Rset = null;
			}
			// Close PreparedStatement
			closePreparedStatement(m_Prep);        
			// Close CallableStatement
			closeCallableStatement(m_Cs);
			// Close the connection.
			if (m_Connection != null) {
				m_Connection.close();
				m_Connection = null;
			}
		}
		catch (Exception e) {
			log.error("Error closing data base connection "+
					e.toString());
			throw e;
		}
	}

	/** 
	 * Pone la conexion en modo auto-commit
	 * @exception SQLException
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		try {
			m_Connection.setAutoCommit(autoCommit);
		}
		catch (SQLException e) {
			log.error( "Error updating  autoCommit variable. "+
					e.toString());
			throw e;
		}
	}

	/**
	 * Funcion auxiliar que segun los parametros de entrada que recibe, los analiza
	 * y los a_ade al statement para su correcta ejecucion. 
	 * @param params Parametros de entrada
	 * @throws Exception
	 */
	public void prepararParams(Vector<Object> params) throws Exception
	{

		for (int i = 0; i < params.size(); i++) {
			if (params.elementAt(i) != null) {
				String className = params.elementAt(i).getClass().getName();
				if (className.equals("java.lang.String")) {
					m_Prep.setString(i + 1, (String) params.elementAt(i));
				}
				else if (className.equals("java.lang.Integer")) {
					m_Prep.setInt(i + 1, ( (Integer) params.elementAt(i)).intValue());
				}
				else if (className.equals("java.lang.Long")) {
					m_Prep.setLong(i + 1, ( (Long) params.elementAt(i)).longValue());
				}
				else if (className.equals("java.lang.Double")) {
					m_Prep.setDouble(i + 1, ( (Double) params.elementAt(i)).doubleValue());
				}
				else if (className.equals("java.util.Date")) {
					m_Prep.setTimestamp(i + 1,
							new java.sql.Timestamp( ( (java.util.Date)
									params.elementAt(i)).getTime()));
				}
				else if (className.equals("java.sql.Date")) {
					m_Prep.setDate(i + 1, (java.sql.Date) params.elementAt(i));
				}
				else if (className.equals("java.sql.Time")) {
					m_Prep.setTime(i + 1, (java.sql.Time) params.elementAt(i));
				}
				else if (className.equals("java.sql.Timestamp")) {
					m_Prep.setTimestamp(i + 1, (java.sql.Timestamp) params.elementAt(i));
				}
			
				else {
					// The class of one of parameters is not dealt here
					log.error("Error.JDBCWrapper.ExecutionSQL.unknownParameterClass");
					throw new Exception("Error.JDBCWrapper.ExecutionSQL.unknownParameterClass");
				}
			}
			else {
				m_Prep.setObject(i + 1, null);
			}
		}
	}

	/**
	 * Funcion auxiliar que segun los parametros de entrada que recibe, los analiza
	 * y los a_ade al Callablestatement para su correcta ejecucion. 
	 * @param params Parametros de entrada
	 * @param pos posicion
	 * @throws Exception
	 */
	public void prepararParams(Vector<Object> params, int pos) throws Exception
	{

		for (pos = 0; pos < params.size(); pos++) {

			if (params.elementAt(pos) != null) {
				String className = params.elementAt(pos).getClass().getName();
				log.debug("param pos: "+pos+" , className: ->"+className+"<-");
				if (className.equals("java.lang.String")) {
					m_Cs.setString(pos + 1, (String) params.elementAt(pos));
				}
				else if (className.equals("java.lang.Integer")) {
					m_Cs.setInt(pos + 1, ( (Integer) params.elementAt(pos)).intValue());
				}
				else if (className.equals("java.lang.Long")) {
					m_Cs.setLong(pos + 1, ( (Long) params.elementAt(pos)).longValue());
				}
				else if (className.equals("java.lang.Double")) {
					m_Cs.setDouble(pos + 1, ( (Double) params.elementAt(pos)).doubleValue());
				}
				else if (className.equals("java.util.Date")) {
					m_Cs.setTimestamp(pos + 1,
							new java.sql.Timestamp( ( (java.util.Date)
									params.elementAt(pos)).getTime()));
				}
				else if (className.equals("java.sql.Date")) {
					m_Cs.setDate(pos + 1, (java.sql.Date) params.elementAt(pos));
				}
				else if (className.equals("java.sql.Time")) {
					m_Cs.setTime(pos + 1, (java.sql.Time) params.elementAt(pos));
				}
				else if (className.equals("java.sql.Timestamp")) {
					m_Cs.setTimestamp(pos + 1, (java.sql.Timestamp) params.elementAt(pos));
				}
			
				else {
					// The class of one of parameters is not dealt here
					log.error("Error.JDBCWrapper.ExecutionSQL.unknownParameterClass");
					throw new Exception("Error.JDBCWrapper.ExecutionSQL.unknownParameterClass");
				}
			}
			else {
				log.error("Parameter NULL "+pos);
				m_Cs.setObject(pos + 1, null);
			}
		}
	}

	
	/**
	 * closes  PreparedStatement
	 * @exception Exception	if there is any problem trying to close  PreparedStatement
	 */
	private void closePreparedStatement(PreparedStatement prep) throws Exception {
		try {
			// Close preparedStatement.
			if (prep != null) {
				prep.close();
			}
		}
		catch (Exception e) {
			log.debug( "Error durante el cierre de un preparedStatement."+
					e.toString());
			throw e;
		}
	}

	/**
	 * Cierra el PreparedStatement
	 * @exception Exception	Si hay algun problema al cerrar PreparedStatement
	 */
	private void closeCallableStatement(CallableStatement  csprep) throws Exception {
		try {
			// Close preparedStatement.
			if (csprep != null) {
				csprep.close();
			}
		}
		catch (Exception e) {
			log.debug( "Error durante el cierre de un preparedStatement."+
					e.toString());
			throw e;
		}
	}

	/**
	 * Ejecuta una consulta SQL sin parametros. El resultado se guarda en la variable
	 * de clase m_Rset (Resultset).
	 * @param sql Consulta SQL request a ejecutar
	 * @exception Exception
	 */
	public void execute(String sql) throws Exception {
		log.debug( "execute(...) " + sql);
		try {
			beforeExecute();
			log.debug("Executing "+sql);
			m_Prep = getConnection().prepareStatement(sql);
			m_Rset = m_Prep.executeQuery();
			log.debug("Execution performed");
		}
		catch (SQLException e) {
			String message = e.getMessage();
			if (message.indexOf("ORA-08177") != -1) {
				int countdown = 5;
				boolean requete_executee = false;
				while ( (countdown != 0) && (requete_executee == false)) {
					try {
						countdown--;
						m_Rset = m_Prep.executeQuery();
						requete_executee = true;
					}
					catch (Exception error) {
						String messageb = error.getMessage();
						if (messageb.indexOf("ORA-08177") != -1) {
							log.error( "New try"+" "+error.toString());
							requete_executee = false;
						}
						else {
							log.error( "Error de conexi_n de la petici_n SQL."+
									error.toString());
							throw new Exception("Error de conexi_n de la petici_n SQL." + error.toString());
						}
					}
				}
				if (requete_executee == false) {
					log.error( "Error de conexi_n de la petici_n SQL. "+e.toString());
					throw new Exception("Error de conexi_n de la petici_n SQL." + e.toString());
				}
			}
			else {
				log.error( "Error de conexi_n de la petici_n SQL. "+e.toString());
				throw new Exception("Error de conexi_n de la petici_n SQL." + e.toString());
			}
		}
		catch (Exception e) {
			throw new Exception("Error de conexi_n de la petici_n SQL." + e.toString());
		}
	}

	/**
	 * Ejecuta una consulta SQL con un parametro. El resultado se guarda en la variable
	 * de clase m_Rset (Resultset).
	 * @param sql Consulta SQL request a ejecutar
	 * @param param1 string con el parametro de entrada
	 * @exception Exception
	 */
	public void execute(String sql, String param1) throws Exception {
		log.debug( "execute(...)" + sql + " parametro: " + param1);
		try {
			beforeExecute();
			m_Prep = getConnection().prepareStatement(sql);
			m_Prep.setString(1, param1);
			log.debug(
					"SQL request: [" + sql + "], [P1 = " + param1 != null ?
							param1.trim() : "null" + "]).");
			m_Rset = m_Prep.executeQuery();
		}
		catch (SQLException e) {
			String message = e.getMessage();
			if (message.indexOf("ORA-08177") != -1) {
				log.error( "ORA-08177 up: request re-executed "+e.toString());
				int countdown = 5;
				boolean requete_executee = false;
				while ( (countdown != 0) && (requete_executee == false)) {
					try {
						countdown--;
						m_Rset = m_Prep.executeQuery();
						requete_executee = true;
					}
					catch (Exception error) {
						String messageb = error.getMessage();
						if (messageb.indexOf("ORA-08177") != -1) {
							log.error( "New try"+" "+error.toString());
							requete_executee = false;
						}
						else {
							log.error( "execute: Error de conexi_n de la petici_n SQL."+
									error.toString());
							throw new Exception(
									"execute: Error de conexi_n de la petici_n SQL. " +
									e.toString());
						}
					}
				}
				if (requete_executee == false) {
					log.error( "execute: Error de conexi_n de la petici_n SQL."+
							e.toString());
					throw new Exception("execute: Error de conexi_n de la petici_n SQL. " +
							e.toString());
				}
			}
			else {
				log.error( "execute: Error de conexi_n de la petici_n SQL."+
						e.toString());
				throw new Exception("execute: Error de conexi_n de la petici_n SQL. " +
						e.toString());
			}
		}
		catch (Exception e) {
			log.error( "execute: Error de conexi_n de la petici_n SQL. "+e.toString());
			throw new Exception("execute: Error de conexi_n de la petici_n SQL. " +
					e.toString());
		}
	}

	/**
	 * Ejecuta una consulta SQL con parametros. El resultado se guarda en la variable
	 * de clase m_Rset (Resultset).
	 * @param sql Consulta SQL request a ejecutar
	 * @param params Lista de parametros de entrada (vector)
	 * @exception Exception
	 */
	public void execute(String sql, Vector<Object> params) throws Exception {
		try {
			beforeExecute();
			m_Prep = getConnection().prepareStatement(sql);
			prepararParams(params);
			log.debug("Executing "+sql+" Params:"+paramToString(params));
			m_Rset = m_Prep.executeQuery();
			log.debug("Execution performed");
			this.ResultadoNonQuery=1;
		}
		catch (SQLException e) {
			String message = e.toString();
			log.error( "Error de SQL "+ message);
			if (message.indexOf("ORA-08177") != -1) {
				log.error( "ORA-08177 up: request re-executed");
				int countdown = 5;
				boolean requete_executee = false;
				while ( (countdown != 0) && (requete_executee == false)) {
					try {
						countdown--;
						m_Rset = m_Prep.executeQuery();
						requete_executee = true;
					}
					catch (Exception error) {
						String messageb = error.getMessage();
						if (messageb.indexOf("ORA-08177") != -1) {
							log.error( "New try"+" "+error.toString());
							requete_executee = false;
						}
						else {
							log.error( "Error de conexi_n de la petici_n SQL."+
									error.toString());
//							throw new POUMConnectionRefusedException();
							e.printStackTrace();
						}
					}
				}
				if (requete_executee == false) {
					log.error( "Error de conexi_n de la petici_n SQL. "+e.toString());
//					throw new POUMConnectionRefusedException();
					e.printStackTrace();
				}
			}
			else {
				log.error( "Error de conexi_n de la petici_n SQL. "+e.toString());
//				throw new POUMConnectionRefusedException();
				e.printStackTrace();
			}
		}
		/*catch (Exception e) {
			throw new POUMConnectionRefusedException();
		}*/
	}

	/**
	 * Metodo ejecutado antes de eliminar el objeto JDBCWrapper de la memoria
	 * Cierra la conexi_n a la base de datos si no se hace todav_a.
	 *  El metodo cerrar se debe llamar expl_citamente sobre los objetos JDBCWrapper
	 *   en la cl_usula final. 
	 */
	protected void finalize() throws Exception {
		if (m_Connection != null) {
			log.debug( "finalize(): the connection have not been liberated");
		}
		close();
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

//	/** Recupera el DataSource de la cache para mejorar el funcionamiento. */
//	abstract DataSource getDataSource() throws Exception;

	/**
	 * Obtiene la fecha de un campo mediante el m_todo getTimeStamp.
	 * El tipo que devuelve es java.util.Date
	 * El metodo que lo llame podr_ hacer casting a java.sql.timestamp
	 * @param champ	field name
	 * @return 	field value
	 * @exception Exception
	 */
	public java.util.Date getDateTimeStamp(String champ) throws Exception {
		try {
			return m_Rset.getTimestamp(champ);
		}
		catch (Exception e) {
			String message = MessageFormat.format(
					"Error de lectura sobre el campo [{0}].",
					new Object[] {champ});
			log.error( message+" "+ e.toString());
			throw new Exception(message +" "+ e.toString());
		}
	}

	/**
	 * Obtiene la fecha de un campo mediante el m_todo getTimeStamp.
	 * El tipo que devuelve es java.util.Date
	 * El metodo que lo llame lo llame podr_ hacer casting a java.sql.Date
	 * @param champ field name
	 * @return 	field value
	 * @exception Exception Si hay algun error leyendo el Resulset
	 */
	public java.util.Date getDate(String champ) throws Exception {
		   try {
        return m_Rset.getDate(champ);
      }
      catch (Exception e) {
        String message = MessageFormat.format(
            "Error de lectura sobre el campo [{0}].",
            new Object[] {champ});
        log.error( message+" "+ e.toString());
        throw new Exception(message +" "+ e.toString());
      }
		
	}

	/**
	 * Obtiene el valor de un campo de tipo double
	 * @param champ field name
	 * @return 	field value
	 * @exception Exception Si hay algun error leyendo el Resulset
	 */
	public double getDouble(String champ) throws Exception {
		try {
			return m_Rset.getDouble(champ);
		}
		catch (Exception e) {
			String message = MessageFormat.format(
					"Error de lectura sobre el campo [{0}].",
					new Object[] {champ});
			log.error( message+" "+ e.toString());
			throw new Exception(message +" "+ e.toString());
		}
	}

	/**
	 * Obtiene el valor de un campo de tipo int
	 * @param champ field name
	 * @return 	field value
	 * @exception Exception Si hay algun error leyendo el Resulset
	 */
	public int getInt(String champ) throws Exception {
		try {
			return m_Rset.getInt(champ);
		}
		catch (Exception e) {
			String message = MessageFormat.format(
					"Error de lectura sobre el campo [{0}].",
					new Object[] {champ});
			log.error( message+" "+ e.toString());
			throw new Exception(message +" "+ e.toString());
		}
	}

	/**
	 * Obtiene el valor de un campo de tipo Long
	 * @param champ field name
	 * @return 	field value
	 * @exception Exception Si hay algun error leyendo el Resulset
	 */
	public long getLong(String champ) throws Exception {
		try {
			return m_Rset.getLong(champ);
		}
		catch (Exception e) {
			log.error( "Error al recuperar el campo long " + champ+ e.toString());
			throw new Exception("Error de lectura sobre el campo [{0}]." + champ + " " +
					e.toString());
		}
	}

	/**
	 * Obtiene el valor de un campo de tipo Object
	 * @param champ field name
	 * @return 	field value
	 * @exception Exception Si hay algun error leyendo el Resulset
	 */
	public Object getObject(String champ) throws Exception {
		try {
			return m_Rset.getObject(champ);
		}
		catch (Exception e) {
			log.error( "Error al recuperar e campo object " + champ+ e.toString());
			throw new Exception("Error.JDBCWrapper.LectureChamp " + champ + " " + 
					e.toString());
		}
	}

	/**
	 * Obtiene el valor de un campo de tipo String
	 * @param champ field name
	 * @return 	field value
	 * @exception Exception Si hay algun error leyendo el Resulset
	 */
	public String getString(String champ) throws Exception {
		String str="";
		try {
			str=m_Rset.getString(champ);
			if(str==null || str.equals("0000-00-00 00:00:00") || str.equals("")) return "";
			return str;
		}
		catch (Exception e) {
			log.error( "Error al recuperar el campo string " + champ+" "+ e.toString());
			throw new Exception("Error.JDBCWrapper.LectureChamp " + champ + " " +
					e.toString());
		}
	}

	/**
	 * Obtiene el valor de un campo de tipo Date
	 * @param champ field name
	 * @return 	field value
	 * @exception Exception Si hay algun error leyendo el Resulset
	 */
	public java.util.Date getTimestamp(String champ) throws Exception {
		try {
			return m_Rset.getTimestamp(champ);
		}
		catch (Exception e) {
			log.error( "Error al recuperar el campo timestamp " + champ+ e.toString());
			throw new Exception("Error.JDBCWrapper.LectureChamp " + champ + " " +
					e.toString());
		}
	}

	/**
	 * Obtiene el valor de un campo de tipo InputStream
	 * @param champ field name
	 * @return 	field value
	 * @exception Exception Si hay algun error leyendo el Resulset
	 */
	public java.io.InputStream getBinaryStream(String champ) throws Exception {
		try {
			return m_Rset.getBinaryStream(champ);
		}
		catch (Exception e) {
			log.error("Error al recuperar el campo timestamp " + champ+" "+ e.toString());
			throw new Exception("Error.JDBCWrapper.LectureChamp " + champ + " " +
					e.toString());
		}
	}

	/**
	 * Obtiene el valor de un campo de tipo Blob
	 * @param champ field name
	 * @return 	field value
	 * @exception Exception Si hay algun error leyendo el Resulset
	 */
	public java.sql.Blob getBlob(String champ) throws Exception {
		try {
			return m_Rset.getBlob(champ);
		}
		catch (Exception e) {
			log.error( "Error al recuperar el campo blob " + champ+ e.toString());
			throw new Exception("Error.JDBCWrapper.LectureChamp " + champ + " " +
					e.toString());
		}
	}

	/**
	 * Obtiene el valor de un campo de tipo Clob
	 * @param champ field name
	 * @return 	field value
	 * @exception Exception Si hay algun error leyendo el Resulset
	 */
	public String getClob(String champ) throws Exception {
		try {
			if (m_Rset.getClob(champ) != null)
				return m_Rset.getClob(champ).getSubString(1, new Integer("" + m_Rset.getClob(champ).length()).intValue());
			else return "";
		}
		catch (Exception e) {
			log.error( "Error al recuperar el campo CLOB " + champ+" "+ e.toString());
			throw new Exception("Error.JDBCWrapper.LectureChamp " + champ + " " +
					e.toString());
		}
	}


	/**
	 * Navega a traves del ResulSet. Va al siguiente elemento.
	 * @return True si hay un elemento siguiente, sino false
	 * @exception Exception
	 */
	public boolean next() throws Exception {

		boolean booleanRetour = false;
		try {
			booleanRetour = m_Rset.next();
		}
		catch (SQLException e) {
			String message = e.getMessage();
			log.error( "Error al recuperar el siguiente elemento"+ message);
			if (message.indexOf("ORA-08177") != -1) {
				log.debug("ORA-08177 up: request re-executed over JDBCWrapper.next()");
				int countdown = 5;
				boolean requete_executee = false;
				while ( (countdown != 0) && (requete_executee == false)) {
					try {
						countdown--;
						booleanRetour = m_Rset.next();
						requete_executee = true;
					}
					catch (Exception error) {
						String messageb = error.getMessage();
						if (messageb.indexOf("ORA-08177") != -1) {
							log.error( "New try"+ messageb);
							requete_executee = false;
						}
						else {
							log.debug( "Error.JDBCWrapper.ResultatSQL"+error.toString());
							throw new Exception("Error.JDBCWrapper.ResultatSQL " + 
									error.toString());
						}
					}
				}
				if (requete_executee == false) {
					log.debug( "Error.JDBCWrapper.ResultatSQL"+" "+ e.toString());
					throw new Exception("Error.JDBCWrapper.ResultatSQL " + e.toString());
				}
			}
			else {
				log.debug( "Erreur.JDBCWrapper.ResultatSQL"+" "+ e.toString());
				throw new Exception("Error.JDBCWrapper.ResultatSQL " + e.toString());
			}
		}
		catch (Exception e) {
			log.error( "Error.JDBCWrapper.ResultatSQL"+" "+ e.toString());
			throw new Exception("Error.JDBCWrapper.ResultatSQL" + e.toString());
		}
		return booleanRetour;
	}

	@SuppressWarnings("unchecked")
	private String paramToString(Vector v){
		String ret="#";
		if(v==null)return ret;
		for (Enumeration e = v.elements() ; e.hasMoreElements() ;) {
	         ret+=e.nextElement()+"#";
	     }
		return ret;
	}
	
	
	

}
