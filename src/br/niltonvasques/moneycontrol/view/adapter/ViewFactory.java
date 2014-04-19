package br.niltonvasques.moneycontrol.view.adapter;

import java.text.ParseException;
import java.util.GregorianCalendar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.R;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;
import br.niltonvasques.moneycontrol.database.bean.Transacao;
import br.niltonvasques.moneycontrol.util.DateUtil;
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

}
