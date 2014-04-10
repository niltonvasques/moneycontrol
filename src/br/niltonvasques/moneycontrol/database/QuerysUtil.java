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
	
	public static final String checkTipoTransacao(int id_Transacao){
		return "SELECT id_TipoTransacao FROM CategoriaTransacao c " +
				"INNER JOIN Transacao t on t.id_CategoriaTransacao = c.id " +
				"WHERE t.id = "+id_Transacao;
	}
	
	public static final String whereTransacaoFromContaWithDateInterval(int id_Conta, Date range){
		return "WHERE id_Conta = "+id_Conta+" AND " +
				"data < date('"+DateUtil.sqlDateFormat().format(range)+"', '+1 month') AND " +
				"data >= date('"+DateUtil.sqlDateFormat().format(range)+"')";
	}
	
	public static final String whereTransacaoWithDateInterval(Date range){
		return "WHERE data < date('"+DateUtil.sqlDateFormat().format(range)+"', '+1 month') AND " +
				"data >= date('"+DateUtil.sqlDateFormat().format(range)+"')";
	}
	
	public static final String sumContasCreditoWithDateInterval(Date range){
		return SUM_CONTAS_CREDITO+" AND data < date('"+DateUtil.sqlDateFormat().format(range)+"', '+1 month') AND " +
				"data >= date('"+DateUtil.sqlDateFormat().format(range)+"')";
	}
	
	public static final String sumContasDebitoWithDateInterval(Date range){
		return SUM_CONTAS_DEBITO+" AND data < date('"+DateUtil.sqlDateFormat().format(range)+"', '+1 month') AND " +
				"data >= date('"+DateUtil.sqlDateFormat().format(range)+"')";
	}
}
