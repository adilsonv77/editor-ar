package org.furb.arbuilder.elementos;

public class Interseccao extends Operador {

	private String parametro;
	
	public Interseccao(String nome) {
		super(nome);
	}
	
	public final void setParametro(String parametro) {
		this.parametro = parametro;
	}

	public final String getParametro() {
		return this.parametro;
	}

	@Override
	public final String toString() {
		if (this.parametro != null) {
			System.out.println(super.getNome() + " " + this.parametro);
			return super.getNome() + " " + this.parametro;
		} else {
			return super.getNome();
		}
	}

}
