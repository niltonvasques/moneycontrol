package br.niltonvasques.moneycontrol.util;

public class StringUtil {
	
	public static String capitalize(String line)
	{
	  return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}
}
