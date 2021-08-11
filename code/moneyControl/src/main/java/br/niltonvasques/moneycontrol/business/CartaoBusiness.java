package br.niltonvasques.moneycontrol.business;

import java.util.GregorianCalendar;

import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.CartaoCredito;
import br.niltonvasques.moneycontrol.database.bean.Fatura;
import br.niltonvasques.moneycontrol.database.bean.Fatura.Status;

public class CartaoBusiness {

    public static GregorianCalendar computeRangeFatura(GregorianCalendar monthRange, CartaoCredito c){
        GregorianCalendar d = (GregorianCalendar) monthRange.clone();
        d.set(GregorianCalendar.DAY_OF_MONTH, c.getDia_fechamento());
        if(c.getDia_fechamento() <= c.getDia_vencimento())
            d.add(GregorianCalendar.MONTH, -1);
        else
            d.add(GregorianCalendar.MONTH, -2);
        return d;
    }
	
	public static Fatura computeFatura(DatabaseHandler db, CartaoCredito cc, GregorianCalendar date){
		GregorianCalendar cartaoDateRange = (GregorianCalendar) date.clone();
		
		String saldo = db.runQuery(QuerysUtil.computeSaldoFromCartaoBeforeDate(cc.getId_Conta(), cartaoDateRange.getTime()));
		float saldoAnterior = 0;
		if(saldo != null && saldo.length() > 0) saldoAnterior = Float.valueOf(saldo);
		
		String pagamentos = db.runQuery(QuerysUtil.getPagamentoFaturaCartao(cc.getId_Conta(),cartaoDateRange.getTime()));
		float pagsF = 0;
		if(pagamentos != null && pagamentos.length() > 0 ) pagsF = Float.valueOf(pagamentos);
		System.out.println("PAGAMENTOS: "+pagamentos);
		
		float fatura = Math.abs(saldoAnterior);
		String faturaStr = db.runQuery(QuerysUtil.sumTransacoesDebitoFromCartaoWithDateInterval(cc.getId_Conta(),cartaoDateRange.getTime()));
		System.out.println("CARTAO " + cc.getId_Conta() + " FATURA: "+faturaStr+" SALDO ANTERIOR: "+saldoAnterior+" - "+cartaoDateRange.getTime());
		if(faturaStr != null && !faturaStr.equals("")){
			try{
				fatura = Float.parseFloat(faturaStr);
				fatura -= saldoAnterior;
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		String cartaoSaldo = db.runQuery(QuerysUtil.computeSaldoConta(cc.getId_Conta()));
		float limite = Float.valueOf(cartaoSaldo);
		limite = cc.getLimite()-( limite > 0 ? 0 : Math.abs(limite));
		
		Fatura fat = new Fatura();
		fat.setDate(cartaoDateRange);
		fat.setCartao(cc);
		fat.setValor(fatura);
		if((pagsF > 0) ){
			fat.setStatus(Status.PAGA);			
		}else if((fatura > 0)){
			fat.setStatus(Status.PENDENTE);
		}
		fat.setLimite(limite);
		fat.setValorPago(pagsF);
		
		return fat;
	}

}
