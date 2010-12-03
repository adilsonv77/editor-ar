package org.furb.arbuilder.elementos;

/**
 *
 * @author Jonathan Hess, André R. Sousa
 */

public class Vertice {
	
    private String nome;
    private int index;

    public Vertice(String nome) {
        this.nome = nome;
    }

    public final String getNome() {
        return nome;
    }

    public final void setNome(String nome) {
        this.nome = nome;
    }

    public final int getIndex() {
        return index;
    }

    public final void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {	
        return this.nome;
    }
}