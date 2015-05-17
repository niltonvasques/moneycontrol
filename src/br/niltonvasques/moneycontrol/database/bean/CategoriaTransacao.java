package br.niltonvasques.moneycontrol.database.bean;

public class CategoriaTransacao implements Comparable<CategoriaTransacao>{

	private int id;
	private String nome;
	private int id_TipoTransacao;
	private boolean system;
	
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
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof CategoriaTransacao){
			if(((CategoriaTransacao)o).getId() == id) return true;
		}
		return false;
	}
	public boolean isSystem() {
		return system;
	}
	public void setSystem(boolean system) {
		this.system = system;
	}
	
	
}
