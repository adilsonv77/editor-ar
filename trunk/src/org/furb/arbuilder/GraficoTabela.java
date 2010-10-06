package org.furb.arbuilder;

import org.furb.arbuilder.elementos.Vertice;
import org.furb.arbuilder.elementos.tabela.Tabela;

/**
 *
 * @author Jonathan Hess, André R. Sousa
 */
public class GraficoTabela {

    private Vertice tabela;
    private String nomeTabela;
    private int x;
    private int y;
    
    public GraficoTabela(String nomeTabela, Tabela tabela) {
        this.nomeTabela = nomeTabela;
        this.tabela = tabela;
    }
    
    public GraficoTabela(String nomeTabela, Tabela tabela, int x, int y) {
        this.nomeTabela = nomeTabela;
        this.tabela = tabela;
        this.x = x;
        this.y = y;
    }
    
    public final Vertice getTabela() {
        return this.tabela;
    }
    
    public final int getX() {
        return this.x;
    }
    
    public final int getY() {
        return this.y;
    }
    
    @Override
    public final String toString() {
        return this.nomeTabela;
    }

}
