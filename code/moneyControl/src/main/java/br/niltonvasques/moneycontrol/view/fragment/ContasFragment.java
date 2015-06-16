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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import br.niltonvasques.moneycontrol.MainActivity;
import br.niltonvasques.moneycontrol.activity.NVFragmentActivity;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.Conta;
import br.niltonvasques.moneycontrol.util.DateUtil;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.view.adapter.ContaAdapter;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView.ChangeMonthListener;
import br.niltonvasques.moneycontrolbeta.R;

public class ContasFragment extends Fragment{
	
	private static final String TAG = "[MainFragment]";
	
	private MoneyControlApp app;
	private DatabaseHandler db;
	private LayoutInflater inflater;
	
	private List<Conta> contas;
	
	private View myFragmentView;
    private ChangeMonthView monthView;
	private ListView listViewContas;
	private ContaAdapter listAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.inflater = inflater;
		
		app = (MoneyControlApp) getActivity().getApplication();
		db = app.getDatabase();
		
		myFragmentView = inflater.inflate(R.layout.fragment_principal, container, false);
		loadComponentsFromXml();
		
		configureComponents();
		
		return myFragmentView;
	}

	private void loadComponentsFromXml() {
		monthView 	= (ChangeMonthView) myFragmentView.findViewById(R.id.principalFragmentChangeMonthView);
		listViewContas 		= (ListView) myFragmentView.findViewById(R.id.mainActivityListViewContas);
	}

	private boolean longClick = false;
	private void configureComponents() {
        
		monthView.setListener(new ChangeMonthListener() {
			@Override
			public void onMonthChange(Date time) {
				update();				
			}
		});
		
		contas = db.select(Conta.class);
		
		listAdapter = new ContaAdapter(contas, monthView.getDateRange(), inflater, app);
		listViewContas.setAdapter(listAdapter);
		
		listViewContas.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
				longClick = true;
				MessageUtils.showMessageYesNo(getActivity(), app.getString(R.string.contas_fragment_message_dialog_atention_title), app.getString(R.string.contas_fragment_remove_account_msg), new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						db.deleteConta(contas.get(position));
						update();
					}
				}, new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						longClick = false;
					}
				});
				return false;
			}
		});
		
		listViewContas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if (!longClick) {
					Fragment fragment = new TransacoesByContaFragment();
					Bundle args = new Bundle();
					args.putInt("conta", contas.get(position).getId());
					args.putString("range", DateUtil.sqlDateFormat().format(monthView.getDateRange().getTime()));
					fragment.setArguments(args);
					((NVFragmentActivity) getActivity()).changeFragment(fragment);
				}
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		update();
		((NVFragmentActivity)getActivity()).getSupportActionBar().setIcon(R.drawable.ic_launcher);
		((NVFragmentActivity)getActivity()).getSupportActionBar().setTitle(getResources().getStringArray(R.array.menu_array)[MainActivity.CONTAS_ITEM_MENU]);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main_activity_actions, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected");
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_add:
	        	MessageUtils.showAddConta(getActivity(), inflater, db, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						update();
					}
				});
	            return true;
	            
	        case R.id.action_transfer:
	        	if(db.select(Conta.class).size() >= 2){
		        	MessageUtils.showTransferencia(getActivity(), inflater, db, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							update();
						}
					});
	        	}else{
	        		MessageUtils.showMessage(getActivity(), getString(R.string.contas_fragment_message_dialog_atention_title), getString(R.string.contas_fragment_message_dialog_transfer_error_msg));
	        	}
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void update(){
		contas.clear();
		contas.addAll(db.select(Conta.class));
		listAdapter.notifyDataSetChanged();
		
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
		
		for(Conta cc : contas) saldoSum+= cc.getSaldo();
		
		saldoSum += credito - debito;
		
		((TextView)myFragmentView.findViewById(R.id.mainActivityTxtSaldoSum)).setText("R$ "+String.format("%.2f",saldoSum));
		((TextView)myFragmentView.findViewById(R.id.mainActivityTxtCreditosSum)).setText("R$ "+String.format("%.2f",credito));
		((TextView)myFragmentView.findViewById(R.id.mainActivityTxtDebitosSum)).setText("R$ "+String.format("%.2f",debito));
	}


}
