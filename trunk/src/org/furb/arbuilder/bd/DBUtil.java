package org.furb.arbuilder.bd;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public final class DBUtil {
	
	private String dbDriver		= "";
	private String dbUrl		= "";
	private String dbUser		= "";
	private String dbPass		= "";
	private boolean canConnect	= false;	//Indica se pode se conectar
	
	private static DBUtil instance = new DBUtil();
	
	private DBUtil() {
		super();
	}
	
	/**
	 * Retorna uma instancia da classe
	 * @return
	 */
	public static DBUtil getInstance()
	{
		if( instance == null )
		{
			instance = new DBUtil();
		}
		
		return instance;
	}
	
	/**
	 * Retorna uma instancia da classe,
	 * inicializando-a com os novos parametros
	 * @param params
	 * @return
	 */
	public static DBUtil getInstance( String[] params )
	{
		initParameters(params);
		return instance;
	}
	
	private static void initParameters( String[] params )
	{
		int idx = 0;
		instance.dbDriver	= params[idx++];
		instance.dbUrl		= params[idx++];
		instance.dbUser		= params[idx++];
		instance.dbPass		= params[idx++];
		instance.canConnect = true;
	}
	
	/**
	 * Instancia um novo objeto Connection
	 * @return
	 */
	public Connection getConnection() throws SQLException
	{
		Connection conn = null;
		
		try
		{
			Class.forName(dbDriver);
			conn = DriverManager.getConnection(dbUrl,dbUser,dbPass);
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
		
		return conn;
	}
	
	/**
	 * Fecha a conexao dos objetos ResultSet, Statement
	 * e Connection passados por parametro
	 * @param conn
	 * @param st
	 * @param rs
	 */
	public void doFinallyClose(Connection conn, Statement st, ResultSet rs)
	{
		this.closeResultSet(rs);
		this.closeStatement(st);
		this.closeConnection(conn);
	}
	
	/**
	 * Fecha a conexao do objeto Connection
	 * @param conn
	 */
	public void closeConnection(Connection conn)
	{
		try 
		{ 
			if( conn != null ) 
			{
				conn.close();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Fecha a conexao do objeto Statement
	 * @param st
	 */
	public void closeStatement(Statement st)
	{
		try 
		{ 
			if( st != null ) 
			{
				st.close();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Fecha a conexao do objeto ResultSet
	 * @param rs
	 */
	public void closeResultSet(ResultSet rs)
	{
		try 
		{ 
			if( rs != null ) 
			{
				rs.close();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the dbDriver
	 */
	public String getDbDriver() {
		return dbDriver;
	}

	/**
	 * @param dbDriver the dbDriver to set
	 */
	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}

	/**
	 * @return the dbUrl
	 */
	public String getDbUrl() {
		return dbUrl;
	}

	/**
	 * @param dbUrl the dbUrl to set
	 */
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	/**
	 * @return the dbUser
	 */
	public String getDbUser() {
		return dbUser;
	}

	/**
	 * @param dbUser the dbUser to set
	 */
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	/**
	 * @return the dbPass
	 */
	public String getDbPass() {
		return dbPass;
	}

	/**
	 * @param dbPass the dbPass to set
	 */
	public void setDbPass(String dbPass) {
		this.dbPass = dbPass;
	}

	/**
	 * @return the canConnect
	 */
	public boolean isCanConnect() {
		return canConnect;
	}
}
