package org.furb.arbuilder;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.furb.arbuilder.bd.DBUtil;
import org.furb.arbuilder.ui.InterfaceResultado;
import org.furb.arbuilder.util.AliasHelper;

/**
 *
 * @author Jonathan Hess, André R. Sousa
 */
public class DAO {
    
    private Connection conn;
    private ArrayList<String> colunas = new ArrayList<String>();
    private ArrayList<String> ultimaConsulta = new ArrayList<String>();
    
    public final boolean conectarBD() throws SQLException 
    {
    	conn = DBUtil.getInstance().getConnection();
        return true;
    }
    
    public final boolean desconectarBD() 
    {
        DBUtil.getInstance().closeConnection(conn);
        return true;
    }
    
    public final boolean executarConsultaBD(String consulta) throws SQLException 
    {
    	Connection con = null;
    	PreparedStatement ps = null;
    	ResultSetMetaData rsMd = null;
    	ResultSet rs = null;
    	
    	try {
    		
    		con = DBUtil.getInstance().getConnection();
    		ps = con.prepareStatement(consulta);
    		rs = ps.executeQuery();
    		rsMd = rs.getMetaData();
    		
            this.colunas = new ArrayList<String>();
            
            for(int i = 1; i <= rsMd.getColumnCount(); i++) {
                this.colunas.add(rsMd.getColumnLabel(i));
            }
            
            this.ultimaConsulta.add(consulta);
            
    	    try {
    	        new InterfaceResultado( getColunas(), rs ).setVisible(true);
    	    } catch (SQLException ex) {
    	        JOptionPane.showMessageDialog(null, "Erro ao tentar mostrar resultado da expressao SQL!\n\n" + ex, "Erro!", JOptionPane.ERROR_MESSAGE);
    	    }
    		
    	} catch ( Exception e ) {
    		JOptionPane.showMessageDialog(null, "Erro ao tentar mostrar resultado da expressao SQL!\n\n" + e, "Erro!", JOptionPane.ERROR_MESSAGE);
		} finally {
			DBUtil.getInstance().doFinallyClose(con, ps, rs);
		}
    	
        return true;
    }
    
    public final ArrayList<String> getColunas() {
        return this.colunas;
    }
    
    public final List<String> getTabelasBD() throws SQLException 
    {
    	List<String> listaTabelas = null;
    	DatabaseMetaData bdMeta = null;
    	ResultSet mRs = null;
    	
    	try {
    		
    		listaTabelas = new ArrayList<String>();
    		
            if( conn == null ) {
                return null;
            }
            
            bdMeta = conn.getMetaData();
            mRs = bdMeta.getTables( conn.getCatalog() , null , null , null );
            
            while( mRs.next() ) 
            {
                listaTabelas.add( mRs.getString("TABLE_NAME") );
            }
    		
    	} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			DBUtil.getInstance().closeResultSet(mRs);
		}
    	
        return listaTabelas;
    }
    
    public final String montarSQlDeAR(String expressaoAR) 
    {
    	String[][] literalReplace = new String[][]{
    			{"@S#ELECT@"	,	"SELECT"},
    			{"@W#HERE@"		,	"WHERE"},
    			{"@G#ROUP BY@"	,	"GROUP BY"},
    			{"@O#RDER BY@"	,	"ORDER BY"},
    			{" <> "," != "},
    			{" \\^ "," AND "},
    			{" v "," OR "},
    			{"@D#ISTINCT@"  ,   "DISTINCT"}
    	};
    	
    	String query = expressaoAR;
    	
    	for( int i = 0; i < literalReplace.length; i++ ) {
    		query = query.replaceAll( literalReplace[i][0] , literalReplace[i][1] );
    	}
    	
    	query = AliasHelper.getInstance().makeAlias( query , true );
    	
        return query;
    }
}
