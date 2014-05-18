package br.niltonvasques.moneycontrol.view.fragment;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.R;
import br.niltonvasques.moneycontrol.activity.NVFragmentActivity;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.Conta;
import br.niltonvasques.moneycontrol.util.DateUtil;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.view.adapter.ContaAdapter;

public class ContasFragment extends Fragment{
	
	private static final String TAG = "[MainFragment]";
	
	private MoneyControlApp app;
	private DatabaseHandler db;
	private LayoutInflater inflater;
	
	private List<Conta> contas;
	private GregorianCalendar dateRange; 
	
	private View myFragmentView;
    private TextView txtViewDateRange;
    private Button	btnNextMonth;
    private Button btnPreviousMonth;
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
		txtViewDateRange 	= (TextView) myFragmentView.findViewById(R.id.principalFragmentTxtViewMonth);
		btnPreviousMonth 	= (Button) myFragmentView.findViewById(R.id.principalFragmentBtnPreviousMonth);
		btnNextMonth		= (Button) myFragmentView.findViewById(R.id.principalFragmentBtnNextMonth);		
		listViewContas 		= (ListView) myFragmentView.findViewById(R.id.mainActivityListViewContas);
	}

	private void configureComponents() {
		dateRange = new GregorianCalendar();
		dateRange.set(GregorianCalendar.DAY_OF_MONTH, 1);
		updateDateRange();
        
        btnPreviousMonth.setOnClickListener(mOnClick);
        btnNextMonth.setOnClickListener(mOnClick);
		
		contas = db.select(Conta.class);
		
		listAdapter = new ContaAdapter(contas, dateRange, inflater, app);
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
				Fragment fragment = new TransacoesByContaFragment();
				Bundle args = new Bundle();
				args.putInt("conta", contas.get(position).getId());
				args.putString("range", DateUtil.sqlDateFormat().format(dateRange.getTime()));
				fragment.setArguments(args);
				((NVFragmentActivity)getActivity()).changeFragment(fragment);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		update();
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
	
	private View.OnClickListener mOnClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.principalFragmentBtnPreviousMonth:
				dateRange.add(GregorianCalendar.MONTH, -1);
				updateDateRange();
				update();
				break;
				
			case R.id.principalFragmentBtnNextMonth:
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
		contas.clear();
		contas.addAll(db.select(Conta.class));
		listAdapter.notifyDataSetChanged();
		
		float credito = 0;
		String creditoQuery = db.runQuery(QuerysUtil.sumContasCreditoWithDateInterval(dateRange.getTime()));
		if(creditoQuery != null && creditoQuery.length() > 0){
			credito = Float.valueOf(creditoQuery);
		}
		
		float debito = 0;
		String debitoQuery = db.runQuery(QuerysUtil.sumContasDebitoWithDateInterval(dateRange.getTime()));
		if(debitoQuery != null && debitoQuery.length() > 0){
			debito = Float.valueOf(debitoQuery);
		}
		
		String saldo = db.runQuery(QuerysUtil.computeSaldoBeforeDate(dateRange.getTime()));
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

	private void updateDateRange() {
		SimpleDateFormat format2 = new SimpleDateFormat("MMMMM - yyyy");
	    txtViewDateRange.setText(format2.format(dateRange.getTime()));
	}

}
