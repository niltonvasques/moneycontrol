package br.niltonvasques.moneycontrol;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;
import br.niltonvasques.moneycontrol.database.bean.Conta;
import br.niltonvasques.moneycontrol.database.bean.TipoTransacao;
import br.niltonvasques.moneycontrol.database.bean.Transacao;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.view.TransacaoAdapter;

public class TransacoesActivity extends Activity{
	
	private int idConta = -1;
	
	private DatabaseHandler db;
	private MoneyControlApp app;
	
	private ListView listViewTransacoes;
	private TransacaoAdapter listAdapter;
	
	private List<Transacao> transacoes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_transacaoes);
		
		idConta = getIntent().getIntExtra("conta", -1);
		
		app = (MoneyControlApp) getApplication();
		db = app.getDatabase();
		
		transacoes = db.select(Transacao.class," WHERE id_Conta = "+idConta);
		

		
		listViewTransacoes = (ListView) findViewById(R.id.transacoesActivityListViewTransacoes);
		listAdapter = new TransacaoAdapter(transacoes, getLayoutInflater(), app);
		listViewTransacoes.setAdapter(listAdapter);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		update();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.transacaoes_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_add:
	        	MessageUtils.showAddTransacao(TransacoesActivity.this, getLayoutInflater(), db, idConta, new OnClickListener() {
					
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
		transacoes.clear();
		transacoes.addAll(db.select(Transacao.class, "WHERE id_Conta = "+idConta));
		listAdapter.notifyDataSetChanged();
		
		float debitoSum = 0;
		float creditoSum = 0;
		for(Transacao cc : transacoes) {
			if(app.getCategoriasTransacao().get(cc.getId_CategoriaTransacao()).getId_TipoTransacao() == 2) debitoSum+= cc.getValor();
			else creditoSum += cc.getValor();
		}
		
		((TextView)findViewById(R.id.transacoesActivityTxtDebitosSum)).setText("R$ "+debitoSum);
		((TextView)findViewById(R.id.transacoesActivityTxtCreditosSum)).setText("R$ "+creditoSum);
		((TextView)findViewById(R.id.transacoesActivityTxtSaldoSum)).setText("R$ "+(creditoSum-debitoSum));
	}

}
