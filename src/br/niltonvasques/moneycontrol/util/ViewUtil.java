package br.niltonvasques.moneycontrol.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class ViewUtil {

	public static <T> T configureSpinner(Context context,Spinner s, List<T> list, T selected){
		if(list.isEmpty()) return null;
		ArrayAdapter<T> adapter = new ArrayAdapter<T>(context, android.R.layout.simple_spinner_item, 
				list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
		
		if(selected == null){
			return adapter.getItem(0);
		}
		
		int pos = adapter.getPosition(selected);
		s.setSelection(pos, true);
		return selected;
	}
	
	public static <T> T configureSpinner(Context context,Spinner s, T[] list, T selected){
		ArrayAdapter<T> adapter = new ArrayAdapter<T>(context, android.R.layout.simple_spinner_item, 
				list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
		
		if(selected == null){
			return adapter.getItem(0);
		}
		
		int pos = adapter.getPosition(selected);
		s.setSelection(pos, true);
		return selected;
	}
	
	public static void adjustDateOnTextView(TextView view, GregorianCalendar date){
		view.setText(
				new StringBuilder()
				// Month is 0 based so add 1
				.append(date.get(Calendar.DAY_OF_MONTH)).append("/")
				.append(date.get(Calendar.MONTH) + 1).append("/")				
				.append(date.get(Calendar.YEAR)).append(" "));
	}
	
	public static void adjustTimeOnTextView(TextView view, GregorianCalendar date){
		view.setText(
				new StringBuilder()
				.append(date.get(Calendar.HOUR_OF_DAY)).append(":")
				.append(date.get(Calendar.MINUTE)));
	}
	
	public static String formatCalendarToDate(GregorianCalendar c){
		return new StringBuilder()
		.append(c.get(Calendar.DAY_OF_MONTH)).append("/")
		.append(c.get(Calendar.MONTH) + 1).append("/")				
		.append(c.get(Calendar.YEAR)).append(" ").toString();
	}
	
	public static String formatCalendarToTime(GregorianCalendar c){
		return new StringBuilder()
		.append(c.get(Calendar.HOUR_OF_DAY)).append(":")
		.append(c.get(Calendar.MINUTE)).toString();
	}
	
	
}
