package br.niltonvasques.moneycontrol.view;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.R;
import br.niltonvasques.moneycontrol.database.bean.Conta;

public class ContaAdapter extends BaseAdapter{
	
	private List<Conta> contas;
	private LayoutInflater inflater;

	public ContaAdapter(List<Conta> contas, LayoutInflater inflater) {
		this.contas = contas;
		this.inflater = inflater;
	}
	
	@Override
	public int getCount() {
		if(contas != null) return contas.size();
		return 0;		
	}

	@Override
	public Object getItem(int position) {
		return contas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Conta cc = (Conta) getItem(position);
		
		View view = inflater.inflate(R.layout.conta_list_item, null);
		
		ImageView imgIcon = (ImageView) view.findViewById(R.id.contaListItemImgIcon);
		TextView txtNome = (TextView) view.findViewById(R.id.contaListItemTxtNome);
		TextView txtSaldo = (TextView) view.findViewById(R.id.contaListItemTxtSaldo);
		
		txtNome.setText(cc.getNome());
		txtSaldo.setText(cc.getSaldo()+"");
		imgIcon.setBackgroundResource(cc.getIcon());
		
		return view;
	}

}
