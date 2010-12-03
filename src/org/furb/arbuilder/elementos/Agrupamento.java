package org.furb.arbuilder.elementos;


/**
 *
 * @author Jonathan Hess, André R. Sousa
 */
public class Agrupamento extends Vertice {

    private String colunasAgrupadoras;
    private String colunasProjetadas;

    public Agrupamento(String nome) {
        super(nome);
    }

    public Agrupamento(String nome, String colunasAgrupadoras, String colunasProjetadas) {
        super(nome);
        this.colunasAgrupadoras = colunasAgrupadoras;
        this.colunasProjetadas = colunasProjetadas;
    }

    public final String getColunasAgrupadoras() {
        return colunasAgrupadoras;
    }

    public final void setColunasAgrupadoras(String colunasAgrupadoras) {
        this.colunasAgrupadoras = colunasAgrupadoras;
    }

    public final String getColunasProjetadas() {
        return colunasProjetadas;
    }

    public final void setColunasProjetadas(String colunasProjetadas) {
        this.colunasProjetadas = colunasProjetadas;
    }

    @Override
    public final String toString() {
        if(this.colunasAgrupadoras != null) {
            if(this.colunasProjetadas != null) {
                return this.colunasAgrupadoras + " " + super.getNome() + " " + this.colunasProjetadas;
            }
            return this.colunasAgrupadoras + " " + super.getNome();
        }
        else {
            if(this.colunasProjetadas != null) {
                return super.getNome() + " " + this.colunasProjetadas;
            }
            return super.getNome();
        }
    }

}