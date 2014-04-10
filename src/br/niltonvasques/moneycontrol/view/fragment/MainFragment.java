package br.niltonvasques.moneycontrol.view.fragment;

import java.util.List;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.R;
import br.niltonvasques.moneycontrol.TransacoesFragment;
import br.niltonvasques.moneycontrol.activity.NVFragmentActivity;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.Conta;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.view.adapter.ContaAdapter;

public class MainFragment extends Fragment{
	
	private static final String TAG = "[MainFragment]";
	
	private MoneyControlApp app;
	private DatabaseHandler db;
	private LayoutInflater inflater;
	
	private List<Conta> contas;
	
	private View myFragmentView;
	private ListView listViewContas;
	private ContaAdapter listAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.inflater = inflater;
		
		app = (MoneyControlApp) getActivity().getApplication();
		
		myFragmentView = inflater.inflate(R.layout.fragment_principal, container, false);
		
		db = app.getDatabase();
		
		db.showTiposBem();
		
		contas = db.select(Conta.class);
		
		listViewContas = (ListView) myFragmentView.findViewById(R.id.mainActivityListViewContas);
		listAdapter = new ContaAdapter(contas, inflater, app);
		listViewContas.setAdapter(listAdapter);
		
		listViewContas.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,final int position, long arg3) {
				MessageUtils.showMessageYesNo(getActivity(), "Atenção!", "Deseja excuir esta conta?", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						db.deleteConta(contas.get(position));
						update();
					}
				});
				return false;
			}
		});
		
		listViewContas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				Fragment fragment = new TransacoesFragment();
				Bundle args = new Bundle();
				args.putInt("conta", contas.get(position).getId());
				fragment.setArguments(args);
				((NVFragmentActivity)getActivity()).changeFragment(fragment);

			}
		});
		
		return myFragmentView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		update();
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
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void update(){
		contas.clear();
		contas.addAll(db.select(Conta.class));
		listAdapter.notifyDataSetChanged();
		
		float credito = Float.valueOf(db.runQuery(QuerysUtil.SUM_CONTAS_CREDITO));
		float debito = Float.valueOf(db.runQuery(QuerysUtil.SUM_CONTAS_DEBITO));
		
		float saldoSum = 0;
		for(Conta cc : contas) saldoSum+= cc.getSaldo();
		
		saldoSum += credito - debito;
		
		((TextView)myFragmentView.findViewById(R.id.mainActivityTxtSaldoSum)).setText("R$ "+saldoSum);
		((TextView)myFragmentView.findViewById(R.id.mainActivityTxtCreditosSum)).setText("R$ "+credito);
		((TextView)myFragmentView.findViewById(R.id.mainActivityTxtDebitosSum)).setText("R$ "+debito);
	}

}
