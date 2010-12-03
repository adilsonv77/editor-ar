package org.furb.arbuilder;

import org.furb.arbuilder.elementos.Vertice;

/**
 *
 * @author Jonathan Hess, André R. Sousa
 */
public class Aresta {

    private Vertice v1;
    private Vertice v2;

    public Aresta(Vertice v1, Vertice v2){
        this.v1 = v1;
        this.v2 = v2;
    }

    //Analisa se a aresta esta entre dois vertices. (Nao importa a ordem que eles sao passados)
    public final boolean contemVertices(Vertice v1, Vertice v2){
        if((this.v1==v1)&&(this.v2==v2)) return(true);
        if((this.v1==v2)&&(this.v2==v1)) return(true);
        return(false);
    }
    
    //Analisa se a aresta contem o Vertice em questo
    public final boolean contemVertice(Vertice v){
        if(this.v1==v) return(true);
        if(this.v2==v) return(true);

        return(false);
    }

    //Retorna o vertice 1 da aresta
    public final Vertice getVertice1(){
        return v1;
    }

    //Retorna o vertice 2 da aresta
    public final Vertice getVertice2(){
        return v2;
    }

    //Atribui um outro vertice a aresta
    public final void setVertice1(Vertice v1){
        this.v1 = v1;
    }

    //Atribui um outro vertice a aresta
    public final void setVertice2(Vertice v2){
        this.v2 = v2;
    }
    
    @Override
    public final String toString() {
        return "";
    }
}