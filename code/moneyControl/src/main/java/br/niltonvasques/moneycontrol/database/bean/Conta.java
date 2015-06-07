package br.niltonvasques.moneycontrol.database.bean;

/**
 * @author niltonvasques
 *
 */
public class Conta {
	
//CREATE TABLE Conta (
//	id INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,
//	nome VARCHAR(200) NOT NULL  DEFAULT 'NULL',
//	id_TipoConta INTEGER DEFAULT NULL REFERENCES TipoConta (id)
//);
	
	private int id;
	private String nome;
	private float saldo;
	private String icon;	
	private int id_TipoConta;
	
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
	
	public float getSaldo() {
		return saldo;
	}
	public void setSaldo(float saldo) {
		this.saldo = saldo;
	}
	
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public int getId_TipoConta() {
		return id_TipoConta;
	}
	
	public void setId_TipoConta(int id_TipoConta) {
		this.id_TipoConta = id_TipoConta;
	} 
	
	@Override
	public String toString() {
		return nome;
	}
}
