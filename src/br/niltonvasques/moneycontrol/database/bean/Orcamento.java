package br.niltonvasques.moneycontrol.database.bean;

public class Orcamento implements Comparable<Orcamento>{

	private int id;
	private String mes;
	private int id_CategoriaTransacao;
	private float valor;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public int getId_CategoriaTransacao() {
		return id_CategoriaTransacao;
	}

	public void setId_CategoriaTransacao(int id_CategoriaTransacao) {
		this.id_CategoriaTransacao = id_CategoriaTransacao;
	}

	public float getValor() {
		return valor;
	}

	public void setValor(float valor) {
		this.valor = valor;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Orcamento){
			if(((Orcamento)o).getId() == id) return true;
		}
		return false;
	}

	@Override
	public int compareTo(Orcamento o) {
		if((o).getId() == id) return 0;
		return -1;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	
}
