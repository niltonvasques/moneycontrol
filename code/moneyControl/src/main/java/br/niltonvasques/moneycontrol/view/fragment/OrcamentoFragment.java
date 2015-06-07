package br.niltonvasques.moneycontrol.view.fragment;

import java.util.Date;
import java.util.List;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.Orcamento;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.view.adapter.OrcamentoAdapter;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView.ChangeMonthListener;
import br.niltonvasques.moneycontrolbeta.R;

public class OrcamentoFragment extends Fragment{
	
	private static final String TAG = "[CategoriasFragment]";
	
	private MoneyControlApp app;
	private DatabaseHandler db;
	private LayoutInflater inflater;
	
	private List<Orcamento> orcamento;
	
	private ChangeMonthView monthView;
	private View myFragmentView;
	private ListView listViewOrcamento;
	private OrcamentoAdapter adapter;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.inflater = inflater;
		
		app = (MoneyControlApp) getActivity().getApplication();
		
		myFragmentView = inflater.inflate(R.layout.fragment_orcamento, container, false);
		
		db = app.getDatabase();
		
		loadComponentsFromXml();
		configureComponents();
			
		return myFragmentView;
	}
	
	private void loadComponentsFromXml() {
		monthView 	= (ChangeMonthView) myFragmentView.findViewById(R.id.orcamentoFragmentChangeMonthView);		
		listViewOrcamento = (ListView) myFragmentView.findViewById(R.id.categoriaFragmentListViewCategorias);
	}

	private void configureComponents() {
		
		monthView.setListener(new ChangeMonthListener() {
			@Override
			public void onMonthChange(Date time) {
				update();				
			}
		});
		
		orcamento = db.select(Orcamento.class, QuerysUtil.whereOrcamentoOnMonth(monthView.getDateRange().getTime()));
		
		adapter = new OrcamentoAdapter(orcamento, inflater, app);
		
		listViewOrcamento.setAdapter(adapter);
		listViewOrcamento.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				MessageUtils.showEditOrcamento(getActivity(), orcamento.get(position), OrcamentoFragment.this.inflater, db, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						update();
					}
				});
			}
		});
		
		listViewOrcamento.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,final int position, long arg3) {
				MessageUtils.showMessageYesNo(getActivity(), "Atenção!", "Deseja excluir esta Rentabilidade?", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Orcamento at = orcamento.get(position);
						db.delete(at);
						update();
					}
				});
				return false;
			}
		});
		
		update();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main_activity_actions, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_transfer).setVisible(false);
		super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected");
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_add:
	        	MessageUtils.showAddOrcamento(getActivity(), inflater, db, new OnClickListener() {
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
	
	private void update(){
		orcamento.clear();
		orcamento.addAll(db.select(Orcamento.class, QuerysUtil.whereOrcamentoOnMonth(monthView.getDateRange().getTime())));
		adapter.notifyDataSetChanged();
		
		float credito = 0;
		String creditoQuery = db.runQuery(QuerysUtil.sumOrcamentoOnMonth(monthView.getDateRange().getTime()));
		if(creditoQuery != null && creditoQuery.length() > 0){
			credito = Float.valueOf(creditoQuery);
		}
		
		float debito = 0;
		String debitoQuery = db.runQuery(QuerysUtil.sumContasDebitoWithDateInterval(monthView.getDateRange().getTime()));
		if(debitoQuery != null && debitoQuery.length() > 0){
			debito = Float.valueOf(debitoQuery);
		}
		
		float restante = credito - debito;
		
		((TextView)myFragmentView.findViewById(R.id.mainActivityTxtSaldoSum)).setText("R$ "+String.format("%.2f",restante));
		((TextView)myFragmentView.findViewById(R.id.mainActivityTxtCreditosSum)).setText("R$ "+String.format("%.2f",credito));
		((TextView)myFragmentView.findViewById(R.id.mainActivityTxtDebitosSum)).setText("R$ "+String.format("%.2f",debito));
	}
	
}
