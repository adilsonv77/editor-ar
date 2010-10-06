package org.furb.arbuilder.util;

import java.util.ArrayList;
import java.util.List;

import org.furb.arbuilder.elementos.tabela.Coluna;
import org.furb.arbuilder.elementos.tabela.Tabela;

public final class AliasHelper {

	private static final long serialVersionUID = 8336757994006370162L;
	
	private List<Tabela> aliasStore = null;
	private static AliasHelper alias = new AliasHelper();
	
	private String lastOperator = "";
	private String currentOperator = "";
	
	private AliasHelper() {
		super();
		aliasStore = new ArrayList<Tabela>();
	}
	
	/**
	 * Retorna uma instancia de AliasHelper
	 * @return
	 */
	public static AliasHelper getInstance() {
		
		if( alias == null ) {
			alias = new AliasHelper();
		}
		
		return alias;
	}
	
	/**
	 * Metodo utilizado para criar um novo objeto
	 * tabela, com base em outra tabela ja existente
	 * @param t1 Tabela 
	 * @return Tabela
	 */
	public Tabela getNewTableFrom( Tabela t1 )
	{
		Tabela nT1 = new Tabela( t1.getNome() );
		
        for( Coluna nT1c : t1.getColunas() ) {
        	Coluna nT1c1 = nT1c.clone();
        	nT1c1.setUniqueTable( String.valueOf( nT1.getUniqueId() ) );
        	nT1.getColunas().add( nT1c1 );
        }
        
        return nT1;
	}
	
	/**
	 * Metodo utilizado para criar um novo objeto
	 * tabela, com base em duas tabelas ja existentes,
	 * normalmente utilizado para criar um select pai
	 * para sub-querys
	 * @param t1 Tabela
	 * @param t2 Tabela
	 * @return Tabela
	 */
	public Tabela getNewTableFromGroup( Tabela t1 , Tabela t2 )
	{
		Tabela nT3 = new Tabela("AUX");
		
        for( Coluna nT1c : t1.getColunas() ) 
        {
        	Coluna nT3c3 = nT1c.clone();
        	if( !nT3c3.getAliasColuna().trim().isEmpty() ) {
        		nT3c3.setNmColuna( nT1c.getAliasColuna() );
        		nT3c3.setAliasColuna( nT3.getUniqueId() + nT1c.getAliasColuna() );
        	}
        	nT3c3.setUniqueTable( String.valueOf( nT3.getUniqueId() ) );
        	nT3.getColunas().add( nT3c3 );
        }
        
        for( Coluna nT2c : t2.getColunas() ) 
        {
        	Coluna nT3c3 = nT2c.clone();
        	if( !nT3c3.getAliasColuna().trim().isEmpty() ) {
        		nT3c3.setNmColuna( nT2c.getAliasColuna() );
        		nT3c3.setAliasColuna( nT3.getUniqueId() + nT2c.getAliasColuna() );
        	}
        	nT3c3.setUniqueTable( String.valueOf( nT3.getUniqueId() ) );
        	nT3.getColunas().add( nT3c3 );
        }
        
        return nT3;
	}
	
	/**
	 * Método utilizado para verificação da necessidade
	 * de se renomear colunas com o mesmo nome, para evitar
	 * problema de coluna duplicada
	 * @param t1 Tabela
	 * @param t2 Tabela
	 */
	public void checkAlias( Tabela t1 , Tabela t2 ) 
	{
		for( Coluna c1 : t1.getColunas() ) 
		{
			for( Coluna c2 : t2.getColunas() ) 
			{
				if( c1.getNmRealColuna().equals( c2.getNmRealColuna() ) ) 
				{
					if( !c1.getAliasColuna().trim().isEmpty() ) {
						c1.setNmColuna( c1.getAliasColuna() );
					}
					else if( !c2.getAliasColuna().trim().isEmpty() ) {
						c2.setNmColuna( c2.getAliasColuna() );
					} 
					
					c1.setAliasColuna( c1.getUniqueTable() + c1.getNmColuna() );
					c2.setAliasColuna( c2.getUniqueTable() + c2.getNmColuna() );
				}
			}
		}
		
		this.checkSingleAlias(t1);
		this.checkSingleAlias(t2);
	}
	
	/**
	 * Método utilizado para verificação da necessidade
	 * de se renomear colunas com o mesmo nome, para evitar
	 * problema de coluna duplicada
	 * @param t1 Tabela
	 */
	public void checkSingleAlias( Tabela t1 )
	{
		for( Coluna c1 : t1.getColunas() ) 
		{
			if( !c1.getAliasColuna().trim().isEmpty() ) {
				String checkAliasC1 = c1.getUniqueTable() + c1.getNmColuna();
				if( !c1.getAliasColuna().equals(checkAliasC1) ) {
					c1.setNmColuna( c1.getAliasColuna() );
					c1.setAliasColuna( c1.getUniqueTable() + c1.getNmColuna() );
				}
			}
		}
	}
	
	/**
	 * Método responsavel por criar um alias
	 * para od ID's de cada tabela.
	 * @param query
	 * @param clearParams
	 * @return
	 */
	public String makeAlias( String query , boolean clearParams ) 
	{
		int idx = 1;
		String newSql = query;
		
		for( Tabela tab : aliasStore )
		{
			newSql = newSql.replaceAll( String.valueOf( tab.getUniqueId() ) , "R" + idx );
			newSql = newSql.replaceAll( tab.getNome() + "\\." , "R" + idx + "\\." );
			idx ++;
		}
		
		if( clearParams ) {
			clearAlias();
		}
		
		return newSql;
	}
	
	/**
	 * Insere uma tabela na lista de Tabelas,
	 * que sera utilizada posteriormente para
	 * gerar os aliases
	 * @param value
	 */
	public void put(Tabela value) {
		aliasStore.add( value );
	}
	
	/**
	 * Retorna a ultima tabela inseria na lista
	 * de tabelas.
	 * @return
	 */
	public Tabela getLast() throws Exception {
		
		if(  aliasStore.isEmpty() ) {
			throw new Exception();
		}
		
		int i = 1;
		
		while( aliasStore.isEmpty() || aliasStore.get( aliasStore.size() - i ).isAutoExclude() ) {
			i++;
		}
		
		return aliasStore.get( aliasStore.size() - i );
	}
	
	public Tabela getPenultimate() {
		return aliasStore.get( aliasStore.size() -2 );
	}

	public List<Tabela> getAliasStore() {
		return aliasStore;
	}

	public void clearAlias() {
		aliasStore.clear();
		lastOperator = "";
		currentOperator = "";
	}

	public String getLastOperator() {
		return lastOperator;
	}

	private void setLastOperator(String lastOperator) {

		this.lastOperator = lastOperator;
	}

	public String getCurrentOperator() {
		return currentOperator;
	}

	public void setCurrentOperator(String currentOperator) {
		
		if( this.lastOperator.isEmpty() ) {
			setLastOperator( currentOperator );
		} else {
			setLastOperator( this.currentOperator );
		}

		this.currentOperator = currentOperator;
	}
}
