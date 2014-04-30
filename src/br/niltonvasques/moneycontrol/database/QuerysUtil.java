package br.niltonvasques.moneycontrol.database;

import java.util.Date;

import br.niltonvasques.moneycontrol.util.DateUtil;

public class QuerysUtil {

	public static final String SUM_CONTAS_CREDITO = "SELECT SUM(valor)"+ 
													" FROM Transacao t"+ 
													" INNER JOIN CategoriaTransacao c on c.id = t.id_CategoriaTransacao"+
													" WHERE c.id_TipoTransacao = 1";
	
	public static final String SUM_CONTAS_DEBITO = "SELECT SUM(valor)"+ 
													" FROM Transacao t"+ 
													" INNER JOIN CategoriaTransacao c on c.id = t.id_CategoriaTransacao"+
													" WHERE c.id_TipoTransacao = 2";
	
	private static final String SUM_TRANSACOES_CREDITO = "SELECT SUM(valor) FROM Transacao t " +
														"INNER JOIN CategoriaTransacao c on c.id = t.id_CategoriaTransacao " +
														"WHERE id_TipoTransacao = 1 AND id_Conta = ";
	
	private static final String SUM_TRANSACOES_DEBITO = "SELECT SUM(valor) FROM Transacao t " +
														"INNER JOIN CategoriaTransacao c on c.id = t.id_CategoriaTransacao " +
														"WHERE id_TipoTransacao = 2 AND id_Conta = ";
	
	public static final String sumTransacoesCreditoFromConta(int id_Conta){
		return SUM_TRANSACOES_CREDITO + id_Conta;
	}
	
	public static final String sumTransacoesDebitoFromConta(int id_Conta){
		return SUM_TRANSACOES_DEBITO + id_Conta;
	}
	
	public static final String sumTransacoesDebitoFromContaWithDateInterval(int id_Conta, Date range){
		return SUM_TRANSACOES_DEBITO + id_Conta+" AND data < date('"+DateUtil.sqlDateFormat().format(range)+"', '+1 month') AND " +
				"data >= date('"+DateUtil.sqlDateFormat().format(range)+"')";
	}
	
	public static final String sumTransacoesCreditoFromContaWithDateInterval(int id_Conta, Date range){
		return SUM_TRANSACOES_CREDITO + id_Conta+" AND data < date('"+DateUtil.sqlDateFormat().format(range)+"', '+1 month') AND " +
				"data >= date('"+DateUtil.sqlDateFormat().format(range)+"')";
	}
	
	public static final String getPagamentoFaturaCartao(int id_Conta, Date range){
		return sumTransacoesCreditoFromContaWithDateInterval(id_Conta,range)+ " AND c.nome like 'Transferência'";
	}
	
	public static final String checkTipoTransacao(int id_Transacao){
		return "SELECT id_TipoTransacao FROM CategoriaTransacao c " +
				"INNER JOIN Transacao t on t.id_CategoriaTransacao = c.id " +
				"WHERE t.id = "+id_Transacao;
	}
	
	public static final String whereTransacaoFromContaWithDateInterval(int id_Conta, Date range){
		return "WHERE id_Conta = "+id_Conta+" AND " +
				"data < date('"+DateUtil.sqlDateFormat().format(range)+"', '+1 month') AND " +
				"data >= date('"+DateUtil.sqlDateFormat().format(range)+"') ORDER BY data DESC";
	}
	
	public static final String whereTransacaoFromContaWithDateIntervalAndCategoria(int id_Conta, int id_CategoriaTransacao, Date range){
		return "WHERE id_Conta = "+id_Conta+" AND " +
				"id_CategoriaTransacao = "+id_CategoriaTransacao+" AND "+
				"data < date('"+DateUtil.sqlDateFormat().format(range)+"', '+1 month') AND " +
				"data >= date('"+DateUtil.sqlDateFormat().format(range)+"') ORDER BY data DESC";
	}
	
	public static String whereTransacaoWithDateIntervalAndCategoria(int id_CategoriaTransacao,	Date time) {
		return "WHERE id_CategoriaTransacao = "+id_CategoriaTransacao+" AND "+
				"data < date('"+DateUtil.sqlDateFormat().format(time)+"', '+1 month') AND " +
				"data >= date('"+DateUtil.sqlDateFormat().format(time)+"') ORDER BY data DESC";
	}
	
	public static final String whereTransacaoWithDateInterval(Date range){
		return "WHERE data < date('"+DateUtil.sqlDateFormat().format(range)+"', '+1 month') AND " +
				"data >= date('"+DateUtil.sqlDateFormat().format(range)+"') ORDER BY data DESC";
	}
	
	public static final String sumContasCreditoWithDateInterval(Date range){
		return SUM_CONTAS_CREDITO+" " +
				"AND c.nome not like 'Transferência' "+
				"AND data < date('"+DateUtil.sqlDateFormat().format(range)+"', '+1 month') AND " +
				"data >= date('"+DateUtil.sqlDateFormat().format(range)+"')";
	}
	
	public static final String sumContasDebitoWithDateInterval(Date range){
		return SUM_CONTAS_DEBITO+" " +
				"AND c.nome not like 'Transferência' "+
				"AND data < date('"+DateUtil.sqlDateFormat().format(range)+"', '+1 month') AND " +
				"data >= date('"+DateUtil.sqlDateFormat().format(range)+"')";
	}
	
	public static final String computeSaldoBeforeDate(Date range){
		return "SELECT "+ 
					"COALESCE("+
						"(SELECT SUM(t.valor) "+ 
						"FROM Transacao t "+ 
						"INNER JOIN CategoriaTransacao c on c.id = t.id_CategoriaTransacao "+
						"WHERE c.id_TipoTransacao = 1 " +
						"AND t.data < date('"+DateUtil.sqlDateFormat().format(range)+"')) "+
						",0) "+
						" - "+
					"COALESCE( "+
						" (SELECT SUM(t.valor) "+ 
						" FROM Transacao t "+ 
						" INNER JOIN CategoriaTransacao c on c.id = t.id_CategoriaTransacao "+
						" WHERE c.id_TipoTransacao = 2 " +
						"AND t.data < date('"+DateUtil.sqlDateFormat().format(range)+"')) "+ 
						" ,0)";
	}
	
	public static final String computeSaldoConta(int id_Conta){
		return "SELECT "+				
					"COALESCE("+
						"(SELECT SUM(t.valor) "+ 
						"FROM Transacao t "+ 
						"INNER JOIN CategoriaTransacao c on c.id = t.id_CategoriaTransacao "+
						"WHERE c.id_TipoTransacao = 1 AND t.id_Conta = "+id_Conta+" )"+
						",0) "+
						" - "+
					"COALESCE( "+
						" (SELECT SUM(t.valor) "+ 
						" FROM Transacao t "+ 
						" INNER JOIN CategoriaTransacao c on c.id = t.id_CategoriaTransacao "+
						" WHERE c.id_TipoTransacao = 2 AND t.id_Conta = "+id_Conta+" )"+ 
						" ,0)";
	}
	
	public static final String computeSaldoFromContaBeforeDate(int id_Conta, Date range){
		return "SELECT "+ 
					"(SELECT saldo FROM Conta WHERE id = "+id_Conta+")+ "+
					"COALESCE("+
						"(SELECT SUM(t.valor) "+ 
						"FROM Transacao t "+ 
						"INNER JOIN CategoriaTransacao c on c.id = t.id_CategoriaTransacao "+
						"WHERE t.id_Conta = "+id_Conta+" AND c.id_TipoTransacao = 1 AND t.data < date('"+DateUtil.sqlDateFormat().format(range)+"')) "+
						",0) "+
						" - "+
					"COALESCE( "+
						" (SELECT SUM(t.valor) "+ 
						" FROM Transacao t "+ 
						" INNER JOIN CategoriaTransacao c on c.id = t.id_CategoriaTransacao "+
						"WHERE t.id_Conta = "+id_Conta+" AND c.id_TipoTransacao = 2 AND t.data < date('"+DateUtil.sqlDateFormat().format(range)+"')) "+ 
						" ,0)";
	}
	
	public static final String reportTransacaoDebitosByCategorias(){
		return 	"SELECT "+ 
				"MAX(c.nome) AS Categoria, "+ 
				"SUM(valor)  as Total, "+ 
				"(SUM(valor) /(SELECT SUM(valor) From Transacao t INNER JOIN CategoriaTransacao c on c.id = t.id_CategoriaTransacao WHERE c.id_TipoTransacao  = 2)*100) as Percentual "+
				"FROM Transacao  t "+
				"INNER JOIN CategoriaTransacao c on c.id = t.id_CategoriaTransacao "+
				"WHERE c.id_TipoTransacao  = 2 "+
				"GROUP BY id_CategoriaTransacao "+ 
				"ORDER BY Max(c.nome) ";
	}
	
	public static final String reportTransacaoDebitosByCategoriasWithDateInterval(Date range){
		return 	"SELECT "+ 
				"MAX(c.nome) AS Categoria, "+ 
				"SUM(valor)  as Total, "+ 
				"(SUM(valor) / " +
					"(SELECT SUM(valor) " +
					"From Transacao t " +
					"INNER JOIN CategoriaTransacao c on c.id = t.id_CategoriaTransacao " +
					"WHERE c.id_TipoTransacao  = 2 " +
					"AND c.nome not like 'Transferência'"+
					"AND data < date('"+DateUtil.sqlDateFormat().format(range)+"', '+1 month') AND " +
					"data >= date('"+DateUtil.sqlDateFormat().format(range)+"') )*100) as Percentual "+
				"FROM Transacao  t "+
				"INNER JOIN CategoriaTransacao c on c.id = t.id_CategoriaTransacao "+
				"WHERE c.id_TipoTransacao  = 2 "+
				"AND c.nome not like 'Transferência'"+
				"AND c.nome not like 'Investimento'"+
				"AND data < date('"+DateUtil.sqlDateFormat().format(range)+"', '+1 month') AND " +
				"data >= date('"+DateUtil.sqlDateFormat().format(range)+"') "+
				"GROUP BY id_CategoriaTransacao "+ 
				"ORDER BY Max(c.nome) ";
	}
	
	public static final String reportTransacaoByTipoByCategoriasWithDateInterval(int tipo, Date range){
		return 	"SELECT "+ 
				"MAX(c.nome) AS Categoria, "+ 
				"SUM(valor)  as Total, "+ 
				"(SUM(valor) / " +
					"(SELECT SUM(valor) " +
					"From Transacao t " +
					"INNER JOIN CategoriaTransacao c on c.id = t.id_CategoriaTransacao " +
					"WHERE c.id_TipoTransacao  = "+tipo+" "+
					"AND c.nome not like 'Transferência'"+
					"AND data < date('"+DateUtil.sqlDateFormat().format(range)+"', '+1 month') AND " +
					"data >= date('"+DateUtil.sqlDateFormat().format(range)+"') )*100) as Percentual "+
				"FROM Transacao  t "+
				"INNER JOIN CategoriaTransacao c on c.id = t.id_CategoriaTransacao "+
				"WHERE c.id_TipoTransacao  = "+tipo+" "+
				"AND data < date('"+DateUtil.sqlDateFormat().format(range)+"', '+1 month') AND " +
				"data >= date('"+DateUtil.sqlDateFormat().format(range)+"') "+
				"AND c.nome not like 'Transferência'"+
				"AND c.nome not like 'Investimento'"+
				"GROUP BY id_CategoriaTransacao "+ 
				"ORDER BY Max(c.nome) ";
	}
	
	public static final String reportCategoriaWithDateInterval(int id_CategoriaTransacao, int month, int year){
		return 	"SELECT SUM(valor) as total, strftime(\"%m\",data) as month, strftime(\"%Y\",data) as year From Transacao "+
				"WHERE id_CategoriaTransacao = " +id_CategoriaTransacao+" "+
				"AND CAST(strftime(\"%m\", data) as integer) = "+month+" "+
				"AND CAST(strftime(\"%Y\", data) as INTEGER) = "+year;
	}
	
	public static final String reportCategoriaWithDateIntervalAndConta(int id_CategoriaTransacao, int id_Conta, int month, int year){
		return 	reportCategoriaWithDateInterval(id_CategoriaTransacao, month, year)+
				" AND id_Conta = "+id_Conta;
	}
	
	public static final String reportCategoriaByMonth(int id_CategoriaTransacao){
		return 	"SELECT SUM(valor) as total, strftime(\"%m\",data) as month, strftime(\"%Y\",data) as year From Transacao "+
				"WHERE id_CategoriaTransacao = " +id_CategoriaTransacao+" "+
				"GROUP BY strftime(\"%m-%Y\", data)";
	}
	
	
	public static final String reportHistoryReceitas(){
		return reportHistory(1);
	}
	
	public static final String reportHistoryDespesas(){
		return reportHistory(2);
	}
	
	public static final String reportHistory(int tipoTransacao){
		return "SELECT SUM(valor) as total, " +
				"strftime(\"%m-%Y\", data) as month " +
				"FROM Transacao t " +
				"INNER JOIN CategoriaTransacao c on c.id = t.id_CategoriaTransacao " +
				"WHERE c.id_TipoTransacao = "+tipoTransacao+" AND c.nome not like 'Transferência' " +
				"AND c.nome not like 'Investimento'"+
				"GROUP BY strftime(\"%m-%Y\", data)";
	}
	
	public static final String reportInvestimentsHistory(){
		return "SELECT SUM(valor) as total, " +
				"strftime(\"%m-%Y\", data) as month " +
				"FROM Transacao t " +
				"INNER JOIN CategoriaTransacao c on c.id = t.id_CategoriaTransacao " +
				"WHERE c.nome = 'Investimento' AND system = 1 " +
				"GROUP BY strftime(\"%m-%Y\", data)";
	}

	public static String sumSaldoContas() {
		return "SELECT SUM(saldo) FROM Conta";
	}

	public static String checkTipoAtivo(int id) {
		return "SELECT t.nome FROM Ativo a " +
				"INNER JOIN TipoAtivo t on t.id = a.id_TipoAtivo " +
				"WHERE a.id = "+id;
	}
	
	public static String checkLastRentabilidadeAtivo(int id_Ativo, Date date) {
		return  "SELECT valor FROM RentabilidadeAtivo r " +
				"WHERE r.id_Ativo = "+id_Ativo+" " +
				"AND data < date('" +DateUtil.sqlDateFormat().format(date)+"','+1 month') "+
	   		   	"ORDER BY data DESC	" +
	   		   	"LIMIT 1";
	}
	
	public static String checkExistsRentabilidadeAtivo(int id_Ativo, Date date) {
		return  "SELECT count(*) FROM RentabilidadeAtivo r " +
				"WHERE r.id_Ativo = "+id_Ativo+" "+
				"AND data < date('" +DateUtil.sqlDateFormat().format(date)+"','+1 month') ";
	}

	
}
