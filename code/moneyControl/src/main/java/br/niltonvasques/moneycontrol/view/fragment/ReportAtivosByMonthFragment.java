package br.niltonvasques.moneycontrol.view.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView.ChangeMonthListener;
import br.niltonvasques.moneycontrolbeta.R;

public class ReportAtivosByMonthFragment extends Fragment{
	
	private static final String TAG = "[CategoriasFragment]";
	
	private MoneyControlApp app;
	private LayoutInflater inflater;
	
	private View myFragmentView;
	private ChangeMonthView monthView;
    private PieChartView pieChartView;

    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		this.inflater = inflater;
		app = (MoneyControlApp) getActivity().getApplication();
		myFragmentView = inflater.inflate(R.layout.fragment_report_ativos_by_month, container, false);
		
		
		return myFragmentView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		loadComponentsFromXml();
		configureComponents(inflater);
	}
	
	private void loadComponentsFromXml() {
		monthView 	= (ChangeMonthView) myFragmentView.findViewById(R.id.reportAtivosByMonthFragmentChangeMonthView);
	}
	
	private void configureComponents(LayoutInflater inflater) {
		monthView.setListener(new ChangeMonthListener() {
			@Override
			public void onMonthChange(Date time) {
				update();				
			}
		});
        
		TextView txt = (TextView) myFragmentView.findViewById(R.id.reportAtivosByMonthFragmentTxtViewTitle);
		txt.setText(R.string.report_by_ativos_chart_title);
		
		pieChartView = (PieChartView) myFragmentView.findViewById(R.id.reportAtivosByMonthFragmentChart);
		pieChartView.setPieChartData(createDataset(app, monthView.getDateRange().getTime()));
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
	
	
	private void update(){
		pieChartView.setPieChartData(createDataset(app, monthView.getDateRange().getTime()));
	}
	
    /**
     * Creates a sample dataset.
     * @return a sample dataset.
     */
    private PieChartData createDataset(MoneyControlApp app, Date range) {
    	Cursor c = app.getDatabase().runQueryCursor(QuerysUtil.reportByAtivos(range));
    	List<SliceValue> values = new ArrayList<SliceValue>();
    	if (c.moveToFirst()) {
			do {
				float valor = c.getFloat(1);
				float percentual = c.getFloat(2);
				percentual = percentual*100;
				String label = c.getString(0)+" R$ "+String.format("%.2f", valor)+" - "+String.format("%.2f", percentual)+"%";
				SliceValue sliceValue = new SliceValue(valor, ChartUtils.nextColor());
				sliceValue.setLabel(label);
				values.add(sliceValue);
			} while (c.moveToNext());
		}
		c.close();
    	PieChartData data = new PieChartData(values);
    	data.setHasLabels(true);
    	return data;
    }
    
    @Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_add).setVisible(false);
		super.onPrepareOptionsMenu(menu);
	}

}
