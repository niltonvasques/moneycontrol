package br.niltonvasques.moneycontrol.view.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TreeSet;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
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
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView;
import br.niltonvasques.moneycontrol.view.custom.LegendView;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView.ChangeMonthListener;
import br.niltonvasques.moneycontrol.view.custom.Legend;
import br.niltonvasques.moneycontrolbeta.R;

public class ReportCategoriasFragment extends Fragment{
	
	private static final String TAG = "[ReportAportesFragment]";
	
	private MoneyControlApp app;
	
	private View myFragmentView;
	private ChangeMonthView changeMonth;
	private LegendView legendView;
	private LineChartView chart;
	private TreeSet<CategoriaTransacao> categorias;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		try{		
			app = (MoneyControlApp) getActivity().getApplication();
			myFragmentView = inflater.inflate(R.layout.fragment_report_categorias, container, false);
			
			categorias = (TreeSet<CategoriaTransacao>) app.getData();
			
			legendView = (LegendView) myFragmentView.findViewById(R.id.fragmentReportCategoriasLegendView);
			changeMonth = (ChangeMonthView) myFragmentView.findViewById(R.id.fragmentReportCategoriasChangeMonthView);
			changeMonth.enableYearType();
			changeMonth.setListener(new ChangeMonthListener() {
				@Override
				public void onMonthChange(Date time) {
					chart.setLineChartData(createDataset(app));
				}
			});
			
			chart = (LineChartView) myFragmentView.findViewById(R.id.fragmentReportCategoriasChart);
			chart.setLineChartData(createDataset(app));
		}catch(Exception e){
			e.printStackTrace();
			MessageUtils.showMessage(getActivity(), getString(R.string.dialog_error_message_title), getString(R.string.dialog_error_message_message));
		}
		
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
    private LineChartData createDataset(MoneyControlApp app) {
    	
		try{
			String yearstr = changeMonth.getDateRange().get(GregorianCalendar.YEAR)+"";			
			
			List<AxisValue> axisValues = new ArrayList<AxisValue>();
			List<Line> lines = new ArrayList<Line>();
			List<Legend> legends = new ArrayList<Legend>();
			
			for(int i = 0; i < 12; i++){
				axisValues.add(new AxisValue(i).setLabel(getResources().getStringArray(R.array.months)[i]));
			}
			
			for (CategoriaTransacao cat : categorias) {
				
				List<PointValue> values = new ArrayList<PointValue>();
				
				Cursor c = app.getDatabase().runQueryCursor(QuerysUtil.reportCategoriaByMonthWhereYear(cat.getId(), yearstr));				
				if (c.moveToFirst()) {
					do {
						float valor = c.getFloat(0);
						int month = Integer.valueOf(c.getString(1));
						int year = c.getInt(2);
						Log.d(TAG, "createDataset string: month: "+c.getString(1)+" year: "+c.getString(2));
						Log.d(TAG, "createDataset: month: "+month+" year: "+year);
						values.add(new PointValue(month-1, valor));
					} while (c.moveToNext());
				}
				c.close();
				Line line = new Line(values);
				line.setColor(ChartUtils.nextColor());
				lines.add(line);
				Legend legend = new Legend();
				legend.setLegendName(cat.getNome());
				legend.setLegendColor(line.getColor());
				legends.add(legend);
			}

			LineChartData lineData = new LineChartData(lines);
			
	    	Axis axisX = new Axis(axisValues).setHasLines(true);
	    	Axis axisY = new Axis().setHasLines(true);
	    	axisX.setName(getResources().getString(R.string.report_aportes_axis_category));
	    	axisY.setName(getResources().getString(R.string.report_aportes_axis_value));
	    	axisX.setTextColor(Color.DKGRAY);
	    	axisY.setTextColor(Color.DKGRAY);
	    	lineData.setAxisXBottom(axisX);
	    	lineData.setAxisYLeft(axisY);
	    	
	    	legendView.setLegends(legends);
	    	
	    	return lineData;
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
