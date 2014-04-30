package br.niltonvasques.moneycontrol.view.adapter;

import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.R;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.Ativo;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;
import br.niltonvasques.moneycontrol.database.bean.RentabilidadeAtivo;
import br.niltonvasques.moneycontrol.database.bean.Transacao;
import br.niltonvasques.moneycontrol.util.DateUtil;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.util.ViewUtil;

public class ViewFactory {
	
	public static View createTransacaoItemView(Transacao tr, MoneyControlApp app, LayoutInflater inflater){
		View view = inflater.inflate(R.layout.transacao_list_item, null);
		
		TextView txtNome = (TextView) view.findViewById(R.id.transacaoListItemTxtDescricao);
		TextView txtSaldo = (TextView) view.findViewById(R.id.transacaoListItemTxtValor);
		TextView txtData = (TextView) view.findViewById(R.id.transacaoListItemTxtData);
		
		String tipo = app.getDatabase().runQuery(QuerysUtil.checkTipoTransacao(tr.getId()));
		
		CategoriaTransacao c = app.getCategoriasTransacao().get(tr.getId_CategoriaTransacao());
		
		if(tipo.equals("2")){
			txtSaldo.setTextColor(Color.RED);
		}else{
			txtSaldo.setTextColor(app.getResources().getColor(R.color.dark_green));
		}
		
		txtNome.setText(tr.getDescricao());
		txtSaldo.setText("R$ "+String.format("%.2f", tr.getValor()));
		try {
			GregorianCalendar g = new GregorianCalendar();
			g.setTime(DateUtil.sqlDateFormat().parse(tr.getData()));
			ViewUtil.adjustDateOnTextView(txtData,g);
			txtData.setText(txtData.getText().toString()+(c != null ? " - "+c.getNome() : "")); 
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return view;
	}

	public static View createAtivoItemView(final Ativo ativo, final Context context, Date dateRange, final MoneyControlApp app,
			final LayoutInflater inflater, final DialogInterface.OnClickListener listener) {
		View view = inflater.inflate(R.layout.ativo_list_item, null);
		
		TextView txtNome = (TextView) view.findViewById(R.id.transacaoListItemTxtDescricao);
		TextView txtValor = (TextView) view.findViewById(R.id.ativoListItemTxtPrice);
		TextView txtProfit = (TextView) view.findViewById(R.id.ativoListItemTxtProfit);
		TextView txtData = (TextView) view.findViewById(R.id.transacaoListItemTxtData);
		Button btnNewEvent = (Button) view.findViewById(R.id.ativoListItemBtnNewEvent);
		
		String tipo = app.getDatabase().runQuery(QuerysUtil.checkTipoAtivo(ativo.getId()));
		
		if( ! app.getDatabase().runQuery(QuerysUtil.checkExistsRentabilidadeAtivo(ativo.getId(),dateRange)).equals("0")){
			String valorStr = app.getDatabase().runQuery(QuerysUtil.checkLastRentabilidadeAtivo(ativo.getId(),dateRange));
			try {
				float valor = Float.valueOf(valorStr);
				float profit = ((valor - ativo.getValor())/ativo.getValor())*100;
				valor *= ativo.getQuantidade();
				txtValor.setText("R$ "+String.format("%.2f",valor));
				txtProfit.setText(String.format("%.2f",profit)+" %");
				
				if(profit < 0){
					txtProfit.setTextColor(Color.RED);
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}else{
			txtValor.setText("R$ "+String.format("%.2f", ativo.getValor()*ativo.getQuantidade()));
			txtProfit.setText("0.00 %");
		}
		
		txtNome.setText(ativo.getNome());
		try {
			GregorianCalendar g = new GregorianCalendar();
			g.setTime(DateUtil.sqlDateFormat().parse(ativo.getData()));
			ViewUtil.adjustDateOnTextView(txtData,g);
			txtData.setText(txtData.getText().toString()+" - "+tipo); 
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		btnNewEvent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MessageUtils.showAddAtivoRentabilidade(context, ativo, inflater, app.getDatabase(), listener);
			}
		});
		
		
		return view;
	}
	
	public static View createRentabilidadeAtivoItemView(final RentabilidadeAtivo ativo, final Context context, final MoneyControlApp app,
			final LayoutInflater inflater, final DialogInterface.OnClickListener listener) {
		View view = inflater.inflate(R.layout.rentabilidade_ativo_list_item, null);
		
		TextView txtValor = (TextView) view.findViewById(R.id.ativoListItemTxtPrice);
		TextView txtData = (TextView) view.findViewById(R.id.transacaoListItemTxtData);
		
		txtValor.setText("R$ "+String.format("%.2f", ativo.getValor()));
		
		try {
			GregorianCalendar g = new GregorianCalendar();
			g.setTime(DateUtil.sqlDateFormat().parse(ativo.getData()));
			ViewUtil.adjustDateOnTextView(txtData,g);
			txtData.setText(txtData.getText().toString()); 
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return view;
	}

}
