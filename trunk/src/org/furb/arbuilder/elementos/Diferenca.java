package org.furb.arbuilder.elementos;

public class Diferenca extends Operador {

    private String entrada1;
    private String entrada2;
	
	public Diferenca(String nome) {
		super(nome);
	}

	public void setEntrada1(String entrada1) {
		this.entrada1 = entrada1;
	}

	public void setEntrada2(String entrada2) {
		this.entrada2 = entrada2;
	}
	
    @Override
    public String toString() {
    	
        if(this.entrada1 != null && this.entrada2 != null) {
            return "(" + entrada1 + ") " + super.getNome() + " (" + entrada2 + ")";
        }
        else {
            return super.getNome();
        }
    }
}
