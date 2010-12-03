package org.furb.arbuilder.elementos.tabela;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.furb.arbuilder.bd.DBUtil;
import org.furb.arbuilder.elementos.Vertice;


/**
 *
 * @author Jonathan Hess, André R. Sousa
 */

public class Tabela extends Vertice {
	
	private List<Coluna> colunas = new ArrayList<Coluna>();
	private List<Coluna> groupBy = new ArrayList<Coluna>();
	
	private long uniqueId = System.nanoTime();
	private boolean autoExclude = false;
	
    public Tabela(String nome) {
        super(nome);
    }
    
    /**
     * Consulta o nome das colunas
     * e seus respectivos tipos de dados
     * suportados
     */
    public final void consultarMetaDados() 
    {
    	Connection conn = null;
    	ResultSet rs = null;
    	Coluna column = null;
    	
    	try {
    		
    		conn = DBUtil.getInstance().getConnection();
    		rs = conn.getMetaData().getColumns( conn.getCatalog() , null , super.getNome() , null );
    		
    		colunas.clear();
    		
    		while( rs.next() ) 
    		{
    			column = new Coluna();
    			column.setNmRealColuna( rs.getString("COLUMN_NAME") );
    			column.setNmColuna( rs.getString("COLUMN_NAME") );
    			column.setTpColuna( rs.getString("TYPE_NAME") );
    			colunas.add( column );
    		}
    		
    	} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			DBUtil.getInstance().doFinallyClose(conn, null, rs);
		}
    }
    
    public final String getColumnNames() {
    	
    	consultarMetaDados();
    	StringBuilder retString = new StringBuilder();
    	
    	for( Coluna c : getColunas() )
    	{
    		retString.append( getUniqueId() );
    		retString.append( "." );
    		retString.append( c.getNmColuna() );
    		retString.append(", ");
    		
    		c.setUniqueTable( String.valueOf( getUniqueId() ) );
    		c.setNmTabela( getNome() );
    	}
    	
    	return retString.toString().substring( 0 , retString.toString().length() - 2 );
    }
    
    public final String getParameters() {

    	StringBuilder retString = new StringBuilder();
    	
    	for( Coluna c : getColunas() )
    	{
    		if( c.getFuncaoAgrupadora().trim().isEmpty() ) {
	    		retString.append( getUniqueId() );
	    		retString.append( "." );
	    		retString.append( c.getNmColuna() );
    		} else {
    			retString.append( c.getFuncaoAgrupadora() );
    			retString.append("(");
	    		retString.append( getUniqueId() );
	    		retString.append( "." );
	    		retString.append( c.getNmColuna() );  
	    		retString.append(")");
    		}
    		
    		if( !c.getAliasColuna().trim().isEmpty() ) {
        		retString.append(" AS ");
        		retString.append( c.getAliasColuna() );
    		}
    		
    		retString.append(", ");
    	}
    	
    	return retString.toString().substring( 0 , retString.toString().length() - 2 );
    }
    
    public final String getParametersWithoutAs() {

    	StringBuilder retString = new StringBuilder();
    	
    	for( Coluna c : getColunas() )
    	{
    		retString.append( getUniqueId() );
    		retString.append( "." );
    		retString.append( c.getNmColuna() );
    		retString.append(", ");
    	}

    	return retString.toString().substring( 0 , retString.toString().length() - 2 );
    }
    
    public final String getGroupByParameters() {

    	StringBuilder retString = new StringBuilder();
    	
    	for( Coluna c : getGroupBy() )
    	{
    		retString.append( getUniqueId() );
    		retString.append( "." );
    		retString.append( c.getNmColuna() );
    		retString.append(", ");
    	}
    	
    	return retString.toString().substring( 0 , retString.toString().length() - 2 );
    }   
    
    @Override
    public final String toString() {
        return super.getNome();
    }

	/**
	 * @return the colunas
	 */
	public final List<Coluna> getColunas() {
		return colunas;
	}

	/**
	 * @param colunas the colunas to set
	 */
	public final void setColunas(List<Coluna> colunas) {
		this.colunas = colunas;
	}

	/**
	 * @return the uniqueId
	 */
	public final long getUniqueId() {
		return uniqueId;
	}

	/**
	 * @param uniqueId the uniqueId to set
	 */
	public final void setUniqueId(long uniqueId) {
		this.uniqueId = uniqueId;
	}

	/**
	 * @return the autoExclude
	 */
	public final boolean isAutoExclude() {
		return autoExclude;
	}

	/**
	 * @param autoExclude the autoExclude to set
	 */
	public final void setAutoExclude(boolean autoExclude) {
		this.autoExclude = autoExclude;
	}

	/**
	 * @return the groupBy
	 */
	public final List<Coluna> getGroupBy() {
		return groupBy;
	}

	/**
	 * @param groupBy the groupBy to set
	 */
	public final void setGroupBy(List<Coluna> groupBy) {
		this.groupBy = groupBy;
	}
}
