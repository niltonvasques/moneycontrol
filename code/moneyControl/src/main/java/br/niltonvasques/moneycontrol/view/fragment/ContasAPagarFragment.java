package br.niltonvasques.moneycontrol.view.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import br.niltonvasques.moneycontrol.MainActivity;
import br.niltonvasques.moneycontrol.activity.NVFragmentActivity;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.bean.Conta;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrolbeta.R;

public class ContasAPagarFragment extends Fragment{
	
	
	private static final String TAG = "[AboutFragment]";
	
	private MoneyControlApp app;
	private DatabaseHandler db;
	private LayoutInflater inflater;
	
	private View myFragmentView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		((NVFragmentActivity)getActivity()).getSupportActionBar().setIcon(R.drawable.ic_launcher);
		((NVFragmentActivity)getActivity()).getSupportActionBar().setTitle(getResources().getStringArray(R.array.menu_array)[MainActivity.CONTAS_A_PAGAR_ITEM_MENU]);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.contas_a_pagar_actions, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected");
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_add:
				MessageUtils.showAddContaAPagar(getActivity(), inflater, db, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//						update();
					}
				});
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.inflater = inflater;
		
		app = (MoneyControlApp) getActivity().getApplication();
		
		myFragmentView = inflater.inflate(R.layout.fragment_contas_a_pagar, container, false);
		
		db = app.getDatabase();
		
		return myFragmentView;
	}
	

}
