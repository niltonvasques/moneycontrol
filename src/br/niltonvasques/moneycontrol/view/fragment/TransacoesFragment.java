package br.niltonvasques.moneycontrol.view.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.R;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.Transacao;
import br.niltonvasques.moneycontrol.util.DateUtil;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.view.adapter.TransacaoAdapter;

public class TransacoesFragment extends Fragment{
	
	private int idConta = -1;
	
	private DatabaseHandler db;
	private MoneyControlApp app;
	private LayoutInflater inflater;
	
	private List<Transacao> transacoes;
	private GregorianCalendar dateRange;
	
	private View myFragmentView;
	private TextView txtViewDateRange;
    private Button	btnNextMonth;
    private Button btnPreviousMonth;
	private ListView listViewTransacoes;
	private TransacaoAdapter listAdapter;
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.inflater = inflater;
		
		myFragmentView = inflater.inflate(R.layout.fragment_transacaoes, null);
		getActivity().getActionBar().setTitle("Transações");
		
		app = (MoneyControlApp) getActivity().getApplication();
		db = app.getDatabase();
		idConta = getArguments().getInt("conta");
		
		String range = getArguments().getString("range");
		dateRange = new GregorianCalendar();
		dateRange.set(GregorianCalendar.DAY_OF_MONTH, 1);
		try {
			dateRange.setTime(DateUtil.sqlDateFormat().parse(range));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		loadComponentsFromXml();
		
		configureComponents(inflater);
		
		
		return myFragmentView;
		
	}

	private void configureComponents(LayoutInflater inflater) {
		
		updateDateRange();
        
        btnPreviousMonth.setOnClickListener(mOnClick);
        btnNextMonth.setOnClickListener(mOnClick);
        
		transacoes = db.select(Transacao.class,QuerysUtil.whereTransacaoFromContaWithDateInterval(idConta, dateRange.getTime()));
		
		listAdapter = new TransacaoAdapter(transacoes, dateRange, inflater, app);
		listViewTransacoes.setAdapter(listAdapter);
		listViewTransacoes.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			public boolean onItemLongClick(android.widget.AdapterView<?> arg0, View arg1, final int position, long arg3) {
				MessageUtils.showMessageYesNo(getActivity(), "Atenção!", "Deseja excluir esta transação?", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Transacao t = transacoes.get(position);
						db.delete(t);
						update();
					}
				});
				return false;
			};
			
		});
	}

	private void loadComponentsFromXml() {
		txtViewDateRange 	= (TextView) myFragmentView.findViewById(R.id.transacoesFragmentTxtViewMonth);
		btnPreviousMonth 	= (Button) myFragmentView.findViewById(R.id.transacoesFragmentBtnPreviousMonth);
		btnNextMonth		= (Button) myFragmentView.findViewById(R.id.transacoesFragmentBtnNextMonth);
		listViewTransacoes = (ListView) myFragmentView.findViewById(R.id.transacoesActivityListViewTransacoes);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		update();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_add:
	        	MessageUtils.showAddTransacao(getActivity(), inflater, db, idConta, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						update();
					}
				});
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private View.OnClickListener mOnClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.transacoesFragmentBtnPreviousMonth:
				dateRange.add(GregorianCalendar.MONTH, -1);
				updateDateRange();
				update();
				break;
				
			case R.id.transacoesFragmentBtnNextMonth:
				dateRange.add(GregorianCalendar.MONTH, +1);
				updateDateRange();
				update();
				break;

			default:
				break;
			}
			
		}
	};
	
	private void update(){
		transacoes.clear();
		transacoes.addAll(db.select(Transacao.class,QuerysUtil.whereTransacaoFromContaWithDateInterval(idConta, dateRange.getTime())));
		listAdapter.notifyDataSetChanged();
		
		float debitoSum = 0;
		float creditoSum = 0;
		String debitos = db.runQuery(QuerysUtil.sumTransacoesDebitoFromContaWithDateInterval(idConta,dateRange.getTime()));
		String creditos = db.runQuery(QuerysUtil.sumTransacoesCreditoFromContaWithDateInterval(idConta,dateRange.getTime()));
		
		if(debitos != null && debitos.length() > 0)  debitoSum = Float.valueOf(debitos);
		if(creditos != null && creditos.length() > 0) creditoSum = Float.valueOf(creditos);
		
		String saldo = db.runQuery(QuerysUtil.computeSaldoFromContaBeforeDate(idConta, dateRange.getTime()));
		float saldoAnterior = 0;
		if(saldo != null && saldo.length() > 0) saldoAnterior = Float.valueOf(saldo);
		
		((TextView)myFragmentView.findViewById(R.id.transacoesActivityTxtDebitosSum)).setText("R$ "+debitoSum);
		((TextView)myFragmentView.findViewById(R.id.transacoesActivityTxtCreditosSum)).setText("R$ "+creditoSum);
		((TextView)myFragmentView.findViewById(R.id.transacoesActivityTxtSaldoSum)).setText("R$ "+(saldoAnterior+creditoSum-debitoSum));
	}
	
	private void updateDateRange() {
		SimpleDateFormat format2 = new SimpleDateFormat("MMMMM - yyyy");
	    txtViewDateRange.setText(format2.format(dateRange.getTime()));
	}

}
