package br.niltonvasques.moneycontrol.view.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.afree.data.general.DefaultPieDataset;
import org.afree.data.general.PieDataset;

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
import br.niltonvasques.moneycontrolbeta.R;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;
import br.niltonvasques.moneycontrol.database.bean.Transacao;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.view.adapter.TransacaoAdapter;
import br.niltonvasques.moneycontrol.view.chart.PieChartView;
import br.niltonvasques.moneycontrol.view.chart.PieChartDemo01View;
import br.niltonvasques.moneycontrol.view.custom.SquareLayout;

public class ReportByCategoriasFragment extends Fragment{
	
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
    private PieChartView pieChartView;

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.inflater = inflater;
		app = (MoneyControlApp) getActivity().getApplication();
		myFragmentView = inflater.inflate(R.layout.fragment_report_bycategoria, container, false);
		SquareLayout view = (SquareLayout)myFragmentView.findViewById(R.id.fragmentReportByCategoriaContent);
		
		dateRange = new GregorianCalendar();
		dateRange.set(GregorianCalendar.DAY_OF_MONTH, 1);
		
		this.tipoTransacao = getArguments().getInt("TipoTransacao");
		String title = getActivity().getResources().getString( tipoTransacao == 1 ? R.string.report_by_categorias_receitas_chart_title : R.string.report_by_categorias_despesas_chart_title);
		pieChartView = new PieChartView(getActivity(), title, app, createDataset(app, tipoTransacao, dateRange.getTime()));
		view.addView(pieChartView);
		
		
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
		pieChartView.setPieDataset(createDataset(app, tipoTransacao, dateRange.getTime()));
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
				dataset.setValue(c.getString(0)+" R$ "+String.format("%.2f", valor)+" - "+percentual+"%", new Double(valor));
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
