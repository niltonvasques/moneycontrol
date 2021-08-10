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
import br.niltonvasques.moneycontrol.util.NumberUtil;
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
		monthView 	= myFragmentView.findViewById(R.id.reportInvestimentosFragmentChangeMonthView);
		listViewAtivos = myFragmentView.findViewById(R.id.transacoesActivityListViewTransacoes);
	}

	private void configureComponents() {
		
		monthView.setListener(time -> update());
        
        ativos = db.select(Ativo.class, "WHERE (SELECT count(*) FROM MovimentacaoAtivo WHERE id_Ativo = Ativo.id) > 0");

		listAdapter = new AtivoAdapter(ativos, getActivity(), monthView.getDateRange(), inflater, app);
		listViewAtivos.setAdapter(listAdapter);
		listViewAtivos.setOnItemClickListener((arg0, arg1, position, arg3) -> {
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
		super.onCreateOptionsMenu(menu, inflater);
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

		TextView txtSaldo = myFragmentView.findViewById(R.id.transacoesActivityTxtSaldoSum);
		txtSaldo.setText("R$ "+ NumberUtil.format(sum));
	}

}
