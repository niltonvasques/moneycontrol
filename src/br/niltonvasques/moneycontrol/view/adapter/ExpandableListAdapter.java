package br.niltonvasques.moneycontrol.view.adapter;
 
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.R;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;
import br.niltonvasques.moneycontrol.database.bean.Transacao;
 
public class ExpandableListAdapter extends BaseExpandableListAdapter {
 
    private Activity context;
    private Map<CategoriaTransacao, List<Transacao>> categoriaTransacoes;
    private List<CategoriaTransacao> categorias;
    private MoneyControlApp app;
 
    public ExpandableListAdapter(Activity context, MoneyControlApp app, List<CategoriaTransacao> categorias,Map<CategoriaTransacao, List<Transacao>> categoriaTransacoes) {
        this.context = context;
        this.categoriaTransacoes = categoriaTransacoes;
        this.categorias = categorias;
        this.app = app;
    }
 
    public Object getChild(int groupPosition, int childPosition) {
        return categoriaTransacoes.get(categorias.get(groupPosition)).get(childPosition);
    }
 
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
     
     
    public View getChildView(final int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
    	LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                
        final Transacao tr = (Transacao) getChild(groupPosition, childPosition);
        
        return ViewFactory.createTransacaoItemView(tr, app, infalInflater);
    }
 
    public int getChildrenCount(int groupPosition) {
        return categoriaTransacoes.get(categorias.get(groupPosition)).size();
    }
 
    public Object getGroup(int groupPosition) {
        return categorias.get(groupPosition);
    }
 
    public int getGroupCount() {
        return categorias.size();
    }
 
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
    	CategoriaTransacao laptopName = (CategoriaTransacao) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.transacao_group_item,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.laptop);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(laptopName.getNome());
        return convertView;
    }
 
    public boolean hasStableIds() {
        return true;
    }
 
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}