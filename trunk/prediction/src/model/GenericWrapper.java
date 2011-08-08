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
	
	static 	String CONNECTION_STRING	 =		"jdbc:hsqldb:hsql://localhost/database";//"jdbc:hsqldb:hsql://localhost:9500";//"jdbc:mysql://localhost:3306/wordnet30";
	/** Data source JDBC 2.0 */
	static DataSource m_DataSource;
	static DataSource m_DataSourceXA;
	
//	protected DBValueObject dbvo;

	/** Recordset with all the query data */
	protected ResultSet m_Rset;

//	protected int rowsNumber = 0;
	/** non query result data */
	public int NonQueryResult =0;

	/** SQL Statement */
	protected PreparedStatement m_Prep;


	/** db connection*/
	private Connection m_Connection;

	/** if true the JDBCWrapper already has a connection with the db*/
	protected boolean m_IsConnected = false;

	static String m_dataSourceName;


	public GenericWrapper() throws Exception {
		m_Connection = getConnection();
	}

	/**
	 * For preparing the query execution, closing the Resultset or the PreparedStatement objects
	 * @throws Excepcion
	 */
	void beforeExecute() throws Exception {
		try {
			if (m_Rset != null) {
				m_Rset.close();
				m_Rset = null;
			}
			closePreparedStatement(m_Prep);
		}
		catch (Exception e) {
			String message = "error in recordset initialization, before executing the query";
			throw e;
		}
	}

	/**
	 * closes the connection and the objects (ResultSet, PreparedStatement)
	 * @exception Exception	if any problem during the closing
	 */
	public void close() throws Exception {
		try {
			// Close ResultSet
			if (m_Rset != null) {
				m_Rset.close();
				m_Rset = null;
			}
		
			if (m_Connection != null) {
				m_Connection.close();
				m_Connection = null;
			}
		}
		catch (Exception e) {
			throw e;
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
			throw e;
		}
	}
	

	/** 
	 * Connection is put into auto commit mode 
	 * @exception SQLException
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		try {
			m_Connection.setAutoCommit(autoCommit);
		}
		catch (SQLException e) {
			throw e;
		}
	}


	/**
	 * Query, the resultset is stored in  m_Rset (Resultset).
	 * @param sql quey
	 * @exception Exception
	 */
	public void dmlExecute(String sql) throws Exception {
		try {
			beforeExecute();
			m_Prep = getConnection().prepareStatement(sql);
			m_Prep.execute();
			this.NonQueryResult=1;
		}
		catch (SQLException e) {
			String message = e.getMessage();
			if (message.indexOf("ORA-08177") != -1) {
				int countdown = 5;
				boolean requete_executee = false;
				while ( (countdown != 0) && (requete_executee == false)) {
					try {
						countdown--;
						m_Prep.executeQuery();
						requete_executee = true;
					}
					catch (Exception error) {
						String messageb = error.getMessage();
						if (messageb.indexOf("ORA-08177") != -1) {
							requete_executee = false;
						}
						else {
							throw new Exception("Error during de SQL query." + error.toString());
						}
					}
				}
				if (requete_executee == false) {
					throw new Exception("Connection error during de SQL query." + e.toString());
				}
			}
			else {
				throw new Exception("Connection error during de SQL query" + e.toString());
			}
		}
		catch (Exception e) {
			throw new Exception("Connection error during de SQL query ." + e.toString());
		}
	}
	
	/**
	 * Query, the resultset is stored in  m_Rset (Resultset).
	 * @param sql quey
	 * @exception Exception
	 */
	public void execute(String sql) throws Exception {
		try {
			beforeExecute();
			m_Prep = getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			m_Rset = m_Prep.executeQuery();
//			m_Rset.last();
//			rowsNumber = m_Rset.getRow();
//			m_Rset.first();
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
							requete_executee = false;
						}
						else {
							throw new Exception("Error during de SQL query." + error.toString());
						}
					}
				}
				if (requete_executee == false) {
					throw new Exception("Connection error during de SQL query." + e.toString());
				}
			}
			else {
				throw new Exception("Connection error during de SQL query" + e.toString());
			}
		}
		catch (Exception e) {
			throw new Exception("Connection error during de SQL query ." + e.toString());
		}
	}

	

	/**
	 * Metodo ejecutado antes de eliminar el objeto JDBCWrapper de la memoria
	 * Cierra la conexi_n a la base de datos si no se hace todav_a.
	 *  El metodo cerrar se debe llamar expl_citamente sobre los objetos JDBCWrapper
	 *   en la cl_usula final. 
	 */
	protected void finalize() throws Exception {
		if (m_Connection != null) {
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
//			if(str==null || str.equals("0000-00-00 00:00:00") || str.equals("")) return "";
			return str;
		}
		catch (Exception e) {
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
			if (message.indexOf("ORA-08177") != -1) {
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
							requete_executee = false;
						}
						else {
							throw new Exception("Error.JDBCWrapper.ResultatSQL " + 
									error.toString());
						}
					}
				}
				if (requete_executee == false) {
					throw new Exception("Error.JDBCWrapper.ResultatSQL " + e.toString());
				}
			}
			else {
				throw new Exception("Error.JDBCWrapper.ResultatSQL " + e.toString());
			}
		}
		catch (Exception e) {
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
