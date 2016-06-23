package it.guarascio.evopoller.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {
	public static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public static void info(String text) {
		System.out.println(dateFormat.format(DateUtils.now().getTime()) + " - " + text);
	}
	
	public static void error(String text) {
		System.out.println(dateFormat.format(DateUtils.now().getTime()) + " - " + text);
	}
}
