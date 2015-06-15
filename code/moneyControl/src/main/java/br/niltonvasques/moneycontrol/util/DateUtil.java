package br.niltonvasques.moneycontrol.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static final SimpleDateFormat sqlDateFormat(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format;
	}

	public static final long diffDays(Date src, Date dst){
		// Get msec from each, and subtract.
		long diff = dst.getTime() - src.getTime();

		return (diff / (1000 * 60 * 60 * 24));
	}
}
