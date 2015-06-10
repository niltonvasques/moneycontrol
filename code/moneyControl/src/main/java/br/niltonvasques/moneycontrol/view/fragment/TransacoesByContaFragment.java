package br.niltonvasques.moneycontrol.view.fragment;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;

import br.niltonvasques.moneycontrol.database.bean.CartaoCredito;
import br.niltonvasques.moneycontrol.database.bean.Compra;
import br.niltonvasques.moneycontrolbeta.R;
import br.niltonvasques.moneycontrol.activity.NVFragmentActivity;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;
import br.niltonvasques.moneycontrol.database.bean.Conta;
import br.niltonvasques.moneycontrol.database.bean.Transacao;
import br.niltonvasques.moneycontrol.util.AssetUtil;
import br.niltonvasques.moneycontrol.util.DateUtil;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.view.adapter.ExpandableListAdapter;
import br.niltonvasques.moneycontrol.view.adapter.TransacaoAdapter;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView.ChangeMonthListener;

public class TransacoesByContaFragment extends Fragment{

	public static final String TAG = "[TransacaoesFragment]";

	private int idConta = -1;

	private DatabaseHandler db;
	private MoneyControlApp app;
	private LayoutInflater inflater;

	private List<Transacao> transacoes;

	private View myFragmentView;
	private ChangeMonthView monthView;
	private ListView listViewTransacoes;
	private TransacaoAdapter listAdapter;
	private ExpandableListView expandableListView;
	private ExpandableListAdapter expandableAdapter;
	private Conta conta;

	List<CategoriaTransacao> groupList = new LinkedList<CategoriaTransacao>();
	List<Transacao> childList;
	Map<CategoriaTransacao, List<Transacao>> categoriasCollection = new LinkedHashMap<CategoriaTransacao, List<Transacao>>();


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.inflater = inflater;

		myFragmentView = inflater.inflate(R.layout.fragment_transacaoes, null);
		loadComponentsFromXml();

		((NVFragmentActivity)getActivity()).getSupportActionBar().setTitle("Transações");

		app = (MoneyControlApp) getActivity().getApplication();
		db = app.getDatabase();
		idConta = getArguments().getInt("conta");
		conta = db.select(Conta.class, " WHERE id = "+idConta).get(0);

		String range = getArguments().getString("range");
		try {
			monthView.getDateRange().setTime(DateUtil.sqlDateFormat().parse(range));
			monthView.updateDateRange();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		configureComponents();

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
		update();
		((NVFragmentActivity)getActivity()).getSupportActionBar().setTitle(conta.getNome());
		updateIcon(conta);
	}

	@SuppressLint("NewApi")
	private void updateIcon(Conta c) {
		try {
			((NVFragmentActivity)getActivity()).getSupportActionBar().setIcon(AssetUtil.loadDrawableFromAsset(app, "icons/"+c.getIcon()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private boolean longClick = false;
	private void configureComponents() {


		monthView.setListener(new ChangeMonthListener() {
			@Override
			public void onMonthChange(Date time) {
				update();				
			}
		});

        transacoes = selectTransacoes();

        listAdapter = new TransacaoAdapter(transacoes, inflater, app);
		listViewTransacoes.setAdapter(listAdapter);
		listViewTransacoes.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				System.out.println("listViewTransacoes.setOnItemClickListener longClick: "+longClick);
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
			}


		});

		listViewTransacoes.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(android.widget.AdapterView<?> arg0, View arg1, final int position, long arg3) {
				longClick = true;
                final Transacao t = transacoes.get(position);
                final boolean parcelado = t.getId_Compra() > 0;
				MessageUtils.showMessageYesNo(getActivity(), app.getString(R.string.transacoes_message_title),
                        app.getString( parcelado ? R.string.fragment_transacoes_remove_parcelas_message : R.string.fragment_transacoes_remove_transacao_message), new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(parcelado){
							db.delete(t, "id_Compra = "+t.getId_Compra());
                            Compra compra = new Compra();
                            compra.setId(t.getId_Compra());
                            db.delete(compra);
						}else {
							db.delete(t);
						}
						update();
					}
				}, new DialogInterface.OnDismissListener() {					
					@Override
					public void onDismiss(DialogInterface dialog) {
						longClick = false;						
					}
				});
				return false;
			};
		});

		updateCollection();
		expandableAdapter = new ExpandableListAdapter(getActivity(), app, groupList, categoriasCollection);
		expandableAdapter.setConta(conta);
		expandableListView.setAdapter(expandableAdapter);

		expandableListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {

				Transacao t = (Transacao)expandableAdapter.getChild(groupPosition, childPosition);
				MessageUtils.showEditTransacao(getActivity(), t, inflater, db, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						update();
					}
				});
				return true;
			}
		});

	}

    private GregorianCalendar cartaoDateRange = null;
    private List<Transacao> selectTransacoes() {
        List<Transacao> trs = new ArrayList<Transacao>();
        if(conta.getId_TipoConta() == 4){
            updateCartaoRange();
            trs = db.select(Transacao.class, QuerysUtil.whereTransacaoFromContaWithDateInterval(idConta, cartaoDateRange.getTime()));
        }else{
            trs = db.select(Transacao.class,QuerysUtil.whereTransacaoFromContaWithDateInterval(idConta, monthView.getDateRange().getTime()));
        }
        return trs;
    }

    private void updateCartaoRange() {
        CartaoCredito cartao = app.getDatabase().select(CartaoCredito.class, " WHERE id_Conta = "+conta.getId()).get(0);
        cartaoDateRange = (GregorianCalendar)monthView.getDateRange().clone();
        if(cartao.getDia_fechamento() <= cartao.getDia_vencimento())
            cartaoDateRange.add(GregorianCalendar.MONTH, -2);
        else
            cartaoDateRange.add(GregorianCalendar.MONTH, -3);
        cartaoDateRange.set(GregorianCalendar.DAY_OF_MONTH, cartao.getDia_fechamento());
        cartaoDateRange.add(GregorianCalendar.MONTH, 1);
        cartaoDateRange.add(GregorianCalendar.DAY_OF_MONTH, 1);
    }

    private void loadComponentsFromXml() {
		monthView 	= (ChangeMonthView) myFragmentView.findViewById(R.id.transacoesFragmentChangeMonthView);
		listViewTransacoes = (ListView) myFragmentView.findViewById(R.id.transacoesActivityListViewTransacoes);
		expandableListView = (ExpandableListView) myFragmentView.findViewById(R.id.transacoesFragmentExpandableListViewTransacoes);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main_transacaoes_actions, menu);
		super.onCreateOptionsMenu(menu, inflater);
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

		case R.id.action_group:
			if(item.getTitle().equals(getActivity().getString(R.string.action_group))){
				item.setTitle(getActivity().getString(R.string.action_disgroup));
				listViewTransacoes.setVisibility(View.GONE);
				expandableListView.setVisibility(View.VISIBLE);
			}else{
				item.setTitle(getActivity().getString(R.string.action_group));
				listViewTransacoes.setVisibility(View.VISIBLE);
				expandableListView.setVisibility(View.GONE);
			}

			return true;

		case R.id.action_investiment:
			MessageUtils.showAddMovimentacaoAtivo(getActivity(), inflater, db, idConta, new OnClickListener() {
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
		transacoes.addAll(selectTransacoes());
        ;
        listAdapter.notifyDataSetChanged();

        updateCollection();
        expandableAdapter.notifyDataSetChanged();

		float debitoSum = 0;
		float creditoSum = 0;

        GregorianCalendar range = monthView.getDateRange();
        if(cartaoDateRange != null){
            updateCartaoRange();
            range = cartaoDateRange;
        }

		String debitos = db.runQuery(QuerysUtil.sumTransacoesDebitoFromContaWithDateInterval(idConta,range.getTime()));
		String creditos = db.runQuery(QuerysUtil.sumTransacoesCreditoFromContaWithDateInterval(idConta,range.getTime()));

		if(debitos != null && debitos.length() > 0)  debitoSum = Float.valueOf(debitos);
		if(creditos != null && creditos.length() > 0) creditoSum = Float.valueOf(creditos);

		String saldo = db.runQuery(QuerysUtil.computeSaldoFromContaBeforeDate(idConta,range.getTime()));
		float saldoAnterior = 0;
		if(saldo != null && saldo.length() > 0) saldoAnterior = Float.valueOf(saldo);

		((TextView)myFragmentView.findViewById(R.id.transacoesActivityTxtDebitosSum)).setText("R$ "+String.format("%.2f", debitoSum));
		((TextView)myFragmentView.findViewById(R.id.transacoesActivityTxtCreditosSum)).setText("R$ "+String.format("%.2f",creditoSum));
		((TextView)myFragmentView.findViewById(R.id.transacoesActivityTxtSaldoSum)).setText("R$ "+String.format("%.2f",(saldoAnterior+creditoSum-debitoSum)));
	}


	private void updateCollection() {
		groupList.clear();
		groupList.addAll(db.select(CategoriaTransacao.class));
		categoriasCollection.clear();

        GregorianCalendar range = monthView.getDateRange();
        if(cartaoDateRange != null){
            updateCartaoRange();
            range = cartaoDateRange;
        }

		List<CategoriaTransacao> emptys = new ArrayList<CategoriaTransacao>();

		for (CategoriaTransacao categoria : groupList) {
			childList = db.select(Transacao.class, QuerysUtil.whereTransacaoFromContaWithDateIntervalAndCategoria(idConta, categoria.getId(), range.getTime()));
			if(childList.isEmpty()){
				emptys.add(categoria);
			}else{
				categoriasCollection.put(categoria, childList);
			}
		}

		for (CategoriaTransacao categoriaTransacao : emptys) {
			groupList.remove(categoriaTransacao);
		}
	}

}
