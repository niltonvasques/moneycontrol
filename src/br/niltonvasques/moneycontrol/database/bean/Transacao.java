package br.niltonvasques.moneycontrol.database.bean;

public class Transacao {

	private int id;
	private float valor;
	private String data;
	private int id_Conta;
	private int id_Bem;
	private int id_CategoriaTransacao;
	private String descricao;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public float getValor() {
		return valor;
	}
	public void setValor(float valor) {
		this.valor = valor;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public int getId_Conta() {
		return id_Conta;
	}
	public void setId_Conta(int id_Conta) {
		this.id_Conta = id_Conta;
	}
	public int getId_Bem() {
		return id_Bem;
	}
	public void setId_Bem(int id_Bem) {
		this.id_Bem = id_Bem;
	}
	public int getId_CategoriaTransacao() {
		return id_CategoriaTransacao;
	}
	public void setId_CategoriaTransacao(int id_CategoriaTransacao) {
		this.id_CategoriaTransacao = id_CategoriaTransacao;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
}
