package br.niltonvasques.moneycontrol.database.bean;

/**
 * @author niltonvasques
 *
 */
public class MovimentacaoAtivo {

	private int id;
	private String data;
	private float financeiro;
	private float movimentacao;
	private int id_Ativo;
	private float patrimonio;
	private float cotas;
	private float cotas_emitidas;
	private int id_Transacao;
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
	public float getFinanceiro() {
		return financeiro;
	}
	public void setFinanceiro(float financeiro) {
		this.financeiro = financeiro;
	}
	public float getMovimentacao() {
		return movimentacao;
	}
	public void setMovimentacao(float movimentacao) {
		this.movimentacao = movimentacao;
	}
	public int getId_Ativo() {
		return id_Ativo;
	}
	public void setId_Ativo(int id_Ativo) {
		this.id_Ativo = id_Ativo;
	}
	public float getPatrimonio() {
		return patrimonio;
	}
	public void setPatrimonio(float patrimonio) {
		this.patrimonio = patrimonio;
	}
	public float getCotas() {
		return cotas;
	}
	public void setCotas(float cotas) {
		this.cotas = cotas;
	}
	public float getCotas_emitidas() {
		return cotas_emitidas;
	}
	public void setCotas_emitidas(float cotas_emitidas) {
		this.cotas_emitidas = cotas_emitidas;
	}
	public int getId_Transacao() {
		return id_Transacao;
	}
	public void setId_Transacao(int id_Transacao) {
		this.id_Transacao = id_Transacao;
	}
	
	
}
