package br.niltonvasques.moneycontrol.view.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.R;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.Ativo;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;
import br.niltonvasques.moneycontrol.database.bean.Conta;
import br.niltonvasques.moneycontrol.database.bean.Transacao;
import br.niltonvasques.moneycontrol.util.DateUtil;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.view.adapter.AtivoAdapter;
import br.niltonvasques.moneycontrol.view.adapter.ExpandableListAdapter;
import br.niltonvasques.moneycontrol.view.adapter.TransacaoAdapter;

public class InvestimentosFragment extends Fragment{
	
	public static final String TAG = "[InvestimentosFragment]";
	
	private DatabaseHandler db;
	private MoneyControlApp app;
	private LayoutInflater inflater;
	
	private List<Ativo> ativos;
	private GregorianCalendar dateRange;
	
	private View myFragmentView;
	private TextView txtViewDateRange;
    private Button	btnNextMonth;
    private Button btnPreviousMonth;
	private ListView listViewAtivos;
	private AtivoAdapter listAdapter;
	
	
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.inflater = inflater;
		
		myFragmentView = inflater.inflate(R.layout.fragment_investimentos, null);
		getActivity().getActionBar().setTitle("Transações");
		
		app = (MoneyControlApp) getActivity().getApplication();
		db = app.getDatabase();
		
		String range = getArguments().getString("range");
		dateRange = new GregorianCalendar();
		dateRange.set(GregorianCalendar.DAY_OF_MONTH, 1);
		try {
			dateRange.setTime(DateUtil.sqlDateFormat().parse(range));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		loadComponentsFromXml();
		
		configureComponents();
		
		return myFragmentView;
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	private void configureComponents() {
		
		updateDateRange();
        
        btnPreviousMonth.setOnClickListener(mOnClick);
        btnNextMonth.setOnClickListener(mOnClick);
        
		ativos = db.select(Ativo.class);
		
		listAdapter = new AtivoAdapter(ativos, inflater, app);
		listViewAtivos.setAdapter(listAdapter);
		listViewAtivos.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				Ativo t = ativos.get(position);
//				MessageUtils.showEditTransacao(getActivity(), t, inflater, db, new OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						update();
//					}
//				});
			}
		});
		
		listViewAtivos.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			public boolean onItemLongClick(android.widget.AdapterView<?> arg0, View arg1, final int position, long arg3) {
//				MessageUtils.showMessageYesNo(getActivity(), "Atenção!", "Deseja excluir esta transação?", new OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						Transacao t = ativos.get(position);
//						db.delete(t);
//						update();
//					}
//				});
				return false;
			};
			
		});
		
 
	}

	private void loadComponentsFromXml() {
		txtViewDateRange 	= (TextView) myFragmentView.findViewById(R.id.transacoesFragmentTxtViewMonth);
		btnPreviousMonth 	= (Button) myFragmentView.findViewById(R.id.transacoesFragmentBtnPreviousMonth);
		btnNextMonth		= (Button) myFragmentView.findViewById(R.id.transacoesFragmentBtnNextMonth);
		listViewAtivos = (ListView) myFragmentView.findViewById(R.id.transacoesActivityListViewTransacoes);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		update();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main_transacaoes_actions, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_transfer).setVisible(false);
		super.onPrepareOptionsMenu(menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_add:
//	        	MessageUtils.showAddTransacao(getActivity(), inflater, db, 0, new OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						update();
//					}
//				});
	            return true;
	            
	        case R.id.action_group:
//	        	if(item.getTitle().equals(getActivity().getString(R.string.action_group))){
//	        		item.setTitle(getActivity().getString(R.string.action_disgroup));
//		        	listViewAtivos.setVisibility(View.GONE);
//		        	expandableListView.setVisibility(View.VISIBLE);
//	        	}else{
//	        		item.setTitle(getActivity().getString(R.string.action_group));
//		        	listViewAtivos.setVisibility(View.VISIBLE);
//		        	expandableListView.setVisibility(View.GONE);
//	        	}
	        	
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
		
		ativos.clear();
		ativos.addAll(db.select(Ativo.class));
		listAdapter.notifyDataSetChanged();
		
		float credito = 0;
//		String creditoQuery = db.runQuery(QuerysUtil.sumContasCreditoWithDateInterval(dateRange.getTime()));
//		if(creditoQuery != null && creditoQuery.length() > 0){
//			credito = Float.valueOf(creditoQuery);
//		}
		
		float debito = 0;
//		String debitoQuery = db.runQuery(QuerysUtil.sumContasDebitoWithDateInterval(dateRange.getTime()));
//		if(debitoQuery != null && debitoQuery.length() > 0){
//			debito = Float.valueOf(debitoQuery);
//		}
		
//		String saldo = db.runQuery(QuerysUtil.computeSaldoBeforeDate(dateRange.getTime()));
		float saldoSum = 0;
//		if(saldo != null && saldo.length() > 0){
//			saldoSum = Float.valueOf(saldo);
//		}		
		
//		saldo = db.runQuery(QuerysUtil.sumSaldoContas());
//		if(saldo != null && saldo.length() > 0){
//			saldoSum += Float.valueOf(saldo);
//		}
		
		saldoSum += credito - debito;
		
		((TextView)myFragmentView.findViewById(R.id.transacoesActivityTxtDebitosSum)).setText("R$ "+String.format("%.2f", debito));
		((TextView)myFragmentView.findViewById(R.id.transacoesActivityTxtCreditosSum)).setText("R$ "+String.format("%.2f",credito));
		((TextView)myFragmentView.findViewById(R.id.transacoesActivityTxtSaldoSum)).setText("R$ "+String.format("%.2f",saldoSum));
	}
	
	private void updateDateRange() {
		SimpleDateFormat format2 = new SimpleDateFormat("MMMMM - yyyy");
	    txtViewDateRange.setText(format2.format(dateRange.getTime()));
	}

}
