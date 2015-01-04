package br.niltonvasques.moneycontrol.view.adapter;

import java.io.IOException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import br.niltonvasques.moneycontrol.R;
import br.niltonvasques.moneycontrol.util.AssetUtil;

public class IconeAdapter extends BaseAdapter{
	private Context context;
	private String[] icones;
	private LayoutInflater inflater;

	public IconeAdapter(String[] icones, LayoutInflater context, Context app) {
		this.icones = icones;
		this.inflater = context;
		this.context = app;
	}
	
	@Override
	public int getCount() {
		if(icones != null) return icones.length;
		return 0;		
	}

	@Override
	public Object getItem(int position) {
		return icones[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = inflater.inflate(R.layout.icon_list_item, null);
		
		try {
			String icon = (String) getItem(position);
			// get input stream
			
			ImageView img = (ImageView) view.findViewById(R.id.iconListItemImgIcon);
			img.setImageDrawable(AssetUtil.loadDrawableFromAsset(context, "icons/"+icon));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return view;
	}
}
