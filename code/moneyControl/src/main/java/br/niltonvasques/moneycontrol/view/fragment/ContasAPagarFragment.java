package br.niltonvasques.moneycontrol.view.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import br.niltonvasques.moneycontrol.MainActivity;
import br.niltonvasques.moneycontrol.activity.NVFragmentActivity;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.business.ContaAPagarBusiness;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.ContaAPagar;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.util.NumberUtil;
import br.niltonvasques.moneycontrol.view.adapter.ContaAPagarAdapter;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView;
import br.niltonvasques.moneycontrolbeta.R;

public class ContasAPagarFragment extends Fragment{


    private static final String TAG = "[AboutFragment]";

    private MoneyControlApp app;
    private DatabaseHandler db;
    private LayoutInflater inflater;

    private ListView listViewContas;
    private ChangeMonthView monthView;
    private View myFragmentView;
    private ContaAPagarAdapter listAdapter;

    private List<ContaAPagar> contas = new ArrayList<ContaAPagar>();

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
        ////Log.d(TAG, "onOptionsItemSelected");
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add:
                MessageUtils.showAddContaAPagar(getActivity(), inflater, db, new DialogInterface.OnClickListener() {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        this.inflater = inflater;

        app = (MoneyControlApp) getActivity().getApplication();
        db = app.getDatabase();

        myFragmentView = inflater.inflate(R.layout.fragment_contas_a_pagar, container, false);

        monthView = (ChangeMonthView) myFragmentView.findViewById(R.id.contasAPagarFragmentChangeMonthView);
        listViewContas = (ListView) myFragmentView.findViewById(R.id.contasAPagarFragmentListViewContas);

        configureComponents();

        return myFragmentView;
    }

    private void configureComponents() {

        monthView.setListener(new ChangeMonthView.ChangeMonthListener() {
            @Override
            public void onMonthChange(Date time) {
                update();
            }
        });
        contas = new ArrayList<ContaAPagar>();
        try {
            contas.addAll(ContaAPagarBusiness.getContaAPagarsOnMonth(db, monthView.getDateRange(), getCalendarLastDay()));
        }catch (Exception e){
            e.printStackTrace();
            MessageUtils.showDefaultErrorMessage(getActivity());
        }


        listAdapter = new ContaAPagarAdapter(getActivity(), contas, monthView.getDateRange(), inflater, app);
        listViewContas.setAdapter(listAdapter);

        listViewContas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
                MessageUtils.showMessageYesNo(getActivity(), app.getString(R.string.contas_fragment_message_dialog_atention_title), app.getString(R.string.contas_fragment_remove_account_msg), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContaAPagar c = contas.get(position);
                        c.setStatus(false);
                        db.update(c);
                        update();
                    }
                });
                return false;
            }
        });

        listViewContas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                //                Fragment fragment = new TransacoesByContaFragment();
                //                Bundle args = new Bundle();
                //                args.putInt("conta", contas.get(position).getId());
                //                args.putString("range", DateUtil.sqlDateFormat().format(monthView.getDateRange().getTime()));
                //                fragment.setArguments(args);
                //                ((NVFragmentActivity)getActivity()).changeFragment(fragment);
            }
        });
        update();
    }

    private void update(){
        contas.clear();
        try {
            contas.addAll(ContaAPagarBusiness.getContaAPagarsOnMonth(db, monthView.getDateRange(), getCalendarLastDay()));
        }catch (Exception e){
            e.printStackTrace();
            MessageUtils.showDefaultErrorMessage(getActivity());
        }
        listAdapter.notifyDataSetChanged();

        float credito = 0;
        for(ContaAPagar c : contas){
            credito += c.getValor();
        }

        float debito = 0;
        String contasPagasQuery = db.runQuery(QuerysUtil.sumContasPagaOnMonth(monthView.getDateRange().getTime()));
        String faturasPagasQuery = db.runQuery(QuerysUtil.sumFaturasPagas(monthView.getDateRange().getTime()));
        if(contasPagasQuery != null && contasPagasQuery.length() > 0){
            debito = Float.valueOf(contasPagasQuery);
        }
        if(faturasPagasQuery != null && faturasPagasQuery.length() > 0){
            debito += Float.valueOf(faturasPagasQuery);
        }

        float restante = credito - debito;

        ((TextView)myFragmentView.findViewById(R.id.contasAPagarFragmentTxtSaldoSum)).setText("R$ "+ NumberUtil.format(restante));
        ((TextView)myFragmentView.findViewById(R.id.contasAPagarFragmentTxtCreditosSum)).setText("R$ " + NumberUtil.format(credito));
        ((TextView)myFragmentView.findViewById(R.id.contasAPagarFragmentTxtDebitosSum)).setText("R$ " + NumberUtil.format(debito));
    }

    private GregorianCalendar getCalendarLastDay() {
        GregorianCalendar tmp = (GregorianCalendar) monthView.getDateRange().clone();
        tmp.add(Calendar.MONTH, 1);
        tmp.add(Calendar.DAY_OF_MONTH, -1);
        return tmp;
    }

}
