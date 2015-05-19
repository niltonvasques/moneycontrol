package br.niltonvasques.moneycontrol.view.custom;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.niltonvasques.moneycontrolbeta.R;

public class LegendView extends LinearLayout{
	
    private Context context;
    
    private List<Legend> legends;
    
    public interface ChangeMonthListener{
    	public void onMonthChange(Date time);
    }

	public LegendView(Context context, AttributeSet attrs) {
		super(context, attrs);
		configure(context);
	}

	public LegendView(Context context) {
		super(context);
		configure(context);
	}

	private void configure(Context context) {
		this.context = context;
		setOrientation(LinearLayout.HORIZONTAL);
		setPadding(0, 5, 5, 0);
		setGravity(Gravity.CENTER);
//		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		mView = layoutInflater.inflate(R.layout.legend, null);
//		addView(mView);
		
	}

	public List<Legend> getLegends() {
		return legends;
	}

	public void setLegends(List<Legend> legends) {
		this.legends = legends;
		for(Legend legend: legends){
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View item = layoutInflater.inflate(R.layout.legend_item, null);
			
		    TextView txtViewLegend = (TextView) item.findViewById(R.id.legendItemTxtViewLegend);
		    ImageView imgViewColor = (ImageView) item.findViewById(R.id.legendItemImgViewColor);
		    txtViewLegend.setText(legend.getLegendName());
		    imgViewColor.setBackgroundColor(legend.getLegendColor());
		    
		    addView(item);
		}		
	}
	


}
