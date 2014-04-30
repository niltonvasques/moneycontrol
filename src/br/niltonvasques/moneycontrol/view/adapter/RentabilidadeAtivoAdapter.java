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
import br.niltonvasques.moneycontrol.database.bean.Ativo;
import br.niltonvasques.moneycontrol.database.bean.RentabilidadeAtivo;

public class RentabilidadeAtivoAdapter extends BaseAdapter{
	
	private MoneyControlApp app;
	private List<RentabilidadeAtivo> rentabilidades;
	private LayoutInflater inflater;
	private Context context;

	public RentabilidadeAtivoAdapter(List<RentabilidadeAtivo> transacoes, Context context, LayoutInflater inflater, MoneyControlApp app) {
		this.rentabilidades = transacoes;
		this.inflater = inflater;
		this.app = app;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		if(rentabilidades != null) return rentabilidades.size();
		return 0;		
	}

	@Override
	public Object getItem(int position) {
		return rentabilidades.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		RentabilidadeAtivo tr = (RentabilidadeAtivo) getItem(position);
		
		return ViewFactory.createRentabilidadeAtivoItemView(tr, context, app, inflater, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				RentabilidadeAtivoAdapter.this.notifyDataSetChanged();
			}
		});
	}

}
