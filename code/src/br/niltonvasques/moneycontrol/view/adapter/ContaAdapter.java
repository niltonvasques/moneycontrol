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
		CartaoCredito cartao = null;
		
		View view = inflater.inflate(R.layout.conta_list_item, null);
		
		BootstrapCircleThumbnail imgIcon = (BootstrapCircleThumbnail) view.findViewById(R.id.contaListItemImgIcon);
		final TextView txtNome = (TextView) view.findViewById(R.id.contaListItemTxtNome);
		final TextView txtSaldo = (TextView) view.findViewById(R.id.contaListItemTxtSaldo);
		final TextView txtDebitos = (TextView) view.findViewById(R.id.contaListItemTxtDebitos);
		final TextView txtCreditos = (TextView) view.findViewById(R.id.contaListItemTxtCreditos);
		
		txtNome.setText(cc.getNome());
		try {
//			imgIcon.setImageDrawable();
			imgIcon.setImage(AssetUtil.loadBitmapFromAsset(app, "icons/"+cc.getIcon()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BootstrapButton btnEditConta = (BootstrapButton) view.findViewById(R.id.contaListItemBtnEditConta);
		
		
		Date range = dateRange.getTime();
		GregorianCalendar cartaoDateRange = null;
		
		String debitos = null;
		String creditos = null;
		float debitoSum = 0;
		float creditoSum = 0;
		
		if(cc.getId_TipoConta() == 4){
			
			cartao = app.getDatabase().select(CartaoCredito.class, " WHERE id_Conta = "+cc.getId()).get(0);
			cartaoDateRange = (GregorianCalendar)dateRange.clone();
			if(cartao.getDia_fechamento() <= cartao.getDia_vencimento())
				cartaoDateRange.add(GregorianCalendar.MONTH, -2);
			else
				cartaoDateRange.add(GregorianCalendar.MONTH, -3);
			cartaoDateRange.set(GregorianCalendar.DAY_OF_MONTH, cartao.getDia_fechamento());
			range = cartaoDateRange.getTime();
			cartaoDateRange.add(GregorianCalendar.MONTH, 1);
			
			btnEditConta.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final CartaoCredito cartaoFinal = app.getDatabase().select(CartaoCredito.class, " WHERE id_Conta = "+cc.getId()).get(0);
					MessageUtils.showEditCartaoCredito(inflater.getContext(), cc, cartaoFinal, inflater, app.getDatabase(), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							contas.clear();
							contas.addAll(app.getDatabase().select(Conta.class));
							notifyDataSetChanged();
						}
					});				
				}
			});
		}else{
			creditos = app.getDatabase().runQuery(QuerysUtil.sumTransacoesCreditoFromContaWithDateInterval(cc.getId(),range));
			if(creditos != null && creditos.length() > 0) creditoSum = Float.valueOf(creditos);
			btnEditConta.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					MessageUtils.showEditConta(inflater.getContext(), cc, inflater, app.getDatabase(), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							contas.clear();
							contas.addAll(app.getDatabase().select(Conta.class));
							notifyDataSetChanged();
						}
					});				
				}
			});
		}		
		
		debitos = app.getDatabase().runQuery(QuerysUtil.sumTransacoesDebitoFromContaWithDateInterval(cc.getId(),range));
		if(debitos != null && debitos.length() > 0)  debitoSum = Float.valueOf(debitos);
		
		
		String saldo = app.getDatabase().runQuery(QuerysUtil.computeSaldoFromContaBeforeDate(cc.getId(), cc.getId_TipoConta() == 4 ? cartaoDateRange.getTime() : range));
		float saldoAnterior = 0;
		if(saldo != null && saldo.length() > 0) saldoAnterior = Float.valueOf(saldo);
		
		
		if(cc.getId_TipoConta() == 4){
			final Fatura f1 = CartaoBusiness.computeFatura(app.getDatabase(), cartao, cartaoDateRange);
			
			final BootstrapButton btnPagarConta = (BootstrapButton) view.findViewById(R.id.contaListItemBtnPagarFatura);
			btnPagarConta.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					f1.setDate(dateRange);
					MessageUtils.showPagarFatura(inflater.getContext(), inflater, app.getDatabase(), f1, cc, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							btnPagarConta.setVisibility(View.GONE);
							txtSaldo.setText(R.string.contas_fragment_fatura_status_paga);	
						}
					});
				}
			});			
			
			txtCreditos.setText("limite: R$ "+String.format("%.2f",f1.getLimite()));
			txtDebitos.setText("fatura: R$ "+String.format("%.2f",f1.getValor()));
			
			switch (f1.getStatus()) {
			case PAGA:
				txtSaldo.setText(R.string.contas_fragment_fatura_status_paga);				
				break;
				
			case PENDENTE:
				btnPagarConta.setVisibility(View.VISIBLE);
				txtSaldo.setText(R.string.contas_fragment_fatura_status_pendente);
				break;
				
			case NENHUM:
				txtSaldo.setText("-");
				break;		

			default:
				break;
			}
		}else{
			txtCreditos.setText("R$ "+String.format("%.2f",creditoSum));
			txtDebitos.setText("R$ "+String.format("%.2f",debitoSum));
			txtSaldo.setText("R$ "+String.format("%.2f",(saldoAnterior+creditoSum-debitoSum)));
		}
		
		return view;
	}

}
