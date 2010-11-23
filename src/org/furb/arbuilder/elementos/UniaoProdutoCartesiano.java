package org.furb.arbuilder.elementos;


/**
 *
 * @author Jonathan Hess, André R. Sousa
 */

public class UniaoProdutoCartesiano extends Operador {
	
    private String entrada1;
    private String entrada2;

    public UniaoProdutoCartesiano(String nome) {
        super(nome);
    }

    public final void setEntrada1(Vertice v) {
        entrada1 = v.toString();
    }

    public final void setEntrada2(Vertice v) {
        entrada2 = v.toString();
    }

    @Override
    public final String toString() {
        if(this.entrada1 != null && this.entrada2 != null) {
            return "(" + entrada1 + ") " + super.getNome() + " (" + entrada2 + ")";
        }
        else {
            return super.getNome();
        }
    }
}
