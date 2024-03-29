package br.niltonvasques.moneycontrol.view.fragment;

import java.util.List;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import br.niltonvasques.moneycontrol.MainActivity;
import br.niltonvasques.moneycontrol.activity.NVFragmentActivity;
import br.niltonvasques.moneycontrolbeta.R;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;
import br.niltonvasques.moneycontrol.util.MessageUtils;

public class CategoriasFragment extends Fragment{
	
	private static final String TAG = "[CategoriasFragment]";
	
	private MoneyControlApp app;
	private DatabaseHandler db;
	private LayoutInflater inflater;
	
	private List<CategoriaTransacao> categorias;
	
	private View myFragmentView;
	private ListView listViewContas;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.inflater = inflater;
		
		app = (MoneyControlApp) getActivity().getApplication();
		
		myFragmentView = inflater.inflate(R.layout.fragment_categoria, container, false);
		
		db = app.getDatabase();
		
		categorias = db.select(CategoriaTransacao.class, QuerysUtil.whereNoSystemCategoriasSorted());

		Object[] cats = categorias.stream().map(c -> c + " " + c.tipo()).toArray();
		
		listViewContas = myFragmentView.findViewById(R.id.categoriaFragmentListViewCategorias);
		listViewContas.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, cats));
		listViewContas.setOnItemClickListener((arg0, arg1, position, arg3) ->
				MessageUtils.showEditCategoria(getActivity(), categorias.get(position),
						CategoriasFragment.this.inflater, db, (dialog, which) -> update()));
		
		return myFragmentView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		((NVFragmentActivity)getActivity()).getSupportActionBar().setIcon(R.drawable.ic_launcher);
		((NVFragmentActivity)getActivity()).getSupportActionBar().setTitle(getResources().getStringArray(R.array.menu_array)[MainActivity.CATEGORIAS_ITEM_MENU]);
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
		////Log.d(TAG, "onOptionsItemSelected");
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_add:
	        	MessageUtils.showAddCategoria(getActivity(), inflater, db, new OnClickListener() {
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
		categorias.clear();
		categorias.addAll(db.select(CategoriaTransacao.class));
		((ArrayAdapter)listViewContas.getAdapter()).notifyDataSetChanged();
	}

}
