package br.niltonvasques.moneycontrol.database.bean;

public class ContaPaga {
	
	private int id;
	private int id_Transacao;
	private int id_ContaAPagar;
	private String vencimento;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	
	@Override
	public String toString() {
		return "";
	}


	public int getId_Transacao() {
		return id_Transacao;
	}

	public void setId_Transacao(int id_Transacao) {
		this.id_Transacao = id_Transacao;
	}

	public int getId_ContaAPagar() {
		return id_ContaAPagar;
	}

	public void setId_ContaAPagar(int id_ContaAPagar) {
		this.id_ContaAPagar = id_ContaAPagar;
	}

	public String getVencimento() {
		return vencimento;
	}

	public void setVencimento(String vencimento) {
		this.vencimento = vencimento;
	}
}
