package br.niltonvasques.moneycontrol.database.bean;

public class CategoriaTransacao implements Comparable<CategoriaTransacao>{

	private int id;
	private String nome;
	private int id_TipoTransacao;
	
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
	public int getId_TipoTransacao() {
		return id_TipoTransacao;
	}
	public void setId_TipoTransacao(int id_TipoTransacao) {
		this.id_TipoTransacao = id_TipoTransacao;
	}
	
	@Override
	public String toString() {
		return nome;
	}
	@Override
	public int compareTo(CategoriaTransacao another) {
		return this.id - another.getId();
	}
	
	
}
