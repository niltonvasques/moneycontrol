package br.niltonvasques.moneycontrol.view.adapter;

import java.util.List;

import br.niltonvasques.moneycontrol.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class IconeAdapter extends BaseAdapter{
	private List<Integer> icones;
	private LayoutInflater context;

	public IconeAdapter(List<Integer> icones, LayoutInflater context) {
		this.icones = icones;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		if(icones != null) return icones.size();
		return 0;		
	}

	@Override
	public Object getItem(int position) {
		return icones.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Integer icon = (Integer) getItem(position);
		View view = context.inflate(R.layout.icon_list_item, null);
		ImageView img = (ImageView) view.findViewById(R.id.iconListItemImgIcon);
		img.setBackgroundResource(icon);				
		
		return view;
	}
}
