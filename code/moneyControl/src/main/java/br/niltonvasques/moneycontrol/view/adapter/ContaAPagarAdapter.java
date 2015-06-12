package br.niltonvasques.moneycontrol.view.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.GregorianCalendar;
import java.util.List;

import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.bean.ContaAPagar;
import br.niltonvasques.moneycontrol.database.bean.Transacao;

public class ContaAPagarAdapter extends BaseAdapter{

	private MoneyControlApp app;
	private List<ContaAPagar> contasAPagar;
	private LayoutInflater inflater;
	private GregorianCalendar dateRange;
    private Context context;

	public ContaAPagarAdapter(Context context, List<ContaAPagar> contasAPagar, GregorianCalendar dateRange, LayoutInflater inflater, MoneyControlApp app) {
        this.context = context;
		this.contasAPagar = contasAPagar;
        this.dateRange = dateRange;
		this.inflater = inflater;
		this.app = app;
	}

    public void setDateRange(GregorianCalendar dateRange){
        this.dateRange = dateRange;
    }
	
	@Override
	public int getCount() {
		if(contasAPagar != null) return contasAPagar.size();
		return 0;		
	}

	@Override
	public Object getItem(int position) {
		return contasAPagar.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ContaAPagar tr = (ContaAPagar) getItem(position);
		
		return ViewFactory.createContaAPagarItemView(context, tr, dateRange, app, inflater, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                notifyDataSetChanged();
            }
        });
	}

}
