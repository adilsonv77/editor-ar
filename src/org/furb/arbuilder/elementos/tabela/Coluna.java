package org.furb.arbuilder.elementos.tabela;

public class Coluna {

	private String nmRealColuna = "";
	private String nmColuna = "";
	private String aliasColuna = "";
	private String tpColuna = "";
	private String uniqueTable = "";
	private String nmTabela = "";
	private String funcaoAgrupadora = "";
	
	public Coluna() {
		super();
	}

	/**
	 * @return the nmColuna
	 */
	public final String getNmColuna() {
		return nmColuna;
	}

	/**
	 * @param nmColuna the nmColuna to set
	 */
	public final void setNmColuna(String nmColuna) {
		this.nmColuna = nmColuna;
	}

	/**
	 * @return the tpColuna
	 */
	public final String getTpColuna() {
		return tpColuna;
	}

	/**
	 * @param tpColuna the tpColuna to set
	 */
	public final void setTpColuna(String tpColuna) {
		this.tpColuna = tpColuna;
	}

	/**
	 * @return the uniqueTable
	 */
	public final String getUniqueTable() {
		return uniqueTable;
	}

	/**
	 * @param uniqueTable the uniqueTable to set
	 */
	public final void setUniqueTable(String uniqueTable) {
		this.uniqueTable = uniqueTable;
	}

	/**
	 * @return the aliasColuna
	 */
	public final String getAliasColuna() {
		return aliasColuna;
	}

	/**
	 * @param aliasColuna the aliasColuna to set
	 */
	public final void setAliasColuna(String aliasColuna) {
		this.aliasColuna = aliasColuna;
	}
	
	/**
	 * @return the nmRealColuna
	 */
	public final String getNmRealColuna() {
		return nmRealColuna;
	}

	/**
	 * @param nmRealColuna the nmRealColuna to set
	 */
	public final void setNmRealColuna(String nmRealColuna) {
		this.nmRealColuna = nmRealColuna;
	}

	/**
	 * @return the nmTabela
	 */
	public final String getNmTabela() {
		return nmTabela;
	}

	/**
	 * @param nmTabela the nmTabela to set
	 */
	public final void setNmTabela(String nmTabela) {
		this.nmTabela = nmTabela;
	}

	/**
	 * @return the funcaoAgrupadora
	 */
	public final String getFuncaoAgrupadora() {
		return funcaoAgrupadora;
	}

	/**
	 * @param funcaoAgrupadora the funcaoAgrupadora to set
	 */
	public final void setFuncaoAgrupadora(String funcaoAgrupadora) {
		this.funcaoAgrupadora = funcaoAgrupadora;
	}

	@Override
	public final String toString() {
		StringBuilder desc = new StringBuilder();
		desc.append( nmRealColuna );
		desc.append(" / ");
		desc.append( tpColuna );
		return desc.toString();
	}
	
	@Override
	public final Coluna clone() {
		Coluna c = new Coluna();
		c.setNmRealColuna( this.nmRealColuna );
		c.setAliasColuna( this.aliasColuna );
		c.setNmColuna( this.nmColuna );
		c.setTpColuna( this.tpColuna );
		c.setUniqueTable( this.uniqueTable );
		c.setNmTabela( this.nmTabela );
		return c;
	}
}
