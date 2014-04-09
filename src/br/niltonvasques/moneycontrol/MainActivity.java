package br.niltonvasques.moneycontrol;

import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.bean.Conta;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.view.ContaAdapter;

public class MainActivity extends Activity {

	private MoneyControlApp app;
	private DatabaseHandler db;
	
	private List<Conta> contas;
	
	private ListView listViewContas;
	private ContaAdapter listAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		app = (MoneyControlApp) getApplication();
		db = app.getDatabase();
		
		db.showTiposBem();
		
		contas = db.select(Conta.class);
		
		listViewContas = (ListView) findViewById(R.id.mainActivityListViewContas);
		listAdapter = new ContaAdapter(contas, getLayoutInflater(), app);
		listViewContas.setAdapter(listAdapter);
		
		listViewContas.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,final int position, long arg3) {
				MessageUtils.showMessageYesNo(MainActivity.this, "Atenção!", "Deseja excuir esta conta?", new OnClickListener() {
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
				Intent it = new Intent(MainActivity.this, TransacoesActivity.class);
				it.putExtra("conta", contas.get(position).getId());
				startActivity(it);
				
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		update();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_add:
	        	MessageUtils.showAddConta(this, getLayoutInflater(), db, new OnClickListener() {
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
		
		float saldoSum = 0;
		for(Conta cc : contas) saldoSum+= cc.getSaldo();
		
		((TextView)findViewById(R.id.mainActivityTxtSaldoSum)).setText("R$ "+saldoSum);
	}

}
