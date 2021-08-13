package br.niltonvasques.moneycontrol.view.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.niltonvasques.moneycontrol.util.NumberUtil;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView;
import br.niltonvasques.moneycontrol.view.custom.ChangeMonthView.ChangeMonthListener;
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
		
		TextView txt = (TextView) myFragmentView.findViewById(R.id.reportByCategoriaFragmentTxtViewTitle);
		txt.setText(tipoTransacao == 1 ? R.string.report_by_categorias_receitas_chart_title : R.string.report_by_categorias_despesas_chart_title);
		
		pieChartView = (PieChartView) myFragmentView.findViewById(R.id.fragmentReportByCategoriaChart);
		pieChartView.setPieChartData(createDataset(app, tipoTransacao, monthView.getDateRange().getTime()));
        
	}

	private void update(){
		pieChartView.setPieChartData(createDataset(app, tipoTransacao, monthView.getDateRange().getTime()));
	}
	
    /**
     * Creates a sample dataset.
     * @return a sample dataset.
     */
    private PieChartData createDataset(MoneyControlApp app, int tipo, Date range) {
    	Cursor c = app.getDatabase().runQueryCursor(QuerysUtil.reportTransacaoByTipoByCategoriasWithDateInterval(tipo, range));
    	List<SliceValue> values = new ArrayList<SliceValue>();
    	if (c.moveToFirst()) {
			do {
				float valor = c.getFloat(1);
				float percentual = c.getFloat(2);
				percentual = Math.round(percentual * 100) / 100f;
				String label = c.getString(0)+" R$ "+ NumberUtil.format(valor)+" - "+percentual+"%";
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
}
