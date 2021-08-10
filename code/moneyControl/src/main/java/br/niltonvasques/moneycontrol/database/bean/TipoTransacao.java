package br.niltonvasques.moneycontrol.database.bean;

public class TipoTransacao {

	public static int CREDITO = 1;
	public static int DEBITO = 2;
	
	private int id;
	private String nome;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	@Override
	public String toString() {
		return nome;
	}
	
	

}
