package br.niltonvasques.moneycontrol.database.bean;

public class Repeticao {

	private int id;
	private String tipo;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNome() {
		return tipo;
	}
	public void setNome(String tipo) {
		this.tipo = tipo;
	}
	
	@Override
	public String toString() {
		return tipo;
	}
	
}
