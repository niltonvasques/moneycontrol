package br.niltonvasques.moneycontrol.view.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.bean.Ativo;
import br.niltonvasques.moneycontrol.database.bean.Transacao;

public class AtivoAdapter extends BaseAdapter{
	
	private MoneyControlApp app;
	private List<Ativo> transacoes;
	private LayoutInflater inflater;

	public AtivoAdapter(List<Ativo> transacoes, LayoutInflater inflater, MoneyControlApp app) {
		this.transacoes = transacoes;
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
		
		Ativo tr = (Ativo) getItem(position);
		
		return ViewFactory.createAtivoItemView(tr, app, inflater);
	}

}
