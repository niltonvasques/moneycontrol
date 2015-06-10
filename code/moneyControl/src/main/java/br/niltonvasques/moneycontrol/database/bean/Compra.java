package br.niltonvasques.moneycontrol.database.bean;

public class Compra {

	private int id;
	private float valor;
	private String data;
	private int parcelas;
	private int id_Conta;
	private String descricao;
	private int id_CategoriaTransacao;

	
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

	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getParcelas() {
		return parcelas;
	}

	public void setParcelas(int parcelas) {
		this.parcelas = parcelas;
	}

	public int getId_CategoriaTransacao() {
		return id_CategoriaTransacao;
	}

	public void setId_CategoriaTransacao(int id_CategoriaTransacao) {
		this.id_CategoriaTransacao = id_CategoriaTransacao;
	}
}
