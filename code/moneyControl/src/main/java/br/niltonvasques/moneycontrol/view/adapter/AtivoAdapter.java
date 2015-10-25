package br.niltonvasques.moneycontrol.view.adapter;

import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.Ativo;

public class AtivoAdapter extends BaseAdapter{
	
	private MoneyControlApp app;
	private List<Ativo> ativos;
	private LayoutInflater inflater;
	private Context context;
	private GregorianCalendar dateRange;

	public AtivoAdapter(List<Ativo> ativos, Context context, GregorianCalendar dateRange, LayoutInflater inflater, MoneyControlApp app) {
		this.ativos = ativos;
		this.inflater = inflater;
		this.app = app;
		this.context = context;
		this.dateRange = dateRange;
	}
	
	@Override
	public int getCount() {
		if(ativos != null) return ativos.size();
		return 0;		
	}

	@Override
	public Object getItem(int position) {
		return ativos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final Ativo tr = (Ativo) getItem(position);
		
		return ViewFactory.createAtivoItemView(tr, context, dateRange.getTime(), app, inflater, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				app.getDatabase().runQuery(QuerysUtil.updateMovimentacaoAtivos(tr.getId()));
				app.getDatabase().runQuery(QuerysUtil.updateMovimentacaoAtivosWithFinanceiro(tr.getId()));
				AtivoAdapter.this.notifyDataSetChanged();
			}
		});
	}

}
