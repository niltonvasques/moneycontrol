package br.niltonvasques.moneycontrol.database.bean;

public class RentabilidadeAtivo {

	private int id;
	private String data;
	private float valor;
	private int id_Ativo;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public float getValor() {
		return valor;
	}
	public void setValor(float valor) {
		this.valor = valor;
	}
	public int getId_Ativo() {
		return id_Ativo;
	}
	public void setId_Ativo(int id_Ativo) {
		this.id_Ativo = id_Ativo;
	}
	
	
}
