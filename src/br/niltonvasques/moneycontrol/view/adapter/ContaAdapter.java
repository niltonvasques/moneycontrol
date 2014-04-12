package br.niltonvasques.moneycontrol.view.adapter;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.R;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.Conta;
import br.niltonvasques.moneycontrol.util.AssetUtil;
import br.niltonvasques.moneycontrol.util.MessageUtils;

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
		
		View view = inflater.inflate(R.layout.conta_list_item, null);
		
		ImageView imgIcon = (ImageView) view.findViewById(R.id.contaListItemImgIcon);
		TextView txtNome = (TextView) view.findViewById(R.id.contaListItemTxtNome);
		TextView txtSaldo = (TextView) view.findViewById(R.id.contaListItemTxtSaldo);
		TextView txtDebitos = (TextView) view.findViewById(R.id.contaListItemTxtDebitos);
		TextView txtCreditos = (TextView) view.findViewById(R.id.contaListItemTxtCreditos);
		
		Button btnEditConta = (Button) view.findViewById(R.id.contaListItemBtnEditConta);
		btnEditConta.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MessageUtils.showEditConta(inflater.getContext(), cc, inflater, app.getDatabase(), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						contas.clear();
						contas.addAll(app.getDatabase().select(Conta.class));
					}
				});				
			}
		});
		
		float debitoSum = 0;
		float creditoSum = 0;
		String debitos = app.getDatabase().runQuery(QuerysUtil.sumTransacoesDebitoFromContaWithDateInterval(cc.getId(),dateRange.getTime()));
		String creditos = app.getDatabase().runQuery(QuerysUtil.sumTransacoesCreditoFromContaWithDateInterval(cc.getId(),dateRange.getTime()));
		
		if(debitos != null && debitos.length() > 0)  debitoSum = Float.valueOf(debitos);
		if(creditos != null && creditos.length() > 0) creditoSum = Float.valueOf(creditos);
		
		String saldo = app.getDatabase().runQuery(QuerysUtil.computeSaldoFromContaBeforeDate(cc.getId(), dateRange.getTime()));
		float saldoAnterior = 0;
		if(saldo != null && saldo.length() > 0) saldoAnterior = Float.valueOf(saldo);
		
		txtNome.setText(cc.getNome());
		try {
			imgIcon.setImageDrawable(AssetUtil.loadDrawableFromAsset(app, "icons/"+cc.getIcon()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		txtCreditos.setText("R$ "+creditoSum);
		txtDebitos.setText("R$ "+debitoSum);
		txtSaldo.setText("R$ "+(saldoAnterior+cc.getSaldo()+creditoSum-debitoSum));
		
		return view;
	}

}
