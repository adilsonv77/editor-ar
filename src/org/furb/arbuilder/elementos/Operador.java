package org.furb.arbuilder.elementos;


/**
 *
 * @author Jonathan Hess, Andr� R. Sousa
 */

public class Operador extends Vertice {
	
    public Operador(String nome) {
        super(nome);
    }

    public String toString() {
        return super.getNome() + "";
    }
}
