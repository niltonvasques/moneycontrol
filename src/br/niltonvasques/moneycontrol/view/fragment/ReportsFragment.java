package br.niltonvasques.moneycontrol.view.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.niltonvasques.moneycontrol.R;
import br.niltonvasques.moneycontrol.activity.NVFragmentActivity;
import br.niltonvasques.moneycontrol.activity.TimeSeriesActivity;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.util.MessageUtils.MessageListener;

public class ReportsFragment extends Fragment{
	
	private static final int REPORT_DESPESAS = 0;
	private static final int REPORT_RECEITAS = 1;
	private static final int REPORT_CATEGORIAS = 2;
	private static final int REPORT_RECEITAS_X_DESPESAS_HISTORY = 3;
	private static final int REPORT_APORTES_HISTORY = 4;
	private static final int REPORT_ASSET_ALLOCATION = 5;
	
	private static final String TAG = "[CategoriasFragment]";
	
	private MoneyControlApp app;
	private DatabaseHandler db;
	private LayoutInflater inflater;
	
	private View myFragmentView;
	private ListView listViewReports;
	
	private static List<String> reports = new ArrayList<String>();
	
	static{
		reports.add("Despesas por Categoria");
		reports.add("Receitas por Categoria");
		reports.add("Histórico por Categoria");
		reports.add("Histórico Receitas X Despesas");
		reports.add("Histórico Aportes");
		reports.add("Alocação dos ativos");
	}
	
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
		
		myFragmentView = inflater.inflate(R.layout.fragment_categoria, container, false);
		
		db = app.getDatabase();
		
		
		listViewReports = (ListView) myFragmentView.findViewById(R.id.categoriaFragmentListViewCategorias);
		listViewReports.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, reports));
		
		listViewReports.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				
				String item = (String)arg0.getItemAtPosition(position);
				if(item.equals(reports.get(REPORT_DESPESAS))){
					Fragment fragment = new ReportByCategoriasFragment();
					Bundle args = new Bundle();
					args.putInt("TipoTransacao", 2);
					fragment.setArguments(args);
					((NVFragmentActivity)getActivity()).changeFragment(fragment);
				}else if(item.equals(reports.get(REPORT_RECEITAS))){
					Fragment fragment = new ReportByCategoriasFragment();
					Bundle args = new Bundle();
					args.putInt("TipoTransacao", 1);
					fragment.setArguments(args);
					((NVFragmentActivity)getActivity()).changeFragment(fragment);
				}else if(item.equals(reports.get(REPORT_CATEGORIAS))){
					MessageUtils.showCategoriasChooseDialog(getActivity(), ReportsFragment.this.inflater, db, new MessageListener() {
						@Override
						public void onMessage(int result, Object data) {
							app.setData(data);
							Intent it = new Intent(getActivity(), TimeSeriesActivity.class);
							startActivity(it);
						}
					} );
				}else if(item.equals(reports.get(REPORT_RECEITAS_X_DESPESAS_HISTORY))){
					Fragment fragment = new ReportReceitasDespesasFragment();
					Bundle args = new Bundle();
					args.putInt("TipoTransacao", 1);
					fragment.setArguments(args);
					((NVFragmentActivity)getActivity()).changeFragment(fragment);
				}else if(item.equals(reports.get(REPORT_APORTES_HISTORY))){
					((NVFragmentActivity)getActivity()).changeFragment(new ReportAportesFragment());
				}else if(item.equals(reports.get(REPORT_ASSET_ALLOCATION))){
					((NVFragmentActivity)getActivity()).changeFragment(new ReportByAtivosFragment());
				}
				
			}
		});
		
		return myFragmentView;
	}
	

}
