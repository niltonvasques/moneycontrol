package br.niltonvasques.moneycontrol.view.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.niltonvasques.moneycontrol.R;
import br.niltonvasques.moneycontrol.activity.NVFragmentActivity;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;

public class ReportsFragment extends Fragment{
	
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
				if(item.equals("Despesas por Categoria")){
					Fragment fragment = new ReportByCategoriasFragment();
					Bundle args = new Bundle();
					args.putInt("TipoTransacao", 2);
					fragment.setArguments(args);
					((NVFragmentActivity)getActivity()).changeFragment(fragment);
				}else if(item.equals("Receitas por Categoria")){
					Fragment fragment = new ReportByCategoriasFragment();
					Bundle args = new Bundle();
					args.putInt("TipoTransacao", 1);
					fragment.setArguments(args);
					((NVFragmentActivity)getActivity()).changeFragment(fragment);
				}
			}
		});
		
		return myFragmentView;
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_add).setVisible(false);
		super.onPrepareOptionsMenu(menu);
	}
	

}
