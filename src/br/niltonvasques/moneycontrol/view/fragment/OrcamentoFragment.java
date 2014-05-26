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
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import br.niltonvasques.moneycontrol.R;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.Orcamento;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.view.adapter.OrcamentoAdapter;

public class OrcamentoFragment extends Fragment{
	
	private static final String TAG = "[CategoriasFragment]";
	
	private MoneyControlApp app;
	private DatabaseHandler db;
	private LayoutInflater inflater;
	private GregorianCalendar dateRange; 
	
	private List<Orcamento> orcamento;
	
    private TextView txtViewDateRange;
    private Button	btnNextMonth;
    private Button btnPreviousMonth;
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
		txtViewDateRange 	= (TextView) myFragmentView.findViewById(R.id.principalFragmentTxtViewMonth);
		btnPreviousMonth 	= (Button) myFragmentView.findViewById(R.id.principalFragmentBtnPreviousMonth);
		btnNextMonth		= (Button) myFragmentView.findViewById(R.id.principalFragmentBtnNextMonth);		
		listViewOrcamento = (ListView) myFragmentView.findViewById(R.id.categoriaFragmentListViewCategorias);
	}

	private void configureComponents() {
		dateRange = new GregorianCalendar();
		dateRange.set(GregorianCalendar.DAY_OF_MONTH, 1);
		updateDateRange();
        btnPreviousMonth.setOnClickListener(mOnClick);
        btnNextMonth.setOnClickListener(mOnClick);
		
		orcamento = db.select(Orcamento.class, QuerysUtil.whereOrcamentoOnMonth(dateRange.getTime()));
		
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
		orcamento.addAll(db.select(Orcamento.class, QuerysUtil.whereOrcamentoOnMonth(dateRange.getTime())));
		adapter.notifyDataSetChanged();
		
		float credito = 0;
		String creditoQuery = db.runQuery(QuerysUtil.sumOrcamentoOnMonth(dateRange.getTime()));
		if(creditoQuery != null && creditoQuery.length() > 0){
			credito = Float.valueOf(creditoQuery);
		}
		
		float debito = 0;
		String debitoQuery = db.runQuery(QuerysUtil.sumContasDebitoWithDateInterval(dateRange.getTime()));
		if(debitoQuery != null && debitoQuery.length() > 0){
			debito = Float.valueOf(debitoQuery);
		}
		
		float restante = credito - debito;
		
		((TextView)myFragmentView.findViewById(R.id.mainActivityTxtSaldoSum)).setText("R$ "+String.format("%.2f",restante));
		((TextView)myFragmentView.findViewById(R.id.mainActivityTxtCreditosSum)).setText("R$ "+String.format("%.2f",credito));
		((TextView)myFragmentView.findViewById(R.id.mainActivityTxtDebitosSum)).setText("R$ "+String.format("%.2f",debito));
	}
	
	private void updateDateRange() {
		SimpleDateFormat format2 = new SimpleDateFormat("MMMMM - yyyy");
	    txtViewDateRange.setText(format2.format(dateRange.getTime()));
	}

}
