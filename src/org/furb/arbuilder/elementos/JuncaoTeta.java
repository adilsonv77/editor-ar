package org.furb.arbuilder.elementos;

/**
 * 
 * @author Jonathan Hess, André R. Sousa
 */

public class JuncaoTeta extends Operador {

    private String parametro;
    private String entrada;

    public JuncaoTeta(String nome) {
        super(nome);
    }

    public JuncaoTeta(String nome, String parametro) {
        super(nome);
        this.parametro = parametro;
    }

    public final void setEntrada(Vertice v) {
        entrada = v.toString();
    }

	public final String getEntrada() {
		return entrada;
	}

	public final void setParametro(String parametro) {
        this.parametro = parametro;
    }
    
    public final String getParametro() {
        return this.parametro;
    }
    
    @Override
	public final String toString() 
    {
        if(this.parametro != null) {
        	System.out.println(super.getNome() + " " + this.parametro);
            return super.getNome() + " " + this.parametro;
        }
        else {
            return super.getNome();
        }
    }
}
