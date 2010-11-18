package org.furb.arbuilder;

public enum Operadores {

	SELECAO					("Selecao"				, 0 , "/org/furb/arbuilder/resources/images/op_selecao.png" 			),
	PROJECAO				("Projecao"				, 1 , "/org/furb/arbuilder/resources/images/op_projecao.png"			),
	UNIAO					("Uniao"				, 2 , "/org/furb/arbuilder/resources/images/op_uniao.png"				),
	PRODUTO_CARTESIANO		("ProdutoCartesiano"	, 3 , "/org/furb/arbuilder/resources/images/op_produto_cartesiano.png"	),
	AGRUPAMENTO				("Agrupamento"			, 4 , "/org/furb/arbuilder/resources/images/op_agrupamento.png"			),
	DIFERENCA				("Diferenca"			, 5 , "/org/furb/arbuilder/resources/images/op_diferenca.png"			),
	ORDENACAO				("Ordenacao"			, 6 , "/org/furb/arbuilder/resources/images/op_ordenacao.png"			),
	JUNCAO_EX_ESQUERDA		("JuncaoExEsquerda"		, 7 , "/org/furb/arbuilder/resources/images/op_juncao.png"				),
	DISTINCT				("Distinct"  			, 8 , "/org/furb/arbuilder/resources/images/op_distinct.png"			), 
	JUNCAO_NATURAL            ("JuncaoNatural"			, 9 , "/org/furb/arbuilder/resources/images/op_juncaoNatural.png"			);
	
	
	private String operador;
	private int type;
	private String image;
	
	private Operadores( String operador , int type , String image ) {
		this.operador = operador;
		this.type = type;
		this.image = image;
	}
	
	/**
	 * Retorna o tipo do operado com base
	 * no objeto GraficoOperador
	 * @param go
	 * @return
	 */
	public static int getOperadorType( GraficoOperador go ) 
	{
		for( Operadores op : Operadores.values() ) {
			if( go.toString().equals( op.getOperador() ) ) {
				return op.getType();
			}
		}
		
		return -1;
	}
	
	/**
	 * Retorna a imagem do operador com base no
	 * seu nome
	 * @param operador
	 * @return
	 */
	public static String getImageForOparador( String operador )
	{
		for( Operadores op : Operadores.values() ) {
			if( operador.contains( op.getOperador() ) ) {
				return op.getImage();
			}
		}
		
		return "";
	}

	/**
	 * @return the operador
	 */
	public final String getOperador() {
		return operador;
	}

	/**
	 * @param operador the operador to set
	 */
	public final void setOperador(String operador) {
		this.operador = operador;
	}

	/**
	 * @return the type
	 */
	public final int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public final void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the image
	 */
	public final String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public final void setImage(String image) {
		this.image = image;
	}
}
