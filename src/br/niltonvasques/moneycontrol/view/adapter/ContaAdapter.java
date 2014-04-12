package br.niltonvasques.moneycontrol.view.adapter;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.R;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.Conta;
import br.niltonvasques.moneycontrol.database.bean.Transacao;
import br.niltonvasques.moneycontrol.util.DateUtil;

public class ContaAdapter extends BaseAdapter{
	
	private List<Conta> contas;
	private LayoutInflater inflater;
	private MoneyControlApp app;
	private GregorianCalendar dateRange;

	public ContaAdapter(List<Conta> contas, GregorianCalendar dateRange, LayoutInflater inflater,  MoneyControlApp app) {
		this.contas = contas;
		this.dateRange = dateRange;
		this.inflater = inflater;
		this.app = app;
	}
	
	public void setDateRange(GregorianCalendar dateRange){
		this.dateRange = dateRange;
	}
	
	@Override
	public int getCount() {
		if(contas != null) return contas.size();
		return 0;		
	}

	@Override
	public Object getItem(int position) {
		return contas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Conta cc = (Conta) getItem(position);
		
		View view = inflater.inflate(R.layout.conta_list_item, null);
		
		ImageView imgIcon = (ImageView) view.findViewById(R.id.contaListItemImgIcon);
		TextView txtNome = (TextView) view.findViewById(R.id.contaListItemTxtNome);
		TextView txtSaldo = (TextView) view.findViewById(R.id.contaListItemTxtSaldo);
		TextView txtDebitos = (TextView) view.findViewById(R.id.contaListItemTxtDebitos);
		TextView txtCreditos = (TextView) view.findViewById(R.id.contaListItemTxtCreditos);
		
		List<Transacao> transacoes = app.getDatabase().select(Transacao.class, 
				QuerysUtil.whereTransacaoFromContaWithDateInterval(cc.getId(), dateRange.getTime()));
		
		float creditos = 0;
		float debitos = 0;
		
		for (Transacao transacao : transacoes) {
			if(app.getCategoriasTransacao().get(transacao.getId_CategoriaTransacao()).getId_TipoTransacao() == 2){
				debitos += transacao.getValor();
			}else{
				creditos += transacao.getValor();
			}
		}
		
		String saldo = app.getDatabase().runQuery(QuerysUtil.computeSaldoFromContaBeforeDate(cc.getId(), dateRange.getTime()));
		float saldoAnterior = 0;
		if(saldo != null && saldo.length() > 0) saldoAnterior = Float.valueOf(saldo);
		
		
		
		txtNome.setText(cc.getNome());
		imgIcon.setBackgroundResource(cc.getIcon());
		txtCreditos.setText("R$ "+creditos);
		txtDebitos.setText("R$ "+debitos);
		txtSaldo.setText("R$ "+(saldoAnterior+cc.getSaldo()+creditos-debitos));
		
		return view;
	}

}
