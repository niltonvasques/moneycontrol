package br.niltonvasques.moneycontrol.view.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.bean.Orcamento;
import br.niltonvasques.moneycontrol.database.bean.Transacao;

public class OrcamentoAdapter extends BaseAdapter{
	
	private MoneyControlApp app;
	private List<Orcamento> orcamentos;
	private LayoutInflater inflater;

	public OrcamentoAdapter(List<Orcamento> orcamentos, LayoutInflater inflater, MoneyControlApp app) {
		this.orcamentos = orcamentos;
		this.inflater = inflater;
		this.app = app;
	}
	
	@Override
	public int getCount() {
		if(orcamentos != null) return orcamentos.size();
		return 0;		
	}

	@Override
	public Object getItem(int position) {
		return orcamentos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Orcamento tr = (Orcamento) getItem(position);
		
		return ViewFactory.createOrcamentoItemView(tr, app, inflater);
	}

}
