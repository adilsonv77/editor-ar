package org.furb.arbuilder;

import org.furb.arbuilder.elementos.Vertice;

/**
 *
 * @author Jonathan Hess, André R. Sousa
 */
public class GraficoOperador {

    private String nomeOperador;
    private Vertice operador;
    private String parametro1;
    private String parametro2;
    private int x;
    private int y;

    public GraficoOperador(String nomeOperador, Vertice operador) {
        this.nomeOperador = nomeOperador;
        this.operador = operador;
    }
    
    public GraficoOperador(String nomeOperador, Vertice operador, String parametro1, String parametro2, int x, int y) {
        this.nomeOperador = nomeOperador;
        this.operador     = operador;
        this.parametro1   = parametro1;
        this.parametro2   = parametro2;
        this.x = x;
        this.y = y;
    }
    
    public final Vertice getOperador() {
        return this.operador;
    }
    
    public final String getParametro(int parametroNumero) {
        switch(parametroNumero) {
            case 1: return this.parametro1;
            case 2: return this.parametro2;
        }
        return "";
    }
    
    public final int getX() {
        return this.x;
    }
    
    public final int getY() {
        return this.y;
    }
    
    @Override
    public final String toString() {
        return this.nomeOperador;
    }
}
