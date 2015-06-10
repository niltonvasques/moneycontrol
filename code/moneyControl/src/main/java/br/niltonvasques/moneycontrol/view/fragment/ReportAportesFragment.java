package br.niltonvasques.moneycontrol.view.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
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
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView.ChangeMonthListener;
import br.niltonvasques.moneycontrolbeta.R;

public class ReportAportesFragment extends Fragment{
	
	private static final String TAG = "[ReportAportesFragment]";
	
	private MoneyControlApp app;
	
	private View myFragmentView;
    private ColumnChartView chart;
	private ChangeMonthView changeMonth;

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		app = (MoneyControlApp) getActivity().getApplication();
		myFragmentView = inflater.inflate(R.layout.fragment_report_aportes, container, false);
		
		changeMonth = (ChangeMonthView) myFragmentView.findViewById(R.id.fragmentReportAportsChangeMonthView);
		changeMonth.enableYearType();
		changeMonth.setListener(new ChangeMonthListener() {
			@Override
			public void onMonthChange(Date time) {
				chart.setColumnChartData(createDataset(app));
			}
		});
		
		chart = (ColumnChartView) myFragmentView.findViewById(R.id.fragmentReportAportsChart);
		chart.setColumnChartData(createDataset(app));
		
		return myFragmentView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected");
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
	        
	        String year = changeMonth.getDateRange().get(GregorianCalendar.YEAR)+"";

	        int i = 0;
	        
	        Cursor c2 = app.getDatabase().runQueryCursor(QuerysUtil.reportInvestimentsHistory(year));
	        if (c2.moveToFirst()) {
	        	do {
	        		values = new ArrayList<SubcolumnValue>();
	        		float valor = c2.getFloat(0);
//	        		String month = c2.getString(1);
	        		int month = Integer.parseInt(c2.getString(1));
	        		System.out.println(month+" - "+c2.getString(1));
//	        		dataset.addValue(valor, series1, month);
	        		SubcolumnValue sub = new SubcolumnValue(valor, ChartUtils.COLOR_BLUE);
	        		values.add(sub);   
	        		axisValues.add(new AxisValue(i++).setLabel(getResources().getStringArray(R.array.months)[month-1]));
	        		Column column = new Column(values);
	        		columns.add(column);
	        	} while (c2.moveToNext());
	        }
			c2.close();

	    	ColumnChartData data = new ColumnChartData(columns);
	    	Axis axisX = new Axis(axisValues).setHasLines(true);
	    	Axis axisY = new Axis().setHasLines(true);
	    	axisX.setName(getResources().getString(R.string.report_aportes_axis_category));
	    	axisY.setName(getResources().getString(R.string.report_aportes_axis_value));
	    	axisX.setTextColor(Color.DKGRAY);
	    	axisY.setTextColor(Color.DKGRAY);
	    	data.setAxisXBottom(axisX);
	    	data.setAxisYLeft(axisY);
	    	
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
