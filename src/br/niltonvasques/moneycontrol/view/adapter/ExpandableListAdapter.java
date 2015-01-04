package br.niltonvasques.moneycontrol.view.adapter;
 
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;
import br.niltonvasques.moneycontrol.database.bean.Conta;
import br.niltonvasques.moneycontrol.database.bean.Transacao;
import br.niltonvasques.moneycontrol.util.DateUtil;
 
public class ExpandableListAdapter extends BaseExpandableListAdapter {
 
    private Activity context;
    private Map<CategoriaTransacao, List<Transacao>> categoriaTransacoes;
    private List<CategoriaTransacao> categorias;
    private MoneyControlApp app;
    private Conta conta;
 
    public ExpandableListAdapter(Activity context, MoneyControlApp app, List<CategoriaTransacao> categorias,Map<CategoriaTransacao, List<Transacao>> categoriaTransacoes) {
        this.context = context;
        this.categoriaTransacoes = categoriaTransacoes;
        this.categorias = categorias;
        this.app = app;
    }
    
    public void setConta(Conta c){
    	this.conta = c;
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
        final Transacao tr = (Transacao) getChild(groupPosition, 0);
        String total = "";
        if(tr != null){
        	try {
				Date d = DateUtil.sqlDateFormat().parse(tr.getData());
				GregorianCalendar range = new GregorianCalendar();
				range.setTime(d);
				if(conta != null){
					total = "R$ "+app.getDatabase().runQuery(QuerysUtil.reportCategoriaWithDateIntervalAndConta(laptopName.getId(), 
							conta.getId(),range.get(GregorianCalendar.MONTH)+1, range.get(GregorianCalendar.YEAR)));
				}else{
					total = "R$ "+app.getDatabase().runQuery(QuerysUtil.reportCategoriaWithDateInterval(laptopName.getId(), 
						range.get(GregorianCalendar.MONTH)+1, range.get(GregorianCalendar.YEAR)));
				}
				((TextView) convertView.findViewById(R.id.value)).setText(total);
			} catch (ParseException e) {
				e.printStackTrace();
			}
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