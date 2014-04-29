package br.niltonvasques.moneycontrol.database.bean;

public class Ativo {

	private int id;
	private float valor;
	private float quantidade;
	private String data;
	private String vencimento;
	private int id_Conta;
	private int id_TipoAtivo;
	private int id_Transacao;
	private String nome;
	private String sigla;
	
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
	public float getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(float quantidade) {
		this.quantidade = quantidade;
	}
	public String getVencimento() {
		return vencimento;
	}
	public void setVencimento(String vencimento) {
		this.vencimento = vencimento;
	}
	public int getId_TipoAtivo() {
		return id_TipoAtivo;
	}
	public void setId_TipoAtivo(int id_TipoAtivo) {
		this.id_TipoAtivo = id_TipoAtivo;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public int getId_Transacao() {
		return id_Transacao;
	}
	public void setId_Transacao(int id_Transacao) {
		this.id_Transacao = id_Transacao;
	}
	
	
}
