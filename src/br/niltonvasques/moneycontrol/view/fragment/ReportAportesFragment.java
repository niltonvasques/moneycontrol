package br.niltonvasques.moneycontrol.view.fragment;

import org.afree.data.category.DefaultCategoryDataset;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import br.niltonvasques.moneycontrol2.R;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.view.chart.BarChartView;
import br.niltonvasques.moneycontrol.view.custom.SquareLayout;

public class ReportAportesFragment extends Fragment{
	
	private static final String TAG = "[ReportAportesFragment]";
	
	private MoneyControlApp app;
	private DatabaseHandler db;
	
	
	private View myFragmentView;
    private BarChartView barChartView;

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		app = (MoneyControlApp) getActivity().getApplication();
		myFragmentView = inflater.inflate(R.layout.fragment_report_aportes, container, false);
		SquareLayout view = (SquareLayout)myFragmentView.findViewById(R.id.fragmentReportByCategoriaContent);
		
		barChartView = new BarChartView(getActivity(), createDataset(app));
		barChartView.setTitle(getResources().getString(R.string.report_aportes_title));
		barChartView.setCategoryAxis(getResources().getString(R.string.report_aportes_axis_category));
		barChartView.setValueAxis(getResources().getString(R.string.report_aportes_axis_value));
		barChartView.update();
		
		view.addView(barChartView);
		
		
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
    private static DefaultCategoryDataset createDataset(MoneyControlApp app) {
    	
    	// row keys...
        String series1 = "Aportes";

        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Cursor c2 = app.getDatabase().runQueryCursor(QuerysUtil.reportInvestimentsHistory());
        if (c2.moveToFirst()) {
        	do {
        		float valor = c2.getFloat(0);
        		String month = c2.getString(1);
        		dataset.addValue(valor, series1, month);
        	} while (c2.moveToNext());
        }
    	
    	
        return dataset;
    }
    
    @Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_add).setVisible(false);
		super.onPrepareOptionsMenu(menu);
	}

}
