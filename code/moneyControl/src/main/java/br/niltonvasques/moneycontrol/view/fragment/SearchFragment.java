package br.niltonvasques.moneycontrol.view.fragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import br.niltonvasques.moneycontrol.MainActivity;
import br.niltonvasques.moneycontrol.activity.NVFragmentActivity;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.Transacao;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.view.adapter.TransacaoAdapter;
import br.niltonvasques.moneycontrolbeta.R;

public class SearchFragment extends Fragment{
	
	public static final String TAG = "[SearchFragment]";
	
	private DatabaseHandler db;
	private MoneyControlApp app;
	private LayoutInflater inflater;
	
	private List<Transacao> transacoes;
	
	private View myFragmentView;
	private EditText editTxtBusca;
	private Button btnBuscar;
	private ListView listViewTransacoes;
	private TransacaoAdapter listAdapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.inflater = inflater;
		
		myFragmentView = inflater.inflate(R.layout.fragment_busca, null);
		loadComponentsFromXml();
		((NVFragmentActivity)getActivity()).getSupportActionBar().setTitle("Busca");
		
		app = (MoneyControlApp) getActivity().getApplication();
		db = app.getDatabase();
		
		configureComponents();
		
		return myFragmentView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(false);
	}
	
	private boolean longClick = false;
	private void configureComponents() {
		transacoes = db.select(Transacao.class,QuerysUtil.whereTransacaoSearch(query()));
		
		listAdapter = new TransacaoAdapter(transacoes, inflater, app);
		listViewTransacoes.setAdapter(listAdapter);
		listViewTransacoes.setOnItemClickListener((arg0, arg1, position, arg3) -> {
			if(!longClick){
				Transacao t = transacoes.get(position);
				MessageUtils.showEditTransacao(getActivity(), t, inflater, db, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						update();
					}
				});
			}
			longClick = false;
		});
		
		listViewTransacoes.setOnItemLongClickListener((arg0, arg1, position, arg3) -> {
			longClick = true;
			MessageUtils.showMessageYesNo(getActivity(), app.getString(R.string.fragment_transacao_remove_dialog_title), app.getString(R.string.fragment_transacao_remove_dialog_message), new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Transacao t = transacoes.get(position);
db.deleteTransacao(t);
					update();
				}
			}, dialog -> longClick = false);
			return false;
		});
		
        btnBuscar.setOnClickListener(v -> {
			Toast.makeText(getActivity(), "Buscando... " + query(), Toast.LENGTH_LONG).show();
			update();
		});
 
	}

	private void loadComponentsFromXml() {
		listViewTransacoes = myFragmentView.findViewById(R.id.transacoesActivityListViewTransacoes);
		editTxtBusca = myFragmentView.findViewById(R.id.buscaFragmentEditTxtSearch);
		btnBuscar = myFragmentView.findViewById(R.id.buscaFragmentBtnBuscar);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		update();
		((NVFragmentActivity)getActivity()).getSupportActionBar().setIcon(R.drawable.ic_launcher);
	}

	private void update(){
		transacoes.clear();
		transacoes.addAll(db.select(Transacao.class,QuerysUtil.whereTransacaoSearch(query())));
		listAdapter.notifyDataSetChanged();
	}

	private String query() {
		String q = editTxtBusca.getText().toString();
		if (q == null || q.isEmpty()) return "nothing...";
		return q;
	}
}
