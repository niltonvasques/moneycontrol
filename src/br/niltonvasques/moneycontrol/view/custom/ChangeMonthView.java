package br.niltonvasques.moneycontrol.view.custom;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.R;

public class ChangeMonthView extends RelativeLayout{
	
	private GregorianCalendar dateRange;
	
    private TextView txtViewDateRange;
    private Button	btnNextMonth;
    private Button btnPreviousMonth;
    private View mView;
    private ChangeMonthListener listener;
    
    public interface ChangeMonthListener{
    	public void onMonthChange(Date time);
    }

	public ChangeMonthView(Context context, AttributeSet attrs) {
		super(context, attrs);
		configure(context);
		loadComponentsFromXml();
	}

	public ChangeMonthView(Context context) {
		super(context);
		configure(context);
		loadComponentsFromXml();
	}

	private void configure(Context context) {
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = layoutInflater.inflate(R.layout.mudar_mes_template, null);
		addView(mView);
		
		dateRange = new GregorianCalendar();
		dateRange.set(GregorianCalendar.DAY_OF_MONTH, 1);
		updateDateRange();
        btnPreviousMonth.setOnClickListener(mOnClick);
        btnNextMonth.setOnClickListener(mOnClick);
	}
	
	private void loadComponentsFromXml() {
		txtViewDateRange 	= (TextView) mView.findViewById(R.id.principalFragmentTxtViewMonth);
		btnPreviousMonth 	= (Button) mView.findViewById(R.id.principalFragmentBtnPreviousMonth);
		btnNextMonth		= (Button) mView.findViewById(R.id.principalFragmentBtnNextMonth);		
	}
	
	private View.OnClickListener mOnClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.principalFragmentBtnPreviousMonth:
				dateRange.add(GregorianCalendar.MONTH, -1);
				updateDateRange();
				if(listener != null){
					listener.onMonthChange(dateRange.getTime());
				}
				break;
				
			case R.id.principalFragmentBtnNextMonth:
				dateRange.add(GregorianCalendar.MONTH, +1);
				updateDateRange();
				if(listener != null){
					listener.onMonthChange(dateRange.getTime());
				}
				break;

			default:
				break;
			}
			
		}
	};
	
	private void updateDateRange() {
		SimpleDateFormat format2 = new SimpleDateFormat("MMMMM - yyyy");
	    txtViewDateRange.setText(format2.format(dateRange.getTime()));
	}

	public void setListener(ChangeMonthListener listener) {
		this.listener = listener;
	}

}
