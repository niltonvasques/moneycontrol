package br.niltonvasques.moneycontrol;

import java.util.List;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.bean.Transacao;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.view.adapter.TransacaoAdapter;

public class TransacoesFragment extends Fragment{
	
	private int idConta = -1;
	
	private DatabaseHandler db;
	private MoneyControlApp app;
	private LayoutInflater inflater;
	
	private View myFragmentView;
	private ListView listViewTransacoes;
	private TransacaoAdapter listAdapter;
	
	private List<Transacao> transacoes;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.inflater = inflater;
		
		myFragmentView = inflater.inflate(R.layout.activity_transacaoes, null);
		
		idConta = getArguments().getInt("conta");
		
		app = (MoneyControlApp) getActivity().getApplication();
		db = app.getDatabase();
		
		transacoes = db.select(Transacao.class," WHERE id_Conta = "+idConta);
		
		listViewTransacoes = (ListView) myFragmentView.findViewById(R.id.transacoesActivityListViewTransacoes);
		listAdapter = new TransacaoAdapter(transacoes, inflater, app);
		listViewTransacoes.setAdapter(listAdapter);
		
		getActivity().getActionBar().setTitle("Transações");
		
		return myFragmentView;
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		update();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_add:
	        	MessageUtils.showAddTransacao(getActivity(), inflater, db, idConta, new OnClickListener() {
					
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
		
		((TextView)myFragmentView.findViewById(R.id.transacoesActivityTxtDebitosSum)).setText("R$ "+debitoSum);
		((TextView)myFragmentView.findViewById(R.id.transacoesActivityTxtCreditosSum)).setText("R$ "+creditoSum);
		((TextView)myFragmentView.findViewById(R.id.transacoesActivityTxtSaldoSum)).setText("R$ "+(creditoSum-debitoSum));
	}

}
