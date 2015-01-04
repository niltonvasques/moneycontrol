package br.niltonvasques.moneycontrol.view.fragment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.activity.NVFragmentActivity;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;
import br.niltonvasques.moneycontrol.database.bean.Transacao;
import br.niltonvasques.moneycontrol.util.DateUtil;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.view.adapter.ExpandableListAdapter;
import br.niltonvasques.moneycontrol.view.adapter.TransacaoAdapter;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView.ChangeMonthListener;
import br.niltonvasques.moneycontrol.R;

public class TransacoesFragment extends Fragment{
	
	public static final String TAG = "[TransacaoesFragment]";
	
	private DatabaseHandler db;
	private MoneyControlApp app;
	private LayoutInflater inflater;
	
	private List<Transacao> transacoes;
	
	private View myFragmentView;
	private ChangeMonthView monthView;
	private ListView listViewTransacoes;
	private TransacaoAdapter listAdapter;
	private ExpandableListView expandableListView;
	private ExpandableListAdapter expandableAdapter;
	
	
	List<CategoriaTransacao> groupList = new LinkedList<CategoriaTransacao>();
    List<Transacao> childList;
    Map<CategoriaTransacao, List<Transacao>> categoriasCollection = new LinkedHashMap<CategoriaTransacao, List<Transacao>>();
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.inflater = inflater;
		
		myFragmentView = inflater.inflate(R.layout.fragment_transacaoes, null);
		loadComponentsFromXml();
		((NVFragmentActivity)getActivity()).getSupportActionBar().setTitle("Transações");
		
		app = (MoneyControlApp) getActivity().getApplication();
		db = app.getDatabase();
		
		String range = getArguments().getString("range");
		try {
			monthView.getDateRange().setTime(DateUtil.sqlDateFormat().parse(range));
			monthView.updateDateRange();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		configureComponents();
		
		return myFragmentView;
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	private void configureComponents() {
		
		monthView.setListener(new ChangeMonthListener() {
			@Override
			public void onMonthChange(Date time) {
				update();				
			}
		});
        
		transacoes = db.select(Transacao.class,QuerysUtil.whereTransacaoWithDateInterval(monthView.getDateRange().getTime()));
		
		listAdapter = new TransacaoAdapter(transacoes, inflater, app);
		listViewTransacoes.setAdapter(listAdapter);
		listViewTransacoes.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				Transacao t = transacoes.get(position);
				MessageUtils.showEditTransacao(getActivity(), t, inflater, db, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						update();
					}
				});
			}
		});
		
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
		
        updateCollection();
        expandableAdapter = new ExpandableListAdapter(getActivity(), app, groupList, categoriasCollection);
        expandableListView.setAdapter(expandableAdapter);
        
        expandableListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {
				
				Transacao t = (Transacao)expandableAdapter.getChild(groupPosition, childPosition);
				MessageUtils.showEditTransacao(getActivity(), t, inflater, db, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						update();
					}
				});
				return true;
			}
		});
 
	}

	private void loadComponentsFromXml() {
		monthView 	= (ChangeMonthView) myFragmentView.findViewById(R.id.transacoesFragmentChangeMonthView);
		listViewTransacoes = (ListView) myFragmentView.findViewById(R.id.transacoesActivityListViewTransacoes);
		expandableListView = (ExpandableListView) myFragmentView.findViewById(R.id.transacoesFragmentExpandableListViewTransacoes);
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
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_add:
	        	MessageUtils.showAddTransacao(getActivity(), inflater, db, 0, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						update();
					}
				});
	            return true;
	            
	        case R.id.action_group:
	        	if(item.getTitle().equals(getActivity().getString(R.string.action_group))){
	        		item.setTitle(getActivity().getString(R.string.action_disgroup));
		        	listViewTransacoes.setVisibility(View.GONE);
		        	expandableListView.setVisibility(View.VISIBLE);
	        	}else{
	        		item.setTitle(getActivity().getString(R.string.action_group));
		        	listViewTransacoes.setVisibility(View.VISIBLE);
		        	expandableListView.setVisibility(View.GONE);
	        	}
	        	
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void update(){
		
		transacoes.clear();
		transacoes.addAll(db.select(Transacao.class,QuerysUtil.whereTransacaoWithDateInterval(monthView.getDateRange().getTime())));
		listAdapter.notifyDataSetChanged();
		
		updateCollection();
		expandableAdapter.notifyDataSetChanged();
		
		float credito = 0;
		String creditoQuery = db.runQuery(QuerysUtil.sumContasCreditoWithDateInterval(monthView.getDateRange().getTime()));
		if(creditoQuery != null && creditoQuery.length() > 0){
			credito = Float.valueOf(creditoQuery);
		}
		
		float debito = 0;
		String debitoQuery = db.runQuery(QuerysUtil.sumContasDebitoWithDateInterval(monthView.getDateRange().getTime()));
		if(debitoQuery != null && debitoQuery.length() > 0){
			debito = Float.valueOf(debitoQuery);
		}
		
		String saldo = db.runQuery(QuerysUtil.computeSaldoBeforeDate(monthView.getDateRange().getTime()));
		float saldoSum = 0;
		if(saldo != null && saldo.length() > 0){
			saldoSum = Float.valueOf(saldo);
		}		
		
		saldo = db.runQuery(QuerysUtil.sumSaldoContas());
		if(saldo != null && saldo.length() > 0){
			saldoSum += Float.valueOf(saldo);
		}
		
		saldoSum += credito - debito;
		
		((TextView)myFragmentView.findViewById(R.id.transacoesActivityTxtDebitosSum)).setText("R$ "+String.format("%.2f", debito));
		((TextView)myFragmentView.findViewById(R.id.transacoesActivityTxtCreditosSum)).setText("R$ "+String.format("%.2f",credito));
		((TextView)myFragmentView.findViewById(R.id.transacoesActivityTxtSaldoSum)).setText("R$ "+String.format("%.2f",saldoSum));
	}
	
    private void updateCollection() {
    	groupList.clear();
    	groupList.addAll(db.select(CategoriaTransacao.class));
    	categoriasCollection.clear();
    	
    	List<CategoriaTransacao> emptys = new ArrayList<CategoriaTransacao>();
 
        for (CategoriaTransacao categoria : groupList) {
        	childList = db.select(Transacao.class, QuerysUtil.whereTransacaoWithDateIntervalAndCategoria(categoria.getId(), monthView.getDateRange().getTime()));
        	if(childList.isEmpty()){
        		emptys.add(categoria);
        	}else{
        		categoriasCollection.put(categoria, childList);
        	}
        }
        
        for (CategoriaTransacao categoriaTransacao : emptys) {
        	groupList.remove(categoriaTransacao);
        }
    }

}
