/**
 * 
 */
package org.furb.arbuilder.elementos;

/**
 * @author Luciana Pereira de Ara�jo
 * 2010
 *
 */
public class JuncaoNatural extends Operador{

	public JuncaoNatural(String nome) {
		super(nome);
	}
	
	@Override
	public final String toString() {
		return super.getNome();

	}
}
