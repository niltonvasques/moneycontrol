/* ===========================================================
 * AFreeChart : a free chart library for Android(tm) platform.
 *              (based on JFreeChart and JCommon)
 * ===========================================================
 *
 * (C) Copyright 2010, by ICOMSYSTECH Co.,Ltd.
 * (C) Copyright 2000-2008, by Object Refinery Limited and Contributors.
 *
 * Project Info:
 *    AFreeChart: http://code.google.com/p/afreechart/
 *    JFreeChart: http://www.jfree.org/jfreechart/index.html
 *    JCommon   : http://www.jfree.org/jcommon/index.html
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * [Android is a trademark of Google Inc.]
 *
 * -----------------
 * TimeSeriesChartDemo01Activity.java
 * -----------------
 * (C) Copyright 2010, 2011, by ICOMSYSTECH Co.,Ltd.
 *
 * Original Author:  Niwano Masayoshi (for ICOMSYSTECH Co.,Ltd);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 19-Nov-2010 : Version 0.0.1 (NM);
 */

package br.niltonvasques.moneycontrol.activity;


import java.util.TreeSet;

import org.afree.data.time.Month;
import org.afree.data.time.TimeSeries;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;
import br.niltonvasques.moneycontrol.view.chart.TimeSeriesChartView;
import br.niltonvasques.moneycontrolbeta.R;

/**
 * TimeSeriesChartDemo01Activity
 */
public class TimeSeriesActivity extends Activity {
	
	private static final String TAG = "[TimeSeriesActivity]";

	private MoneyControlApp app;
    /**
     * Called when the activity is starting.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        app = (MoneyControlApp) getApplication();

        TreeSet<CategoriaTransacao> categorias = (TreeSet<CategoriaTransacao>) app.getData();
		TimeSeries[] timeSeries = new TimeSeries[categorias.size()];
		int i = 0;
		for (CategoriaTransacao categoriaTransacao : categorias) {
			timeSeries[i++] = createDataset(app, categoriaTransacao.getId()); //Alimentação
		}
		
		TimeSeriesChartView mView = new TimeSeriesChartView(this, getResources().getString(R.string.report_categorias_title), timeSeries);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(mView);
    }
    
    private static TimeSeries createDataset(MoneyControlApp app, int id_CategoriaTransacao) {
    	CategoriaTransacao cat = app.getDatabase().select(CategoriaTransacao.class, " WHERE id = "+id_CategoriaTransacao).get(0);
    	Cursor c = app.getDatabase().runQueryCursor(QuerysUtil.reportCategoriaByMonth(id_CategoriaTransacao));
    	TimeSeries s1 = new TimeSeries(cat.getNome());
    	if (c.moveToFirst()) {
			do {
				float valor = c.getFloat(0);
				int month = Integer.valueOf(c.getString(1));
				int year = c.getInt(2);
				Log.d(TAG, "createDataset string: month: "+c.getString(1)+" year: "+c.getString(2));
				Log.d(TAG, "createDataset: month: "+month+" year: "+year);
		        s1.add(new Month(month, year), valor);
			} while (c.moveToNext());
		}
        return s1;
    }
}
