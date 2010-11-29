package org.furb.arbuilder.elementos;

/**
 * 
 * @author Luciana Pereira de Araújo
 * 2010
 */

public class Distinct extends Operador {

	private String entrada;

	public Distinct(String nome) {
		super(nome);
	}

	public final String getEntrada() {
		return entrada;
	}

	public final void setEntrada(Vertice v) {
		entrada = v.toString();
	}

	@Override
	public final String toString() {
		return super.getNome();

	}
}
