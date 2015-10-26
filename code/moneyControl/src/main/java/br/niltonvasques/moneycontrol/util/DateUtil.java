package br.niltonvasques.moneycontrol.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

    public static String formatCalendarToDate(GregorianCalendar c){
        return new StringBuilder()
                .append(c.get(Calendar.DAY_OF_MONTH)).append("-")
                .append(c.get(Calendar.MONTH) + 1).append("-")
                .append(c.get(Calendar.YEAR)).append(" ")
                .append(c.get(Calendar.HOUR_OF_DAY)).append(":")
                .append(c.get(Calendar.MINUTE))
                .toString();
    }
}
