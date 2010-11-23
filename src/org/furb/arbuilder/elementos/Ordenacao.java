package org.furb.arbuilder.elementos;

public class Ordenacao extends Operador {

    private String parametro;
    private String entrada;
	
	public Ordenacao(String nome) {
		super(nome);
	}
	
	public Ordenacao(String nome, String parametro ) {
		super(nome);
		this.parametro = parametro;
	}

	public final String getParametro() {
		return parametro;
	}

	public final void setParametro(String parametro) {
		this.parametro = parametro;
	}

	public final String getEntrada() {
		return entrada;
	}

    public final void setEntrada(Vertice v) {
        entrada = v.toString();
    }
	
    @Override
    public final String toString() {
        if(this.parametro != null) {
            return super.getNome() + " " + this.parametro;
        }
        else {
            return super.getNome();
        }
    }
}
