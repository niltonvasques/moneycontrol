package br.niltonvasques.moneycontrol.view.adapter;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.beardedhen.androidbootstrap.FontAwesomeText;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.niltonvasques.moneycontrol.database.bean.ContaAPagar;
import br.niltonvasques.moneycontrolbeta.R;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.Ativo;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;
import br.niltonvasques.moneycontrol.database.bean.MovimentacaoAtivo;
import br.niltonvasques.moneycontrol.database.bean.Orcamento;
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
		FontAwesomeText fontAwesome = (FontAwesomeText) view.findViewById(R.id.transacaoListItemFontAwe);
		
		String tipo = app.getDatabase().runQuery(QuerysUtil.checkTipoTransacao(tr.getId()));
		
		CategoriaTransacao c = app.getCategoriasTransacao().get(tr.getId_CategoriaTransacao());
		
		if(tipo.equals("2")){
			txtSaldo.setTextColor(Color.RED);
		}else{
			txtSaldo.setTextColor(app.getResources().getColor(R.color.dark_green));
			fontAwesome.setTextColor(app.getResources().getColor(R.color.dark_green));
			fontAwesome.setIcon("fa-arrow-up");
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
//		TextView txtData = (TextView) view.findViewById(R.id.transacaoListItemTxtData);
		Button btnNewEvent = (Button) view.findViewById(R.id.ativoListItemBtnNewEvent);
		
		
//		String tipo = app.getDatabase().runQuery(QuerysUtil.checkTipoAtivo(ativo.getId()));
		
		List<MovimentacaoAtivo> movimentacoes = app.getDatabase().select(MovimentacaoAtivo.class, QuerysUtil.whereLastMovimentacoesAtivoOrderByDateDesc(ativo.getId(), dateRange));
		
		if( ! movimentacoes.isEmpty()){
			MovimentacaoAtivo lastMovimentacao = movimentacoes.get(0);
			try {
				float profit = 0;
					
				float lastValorCota = 1, penultValorCota = 1;
				
				if(lastMovimentacao.getCotas() != 0)
					lastValorCota = (lastMovimentacao.getPatrimonio()/lastMovimentacao.getCotas());
				
				profit = ((lastValorCota/penultValorCota)-1)*100;
				
				txtValor.setText("R$ "+String.format("%.2f",lastMovimentacao.getPatrimonio()));
				txtProfit.setText(String.format("%.2f",profit)+" %");

				if(profit < 0){
					txtProfit.setTextColor(Color.RED);
				}

			} catch (Exception e) {
				
			}

		}else{
			txtValor.setText("R$ "+String.format("%.2f", 0f));
			txtProfit.setText("0.00 %");
		}
//		
		txtNome.setText(ativo.getNome());
//		try {
//			GregorianCalendar g = new GregorianCalendar();
//			g.setTime(DateUtil.sqlDateFormat().parse(ativo.getData()));
//			ViewUtil.adjustDateOnTextView(txtData,g);
//			txtData.setText(txtData.getText().toString()+" - "+tipo); 
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		
		btnNewEvent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MessageUtils.showAddAtivoFechamento(context, ativo, inflater, app.getDatabase(), listener);
			}
		});
		
		
		return view;
	}
	
	public static View createMovimentacaoAtivoItemView(final MovimentacaoAtivo ativo, final Context context, final MoneyControlApp app,
			final LayoutInflater inflater, final DialogInterface.OnClickListener listener) {
		View view = inflater.inflate(R.layout.rentabilidade_ativo_list_item, null);
		
		TextView txtValor = (TextView) view.findViewById(R.id.ativoListItemTxtPrice);
		TextView txtData = (TextView) view.findViewById(R.id.transacaoListItemTxtData);
		
		txtValor.setText("R$ "+String.format("%.2f", ativo.getPatrimonio()));
		
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
	
	public static View createOrcamentoItemView(Orcamento tr, MoneyControlApp app, LayoutInflater inflater){
		View view = inflater.inflate(R.layout.orcamento_list_item, null);
		
		TextView txtNome = (TextView) view.findViewById(R.id.orcamentoListItemTxtCategoria);
		TextView txtSaldo = (TextView) view.findViewById(R.id.orcamentoListItemTxtValorPlanejado);
		TextView txtRealizado = (TextView) view.findViewById(R.id.orcamentoListItemTxtValorRealizado);
		TextView txtRestante = (TextView) view.findViewById(R.id.orcamentoListItemTxtValorRestante);
		ProgressBar progress = (ProgressBar) view.findViewById(R.id.orcamentoListItemProgressBar);
//		progress.getProgressDrawable().setColorFilter(Color.RED, Mode.MULTIPLY);
		
//		String tipo = app.getDatabase().runQuery(QuerysUtil.checkTipoTransacao(tr.getId()));
		
		CategoriaTransacao c = app.getCategoriasTransacao().get(tr.getId_CategoriaTransacao());
		
		try {
			GregorianCalendar g = new GregorianCalendar();
			g.setTime(DateUtil.sqlDateFormat().parse(tr.getMes()));
			Cursor cur = app.getDatabase().runQueryCursor(QuerysUtil.reportCategoriaWithDateInterval(tr.getId_CategoriaTransacao(), g.get(Calendar.MONTH)+1, g.get(Calendar.YEAR)));
			if(cur.moveToFirst()){
				float total = cur.getFloat(0);
				float restante = tr.getValor()-total;
				txtRealizado.setText("R$ "+String.format("%.2f", total));
				txtRestante.setText("R$ "+String.format("%.2f", restante));
				if(restante < 0){
					txtRestante.setTextColor(Color.RED);
				}
				int progressSize = (int) (total/tr.getValor()* 100);
				progress.setProgress(progressSize);
			}
			cur.close();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		txtNome.setText(c.getNome());
		txtSaldo.setText("R$ "+String.format("%.2f", tr.getValor()));
		
		return view;
	}

	public static View createContaAPagarItemView(ContaAPagar tr, GregorianCalendar range, MoneyControlApp app, LayoutInflater inflater){
		View view = inflater.inflate(R.layout.conta_a_pagar_list_item, null);

		TextView txtNome = (TextView) view.findViewById(R.id.contaAPagarListItemTxtDescricao);
		TextView txtSaldo = (TextView) view.findViewById(R.id.contaAPagarListItemTxtValor);
		TextView txtData = (TextView) view.findViewById(R.id.contaAPagarListItemTxtData);
		FontAwesomeText fontAwesome = (FontAwesomeText) view.findViewById(R.id.contaAPagarListItemFontAwe);

        GregorianCalendar contaData = new GregorianCalendar();
        try {
            contaData.setTime(DateUtil.sqlDateFormat().parse(tr.getData()));
            contaData.set(Calendar.MONTH, range.get(Calendar.MONTH));
            contaData.set(Calendar.YEAR, range.get(Calendar.YEAR));
            ViewUtil.adjustDateOnTextView(txtData, contaData);
//			txtData.setText(txtData.getText().toString()+(c != null ? " - "+c.getNome() : ""));
        } catch (ParseException e) {
            e.printStackTrace();
        }
//
		String data = app.getDatabase().runQuery(QuerysUtil.lastContaPagaOnMonth(tr.getId(), range.getTime()));
        System.out.println("DATA: "+data);

        GregorianCalendar now = new GregorianCalendar();

        boolean paid = data != null && !data.equals("");
        boolean delayed = now.compareTo(contaData) > 0 && now.get(Calendar.DAY_OF_MONTH) > contaData.get(Calendar.DAY_OF_MONTH);
//
//		CategoriaTransacao c = app.getCategoriasTransacao().get(tr.getId_CategoriaTransacao());
//
		if(!paid && !delayed) {
            txtSaldo.setTextColor(app.getResources().getColor(R.color.amber));
            fontAwesome.setTextColor(app.getResources().getColor(R.color.amber));
            fontAwesome.setIcon("fa-warning");
        }else if(!paid && delayed){
            txtSaldo.setTextColor(Color.RED);
            fontAwesome.setTextColor(Color.RED);
            fontAwesome.setIcon("fa-times");
		}else{
			txtSaldo.setTextColor(app.getResources().getColor(R.color.dark_green));
			fontAwesome.setTextColor(app.getResources().getColor(R.color.dark_green));
			fontAwesome.setIcon("fa-check");
            view.findViewById(R.id.contaAPagarListItemBtnPagarFatura).setVisibility(View.GONE);
		}
//
		txtNome.setText(tr.getDescricao());
		txtSaldo.setText("R$ "+String.format("%.2f", tr.getValor()));


		return view;
	}

}
