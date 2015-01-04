package br.niltonvasques.moneycontrol.view.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import br.niltonvasques.moneycontrol2.R;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;

public class CategoriaChooseAdapter extends BaseAdapter{
	
	private List<CategoriaTransacao> categorias;
	private LayoutInflater inflater;
	private TreeSet<CategoriaTransacao> categoriasSelected = new TreeSet<CategoriaTransacao>();

	public CategoriaChooseAdapter(List<CategoriaTransacao> transacoes, LayoutInflater inflater) {
		this.categorias = transacoes;
		this.inflater = inflater;
	}
	
	@Override
	public int getCount() {
		if(categorias != null) return categorias.size();
		return 0;		
	}

	@Override
	public Object getItem(int position) {
		return categorias.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final CategoriaTransacao tr = (CategoriaTransacao) getItem(position);
		
		View view = inflater.inflate(R.layout.categoria_choose_list_item, null);
		
		TextView txtNome = (TextView) view.findViewById(R.id.categoriaChooseListItemTxtNome);
		txtNome.setText(tr.getNome());
		
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.categoriaChooseListItemCheckBox);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) categoriasSelected.add(tr);
				else categoriasSelected.remove(tr);
			}
		});
		
		return view;
	}

	public TreeSet<CategoriaTransacao> getCategoriasSelected() {
		return categoriasSelected;
	}

}
