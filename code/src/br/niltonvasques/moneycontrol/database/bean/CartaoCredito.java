package br.niltonvasques.moneycontrol.database.bean;

public class CartaoCredito {
	
	private int id;
	private float limite;
	private int dia_vencimento;
	private int dia_fechamento;
	private int id_Conta;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public float getLimite() {
		return limite;
	}
	public void setLimite(float limite) {
		this.limite = limite;
	}
	public int getDia_vencimento() {
		return dia_vencimento;
	}
	public void setDia_vencimento(int dia_vencimento) {
		this.dia_vencimento = dia_vencimento;
	}
	public int getDia_fechamento() {
		return dia_fechamento;
	}
	public void setDia_fechamento(int dia_fechamento) {
		this.dia_fechamento = dia_fechamento;
	}
	public int getId_Conta() {
		return id_Conta;
	}
	public void setId_Conta(int id_Conta) {
		this.id_Conta = id_Conta;
	}
	
	

}
