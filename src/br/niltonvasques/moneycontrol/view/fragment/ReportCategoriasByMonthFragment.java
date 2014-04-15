package br.niltonvasques.moneycontrol.view.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.afree.data.general.DefaultPieDataset;
import org.afree.data.general.PieDataset;
import org.afree.data.time.Month;
import org.afree.data.time.TimeSeries;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import br.niltonvasques.moneycontrol.R;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;
import br.niltonvasques.moneycontrol.database.bean.Transacao;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.view.adapter.TransacaoAdapter;
import br.niltonvasques.moneycontrol.view.chart.PieChartView;
import br.niltonvasques.moneycontrol.view.chart.PieChartDemo01View;
import br.niltonvasques.moneycontrol.view.chart.TimeSeriesChartView;
import br.niltonvasques.moneycontrol.view.custom.SquareLayout;

public class ReportCategoriasByMonthFragment extends Fragment{
	
	private static final String TAG = "[CategoriasFragment]";
	
	private MoneyControlApp app;
	private DatabaseHandler db;
	private LayoutInflater inflater;
	
	private List<CategoriaTransacao> categorias;
	private GregorianCalendar dateRange;
	private int tipoTransacao;
	
	private View myFragmentView;
	private TextView txtViewDateRange;
    private Button	btnNextMonth;
    private Button btnPreviousMonth;
    private TimeSeriesChartView timeSeriesChart;

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.inflater = inflater;
		app = (MoneyControlApp) getActivity().getApplication();
		myFragmentView = inflater.inflate(R.layout.fragment_report_bycategoria, container, false);
		SquareLayout view = (SquareLayout)myFragmentView.findViewById(R.id.fragmentReportByCategoriaContent);
		
		dateRange = new GregorianCalendar();
		dateRange.set(GregorianCalendar.DAY_OF_MONTH, 1);
		
		List<CategoriaTransacao> categorias = app.getDatabase().select(CategoriaTransacao.class," WHERE id_TipoTransacao = 2 AND nome not like 'Transferência'");
		TimeSeries[] timeSeries = new TimeSeries[categorias.size()];
		int i = 0;
		for (CategoriaTransacao categoriaTransacao : categorias) {
			timeSeries[i++] = createDataset(app, categoriaTransacao.getId(), dateRange.getTime()); //Alimentação
		}
		
		timeSeriesChart = new TimeSeriesChartView(getActivity(), getActivity().getResources().getString(R.string.report_categorias_title), timeSeries);
		view.addView(timeSeriesChart);
		
		
		return myFragmentView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		loadComponentsFromXml();
		configureComponents(inflater);
	}
	
	private void loadComponentsFromXml() {
		txtViewDateRange 	= (TextView) myFragmentView.findViewById(R.id.reportByCategoriaFragmentTxtViewMonth);
		btnPreviousMonth 	= (Button) myFragmentView.findViewById(R.id.reportByCategoriaFragmentBtnPreviousMonth);
		btnNextMonth		= (Button) myFragmentView.findViewById(R.id.reportByCategoriaFragmentBtnNextMonth);
	}
	
	private void configureComponents(LayoutInflater inflater) {
		
		updateDateRange();
        
        btnPreviousMonth.setOnClickListener(mOnClick);
        btnNextMonth.setOnClickListener(mOnClick);
        
	}
	
	private View.OnClickListener mOnClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.reportByCategoriaFragmentBtnPreviousMonth:
				dateRange.add(GregorianCalendar.MONTH, -1);
				updateDateRange();
				update();
				break;
				
			case R.id.reportByCategoriaFragmentBtnNextMonth:
				dateRange.add(GregorianCalendar.MONTH, +1);
				updateDateRange();
				update();
				break;

			default:
				break;
			}
			
		}
	};
	
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
	
	private void updateDateRange() {
		SimpleDateFormat format2 = new SimpleDateFormat("MMMMM - yyyy");
	    txtViewDateRange.setText(format2.format(dateRange.getTime()));
	}
	
	private void update(){
//		pieChartView.setPieDataset(createDataset(app, tipoTransacao, dateRange.getTime()));
	}
	
    /**
     * Creates a sample dataset.
     * @return a sample dataset.
     */
    private static TimeSeries createDataset(MoneyControlApp app, int id_CategoriaTransacao, Date range) {
    	CategoriaTransacao cat = app.getDatabase().select(CategoriaTransacao.class, " WHERE id = "+id_CategoriaTransacao).get(0);
    	Cursor c = app.getDatabase().runQueryCursor(QuerysUtil.reportCategoriaByMonth(id_CategoriaTransacao));
    	TimeSeries s1 = new TimeSeries(cat.getNome());
    	if (c.moveToFirst()) {
			do {
				float valor = c.getFloat(0);
				int month = c.getInt(1);
				int year = c.getInt(2);
		        s1.add(new Month(month, year), valor);
			} while (c.moveToNext());
		}
        return s1;
    }
    
    @Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_add).setVisible(false);
		super.onPrepareOptionsMenu(menu);
	}

}
