package br.niltonvasques.moneycontrol.view.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import android.R.color;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView;
import br.niltonvasques.moneycontrol.view.custom.Legend;
import br.niltonvasques.moneycontrol.view.custom.LegendView;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView.ChangeMonthListener;
import br.niltonvasques.moneycontrolbeta.R;

public class ReportReceitasDespesasFragment extends Fragment{
	
	private static final String TAG = "[CategoriasFragment]";
	
	private MoneyControlApp app;
	
	private View myFragmentView;
	private ChangeMonthView changeMonth;
	private ColumnChartView chart;
	private LegendView legendView;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		app = (MoneyControlApp) getActivity().getApplication();
		myFragmentView = inflater.inflate(R.layout.fragment_report_receitas_x_despesas, container, false);
		
		legendView = (LegendView) myFragmentView.findViewById(R.id.fragmentReportReceitasDespesasLegendView);
		changeMonth = (ChangeMonthView) myFragmentView.findViewById(R.id.mes);
		changeMonth.enableYearType();
		changeMonth.setListener(new ChangeMonthListener() {
			@Override
			public void onMonthChange(Date time) {
				chart.setColumnChartData(createDataset(app));
			}
		});
		
		chart = (ColumnChartView) myFragmentView.findViewById(R.id.chart);
		chart.setColumnChartData(createDataset(app));
		
		return myFragmentView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		////Log.d(TAG, "onOptionsItemSelected");
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_add:
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
    /**
     * Creates a sample dataset.
     * @return a sample dataset.
     */
    private ColumnChartData createDataset(MoneyControlApp app) {
    	
		try{
	        
	        LinkedHashMap<Integer, List<SubcolumnValue>> columnMap = new LinkedHashMap<Integer, List<SubcolumnValue>>();
	        
	        List<Column> columns = new ArrayList<Column>();
	        List<SubcolumnValue> values;
	        List<AxisValue> axisValues = new ArrayList<AxisValue>();
	        List<Legend> legends = new ArrayList<Legend>();
	        
	        Legend legend = new Legend();
			legend.setLegendName(getString(R.string.report_receita_x_despesas_legend_receita));
			legend.setLegendColor(ChartUtils.COLOR_GREEN);
			legends.add(legend);
			
			legend = new Legend();
			legend.setLegendName(getString(R.string.report_receita_x_despesas_legend_despesa));
			legend.setLegendColor(ChartUtils.COLOR_RED);
			legends.add(legend);
	        
	        String year = changeMonth.getDateRange().get(GregorianCalendar.YEAR)+"";

	        int i = 0;
	        Cursor c2 = app.getDatabase().runQueryCursor(QuerysUtil.reportHistoryDespesasByYear(year));
	        if (c2.moveToFirst()) {
	        	do {
	        		values = new ArrayList<SubcolumnValue>();
	        		float valor = c2.getFloat(0);
	        		int month = Integer.parseInt(c2.getString(1));
//	        		dataset.addValue(valor, series2, month);
	        		SubcolumnValue sub = new SubcolumnValue(valor, ChartUtils.COLOR_RED);
//	        		sub.setLabel(month);
	        		values.add(sub);   
	        		columnMap.put(month, values);
	        		axisValues.add(new AxisValue(i++).setLabel(getResources().getStringArray(R.array.months)[month-1]));
	        	} while (c2.moveToNext());
	        }
			c2.close();
	        
	    	Cursor c1 = app.getDatabase().runQueryCursor(QuerysUtil.reportHistoryReceitasByYear(year));
	    	if (c1.moveToFirst()) {
				do {
					float valor = c1.getFloat(0);
					int month = Integer.parseInt(c1.getString(1));
					if(columnMap.containsKey(month))
						values = columnMap.get(month);
					else
						values = new ArrayList<SubcolumnValue>();
	        		SubcolumnValue sub = new SubcolumnValue(valor, ChartUtils.COLOR_GREEN);
//	        		sub.setLabel(month);
	        		values.add(sub); 
	        		columnMap.put(month, values);
	        		Column column = new Column(values);
//	        		column.setHasLabels(true);
	        		columns.add(column);
				} while (c1.moveToNext());
			}
	    	c1.close();
	    	ColumnChartData data = new ColumnChartData(columns);
	    	Axis axisX = new Axis(axisValues).setHasLines(true);
	    	Axis axisY = new Axis().setHasLines(true);
	    	axisX.setName(getResources().getString(R.string.report_receita_x_despesas_axis_category));
	    	axisY.setName(getResources().getString(R.string.report_receita_x_despesas_axis_value));
	    	axisX.setTextColor(Color.DKGRAY);
	    	axisY.setTextColor(Color.DKGRAY);
	    	data.setAxisXBottom(axisX);
	    	data.setAxisYLeft(axisY);
	    	
	    	legendView.setLegends(legends);
	    	
	    	return data;
		}catch(Exception e){
			e.printStackTrace();
			MessageUtils.showMessage(getActivity(), getString(R.string.dialog_error_message_title), getString(R.string.dialog_error_message_message));
		}
    	
    	return null;
    }
    
    @Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_add).setVisible(false);
		super.onPrepareOptionsMenu(menu);
	}

}
