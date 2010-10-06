package org.furb.arbuilder;

import java.awt.Point;

/**
 *
 * @author Jonathan Hess, André R. Sousa
 */
public class GraficoLigacao {
    
    private Point posicaoPai;
    private Point posicaoFilho;
    
    public GraficoLigacao() {
    }
    
    public GraficoLigacao(Point posicaoPai, Point posicaoFilho) {
        this.posicaoPai   = posicaoPai;
        this.posicaoFilho = posicaoFilho;
    }
    
    public final Point getPosicaoPai() {
        return this.posicaoPai;
    }
    
    public final Point getPosicaoFilho() {
        return this.posicaoFilho;
    }

}