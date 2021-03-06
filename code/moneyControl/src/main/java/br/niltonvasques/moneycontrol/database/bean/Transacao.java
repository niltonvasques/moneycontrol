package br.niltonvasques.moneycontrol.database.bean;

public class Transacao {

	private int id;
	private float valor;
	private String data;
	private int id_Conta;
	private int id_Bem;
	private int id_CategoriaTransacao;
	private String descricao;
	private int id_Compra;
	private boolean executada;
	
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


	public int getId_Compra() {
		return id_Compra;
	}

	public void setId_Compra(int id_Compra) {
		this.id_Compra = id_Compra;
	}

	public boolean isExecutada() {
		return executada;
	}

	public void setExecutada(boolean executada) {
		this.executada = executada;
	}
}
