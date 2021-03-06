package br.niltonvasques.moneycontrol.view.fragment;

import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import br.niltonvasques.moneycontrol.MainActivity;
import br.niltonvasques.moneycontrol.activity.NVFragmentActivity;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.Ativo;
import br.niltonvasques.moneycontrol.database.bean.MovimentacaoAtivo;
import br.niltonvasques.moneycontrol.util.DateUtil;
import br.niltonvasques.moneycontrol.view.adapter.AtivoAdapter;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView.ChangeMonthListener;
import br.niltonvasques.moneycontrolbeta.R;

public class InvestimentosFragment extends Fragment{
	
	public static final String TAG = "[InvestimentosFragment]";
	
	private DatabaseHandler db;
	private MoneyControlApp app;
	private LayoutInflater inflater;
	
	private List<Ativo> ativos;
	
	private View myFragmentView;
	private ListView listViewAtivos;
	private ChangeMonthView monthView;
	private AtivoAdapter listAdapter;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.inflater = inflater;
		
		myFragmentView = inflater.inflate(R.layout.fragment_investimentos, container, false);
		
		app = (MoneyControlApp) getActivity().getApplication();
		db = app.getDatabase();
		
		loadComponentsFromXml();
		
		configureComponents();
		
		return myFragmentView;
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	private void loadComponentsFromXml() {
		monthView 	= (ChangeMonthView) myFragmentView.findViewById(R.id.reportInvestimentosFragmentChangeMonthView);
		listViewAtivos = (ListView) myFragmentView.findViewById(R.id.transacoesActivityListViewTransacoes);
	}

	private void configureComponents() {
		
		monthView.setListener(new ChangeMonthListener() {
			@Override
			public void onMonthChange(Date time) {
				update();				
			}
		});
        
//		ativos = db.select(Ativo.class, " WHERE data < date('"+DateUtil.sqlDateFormat().format(dateRange.getTime())+"','+1 month')");
        ativos = db.select(Ativo.class, "WHERE (SELECT count(*) FROM MovimentacaoAtivo WHERE id_Ativo = Ativo.id) > 0");
        for (Ativo a : ativos) {
			////Log.i(TAG, a.toString());
		}
		
		listAdapter = new AtivoAdapter(ativos, getActivity(), monthView.getDateRange(), inflater, app);
		listViewAtivos.setAdapter(listAdapter);
		listViewAtivos.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				Ativo ativo = ativos.get(position);
//				MessageUtils.showEditAtivo(getActivity(), ativo, inflater, db, new OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						update();
//					}
//				});
				Fragment fragment = new MovimentacaoAtivoFragment();
				Bundle args = new Bundle();
				args.putInt("id_Ativo", ativo.getId());
				args.putString("range", DateUtil.sqlDateFormat().format(monthView.getDateRange().getTime()));
				fragment.setArguments(args);
				((NVFragmentActivity)getActivity()).changeFragment(fragment);
			}
		});
		
//		listViewAtivos.setOnItemLongClickListener(new OnItemLongClickListener() {
//			
//			public boolean onItemLongClick(android.widget.AdapterView<?> arg0, View arg1, final int position, long arg3) {
//				MessageUtils.showMessageYesNo(getActivity(), "Atenção!", "Deseja excluir este ativo?", new OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						Ativo at = ativos.get(position);
////						Transacao tr = db.select(Transacao.class, " WHERE id = "+at.getId_Transacao()).get(0);
//						db.delete(at);
////						db.delete(tr);
//						update();
//					}
//				});
//				return false;
//			};
//			
//		});
		
 
	}

	@Override
	public void onResume() {
		super.onResume();
		update();
		((NVFragmentActivity)getActivity()).getSupportActionBar().setIcon(R.drawable.ic_launcher);
		((NVFragmentActivity)getActivity()).getSupportActionBar().setTitle(getResources().getStringArray(R.array.menu_array)[MainActivity.INVESTIMENTOS_ITEM_MENU]);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		inflater.inflate(R.menu.main_transacaoes_actions, menu);
		super.onCreateOptionsMenu(menu, inflater);
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
	
	private void update(){
		
		ativos.clear();
//		ativos.addAll(db.select(Ativo.class, " WHERE data < date('"+DateUtil.sqlDateFormat().format(dateRange.getTime())+"','+1 month')"));
		ativos.addAll(db.select(Ativo.class, "WHERE (SELECT count(*) FROM MovimentacaoAtivo WHERE id_Ativo = Ativo.id) > 0"));

		for(Ativo ativo : ativos){
			db.runQuery(QuerysUtil.updateMovimentacaoAtivos(ativo.getId()));
			db.runQuery(QuerysUtil.updateMovimentacaoAtivosWithFinanceiro(ativo.getId()));
		}
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
//		float saldoSum = 0;
//		if(saldo != null && saldo.length() > 0){
//			saldoSum = Float.valueOf(saldo);
//		}		
		
//		saldo = db.runQuery(QuerysUtil.sumSaldoContas());
//		if(saldo != null && saldo.length() > 0){
//			saldoSum += Float.valueOf(saldo);
//		}
		
		String sumStr = db.runQuery("SELECT SUM(patrimonio) " +
									"FROM (SELECT patrimonio " +
											"FROM ( SELECT * FROM MovimentacaoAtivo " +
													"WHERE data  < date('"+DateUtil.sqlDateFormat().format(monthView.getDateRange().getTime())+"','+1 month') "+
													" ORDER BY data ASC) "+
										   "GROUP BY id_Ativo)");
		
		float sum = 0;
		try{
			sum = Float.valueOf(sumStr);
		}catch(Exception e){}
		
//		saldoSum += credito - debito;
		
		((TextView)myFragmentView.findViewById(R.id.transacoesActivityTxtDebitosSum)).setText("R$ "+String.format("%.2f", debito));
		((TextView)myFragmentView.findViewById(R.id.transacoesActivityTxtCreditosSum)).setText("R$ "+String.format("%.2f",credito));
		((TextView)myFragmentView.findViewById(R.id.transacoesActivityTxtSaldoSum)).setText("R$ "+String.format("%.2f",sum));
	}

}
