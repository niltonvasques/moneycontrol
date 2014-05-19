package br.niltonvasques.moneycontrol.view.adapter;

import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.bean.MovimentacaoAtivo;

public class MovimentacaoAtivoAdapter extends BaseAdapter{
	
	private MoneyControlApp app;
	private List<MovimentacaoAtivo> movimentacoes;
	private LayoutInflater inflater;
	private Context context;

	public MovimentacaoAtivoAdapter(List<MovimentacaoAtivo> transacoes, Context context, LayoutInflater inflater, MoneyControlApp app) {
		this.movimentacoes = transacoes;
		this.inflater = inflater;
		this.app = app;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		if(movimentacoes != null) return movimentacoes.size();
		return 0;		
	}

	@Override
	public Object getItem(int position) {
		return movimentacoes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		MovimentacaoAtivo tr = (MovimentacaoAtivo) getItem(position);
		
		return ViewFactory.createMovimentacaoAtivoItemView(tr, context, app, inflater, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MovimentacaoAtivoAdapter.this.notifyDataSetChanged();
			}
		});
	}

}
