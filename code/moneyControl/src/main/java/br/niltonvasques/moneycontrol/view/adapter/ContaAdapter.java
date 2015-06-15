package br.niltonvasques.moneycontrol.view.adapter;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.business.CartaoBusiness;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.CartaoCredito;
import br.niltonvasques.moneycontrol.database.bean.Conta;
import br.niltonvasques.moneycontrol.database.bean.Fatura;
import br.niltonvasques.moneycontrol.util.AssetUtil;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrolbeta.R;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;

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
		
		final Conta cc = (Conta) getItem(position);

		return ViewFactory.createContaItemView(inflater.getContext(), cc, dateRange, app, inflater, contas, this);
	}

}
