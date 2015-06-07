package br.niltonvasques.moneycontrol.util;

import java.text.SimpleDateFormat;

public class DateUtil {

	public static final SimpleDateFormat sqlDateFormat(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format;
	}
}
