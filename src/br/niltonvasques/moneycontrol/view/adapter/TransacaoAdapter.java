package br.niltonvasques.moneycontrol.view.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.R;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.Transacao;
import br.niltonvasques.moneycontrol.util.DateUtil;
import br.niltonvasques.moneycontrol.util.ViewUtil;

public class TransacaoAdapter extends BaseAdapter{
	
	private MoneyControlApp app;
	private List<Transacao> transacoes;
	private LayoutInflater inflater;
	private GregorianCalendar dateRange;

	public TransacaoAdapter(List<Transacao> transacoes, GregorianCalendar dateRange, LayoutInflater inflater, MoneyControlApp app) {
		this.transacoes = transacoes;
		this.dateRange = dateRange;
		this.inflater = inflater;
		this.app = app;
	}
	
	@Override
	public int getCount() {
		if(transacoes != null) return transacoes.size();
		return 0;		
	}

	@Override
	public Object getItem(int position) {
		return transacoes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Transacao cc = (Transacao) getItem(position);
		
		View view = inflater.inflate(R.layout.transacao_list_item, null);
		
		TextView txtNome = (TextView) view.findViewById(R.id.transacaoListItemTxtDescricao);
		TextView txtSaldo = (TextView) view.findViewById(R.id.transacaoListItemTxtValor);
		TextView txtData = (TextView) view.findViewById(R.id.transacaoListItemTxtData);
		
		String tipo = app.getDatabase().runQuery(QuerysUtil.checkTipoTransacao(cc.getId()));
		
		if(tipo.equals("2")){
			txtSaldo.setTextColor(Color.RED);
		}else{
			txtSaldo.setTextColor(app.getResources().getColor(R.color.dark_green));
		}
		
		txtNome.setText(cc.getDescricao());
		txtSaldo.setText("R$ "+cc.getValor());
		try {
			GregorianCalendar g = new GregorianCalendar();
			g.setTime(DateUtil.sqlDateFormat().parse(cc.getData()));
			ViewUtil.adjustDateOnTextView(txtData,g);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return view;
	}

}
