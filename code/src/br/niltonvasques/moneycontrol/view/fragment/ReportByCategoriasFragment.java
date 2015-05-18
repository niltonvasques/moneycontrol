package br.niltonvasques.moneycontrol.view.fragment;

import java.util.Date;

import org.afree.data.general.DefaultPieDataset;
import org.afree.data.general.PieDataset;

import android.database.Cursor;
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
import br.niltonvasques.moneycontrol.view.chart.PieChartView;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView.ChangeMonthListener;
import br.niltonvasques.moneycontrol.view.custom.SquareLayout;
import br.niltonvasques.moneycontrolbeta.R;

public class ReportByCategoriasFragment extends Fragment{
	
	private static final String TAG = "[CategoriasFragment]";
	
	private MoneyControlApp app;
	private LayoutInflater inflater;
	
	private int tipoTransacao;
	
	private View myFragmentView;
	private ChangeMonthView monthView;
    private PieChartView pieChartView;

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.inflater = inflater;
		app = (MoneyControlApp) getActivity().getApplication();
		myFragmentView = inflater.inflate(R.layout.fragment_report_bycategoria, container, false);
		this.tipoTransacao = getArguments().getInt("TipoTransacao");		
		
		
		return myFragmentView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		loadComponentsFromXml();
		configureComponents(inflater);
	}
	
	private void loadComponentsFromXml() {
		monthView 	= (ChangeMonthView) myFragmentView.findViewById(R.id.reportByCategoriaFragmentChangeMonthView);
	}
	
	private void configureComponents(LayoutInflater inflater) {
		
		monthView.setListener(new ChangeMonthListener() {
			@Override
			public void onMonthChange(Date time) {
				update();				
			}
		});
		
		SquareLayout view = (SquareLayout)myFragmentView.findViewById(R.id.fragmentReportByCategoriaContent);		
		String title = getActivity().getResources().getString( tipoTransacao == 1 ? R.string.report_by_categorias_receitas_chart_title : R.string.report_by_categorias_despesas_chart_title);
		pieChartView = new PieChartView(getActivity(), title, app, createDataset(app, tipoTransacao, monthView.getDateRange().getTime()));
		view.addView(pieChartView);
        
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
		pieChartView.setPieDataset(createDataset(app, tipoTransacao, monthView.getDateRange().getTime()));
	}
	
    /**
     * Creates a sample dataset.
     * @return a sample dataset.
     */
    private static PieDataset createDataset(MoneyControlApp app, int tipo, Date range) {
    	Cursor c = app.getDatabase().runQueryCursor(QuerysUtil.reportTransacaoByTipoByCategoriasWithDateInterval(tipo, range));
    	DefaultPieDataset dataset = new DefaultPieDataset();
    	if (c.moveToFirst()) {
			do {
				float valor = c.getFloat(1);
				float percentual = c.getFloat(2);
				percentual = Math.round(percentual*100)/100f;
				dataset.setValue(c.getString(0)+" R$ "+String.format("%.2f", valor)+" - "+percentual+"%", Double.valueOf(valor));
			} while (c.moveToNext());
		}
        return dataset;
    }
    
    @Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_add).setVisible(false);
		super.onPrepareOptionsMenu(menu);
	}

}
