package br.niltonvasques.moneycontrol.view.fragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
import android.widget.Toast;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.view.chart.BarChartView;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView.ChangeMonthListener;
import br.niltonvasques.moneycontrol.view.custom.SquareLayout;
import br.niltonvasques.moneycontrolbeta.R;

public class ReportReceitasDespesasFragment extends Fragment{
	
	private static final String TAG = "[CategoriasFragment]";
	
	private MoneyControlApp app;
	private DatabaseHandler db;
	
	
	private View myFragmentView;
	private ChangeMonthView changeMonth;
    private BarChartView barChartView;

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		app = (MoneyControlApp) getActivity().getApplication();
		myFragmentView = inflater.inflate(R.layout.fragment_report_receitas_x_despesas, container, false);
		changeMonth = (ChangeMonthView) myFragmentView.findViewById(R.id.mes);
		changeMonth.enableYearType();
		changeMonth.setListener(new ChangeMonthListener() {
			@Override
			public void onMonthChange(Date time) {
				Toast.makeText(getActivity(), "Change Year", Toast.LENGTH_LONG).show();
				barChartView.setDataset(createDataset(app));
				barChartView.update();
			}
		});
		
		SquareLayout view = (SquareLayout)myFragmentView.findViewById(R.id.fragmentReportByCategoriaContent);
		
		barChartView = new BarChartView(getActivity(), createDataset(app));
		barChartView.setTitle(getResources().getString(R.string.report_receita_x_despesas_title));
		barChartView.setCategoryAxis(getResources().getString(R.string.report_receita_x_despesas_axis_category));
		barChartView.setValueAxis(getResources().getString(R.string.report_receita_x_despesas_axis_value));
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
    private DefaultCategoryDataset createDataset(MoneyControlApp app) {
    	
    	// row keys...
        String series1 = "Receitas";
        String series2 = "Despesas";

        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        String year = changeMonth.getDateRange().get(GregorianCalendar.YEAR)+"";

        Cursor c2 = app.getDatabase().runQueryCursor(QuerysUtil.reportHistoryDespesasByYear(year));
        if (c2.moveToFirst()) {
        	do {
        		float valor = c2.getFloat(0);
        		String month = c2.getString(1);
        		dataset.addValue(valor, series2, month);
        	} while (c2.moveToNext());
        }
        
    	Cursor c1 = app.getDatabase().runQueryCursor(QuerysUtil.reportHistoryReceitasByYear(year));
    	if (c1.moveToFirst()) {
			do {
				float valor = c1.getFloat(0);
				String month = c1.getString(1);
				dataset.addValue(valor, series1, month);
			} while (c1.moveToNext());
		}
    	c1.close();
    	
        return dataset;
    }
    
    @Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_add).setVisible(false);
		super.onPrepareOptionsMenu(menu);
	}

}
