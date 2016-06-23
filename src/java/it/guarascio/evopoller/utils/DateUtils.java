package it.guarascio.evopoller.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
	public static Calendar forcedCalendar = null;
	public static Calendar now() {
		if (forcedCalendar == null) {
			Calendar c = new GregorianCalendar();
			c.setTime(new Date());
			//c.add(Calendar.DATE, 4);			
			return c;
		}
		return forcedCalendar;
	}
	
	public static boolean sameDay(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
		                  cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
	}
	
	public static int daysBetween(Date d1, Date d2){
		return (int)( (removeTime(d2).getTime() - removeTime(d1).getTime()) / (1000 * 60 * 60 * 24));
	}
	
	public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
	
//	public static void main(String[] args) throws Exception {
//		Date d1 = new Date();		
//		
//		Date d2 = new Date(2013 - 1900, 8, 7, 6, 0, 0);
//		System.out.println(percentageOfDay(d2));
//	}
	
	public static Date addMinutes(Date date, int minutes) {
		return new Date(date.getTime()+ minutes *60000);
	}
	
	public static Date addDays(Date date, int days) {
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		c.add(Calendar.DATE, days);		
		return c.getTime();
	}
	
	
	public static double percentageOfDay(Date date) {
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		double hour = now.get(Calendar.HOUR_OF_DAY); 
		double minutes =now.get(Calendar.MINUTE);
		
		return (hour / 24 + minutes / (60 * 24));
	}
}
