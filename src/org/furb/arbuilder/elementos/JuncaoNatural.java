/**
 * 
 */
package org.furb.arbuilder.elementos;

/**
 * @author Luciana Pereira de Araújo 2010
 * 
 */
public class JuncaoNatural extends Operador {
	private String parametro;

	public JuncaoNatural(String nome) {
		super(nome);
	}

	public JuncaoNatural(String nome, String parametro) {
		super(nome);
	}

	public final void setParametro(String parametro) {
		this.parametro = parametro;
	}

	public final String getParametro() {
		return this.parametro;
	}

	@Override
	public final String toString() {
		if (this.parametro != null) {
			System.out.println(super.getNome() + " " + this.parametro);
			return super.getNome() + " " + this.parametro;
		} else {
			return super.getNome();
		}
	}
}
