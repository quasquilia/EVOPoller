package it.guarascio.evopoller.scheduler;

import it.guarascio.evopoller.utils.DateUtils;
import it.guarascio.evopoller.utils.Logger;

import java.util.Calendar;

public class Scheduler {
	private int startHour;
	private int startMinutes;
	private int minutesInterval;
	private final IScheduledTask task;
	
	public Scheduler(IScheduledTask task) {
		this.task = task;
	}

	public void setStartHour(int hour, int minutes) {
		startHour = hour;
		startMinutes = minutes;
	}

	public int getMinutesInterval() {
		return minutesInterval;
	}

	public void setMinutesInterval(int minutesInterval) {
		this.minutesInterval = minutesInterval;
	}

	
	public void start() {
		do {
			dailyCycle();
			
			// Calcolo il tempo di attesa fino a domattina e aspetto
			Calendar tomorrowMorningStartTime = getTodayStartTime();
			tomorrowMorningStartTime.add(Calendar.DAY_OF_YEAR, 1);
			long millis = tomorrowMorningStartTime.getTimeInMillis() - DateUtils.now().getTimeInMillis();
			Logger.info("See you tomorrow at " + tomorrowMorningStartTime + ".");
			sleep(millis);			
			
		} while (true);					
	}
	
	/**
	 * Ciclo giornaliero
	 */
	private void dailyCycle() {
		long millis = getTodayStartTime().getTimeInMillis() - DateUtils.now().getTimeInMillis();
		
		if (millis <=0) {
			// l'ora corrente supera l'ora in cui dovevo essere partito --> sono in ritardo: parto subito			
		} else {
			// Aspetto
			Logger.info("Waiting " + (millis/1000) + " sec... for start");
			sleep(millis);			
		}
		
		task.started();
		
		// prima esecuzione della mattina		
		long startTaskTime = System.currentTimeMillis();
		boolean result = task.doTask();
		// Ricalcolo tempo di attesa sulla base di quanto tempo ho perso nell'esecuzione
		long correction = System.currentTimeMillis() - startTaskTime;
		long intervalMillis = minutesInterval * 60 * 1000 - correction;
		if (intervalMillis <= 0) {
			// Richiesta durata troppo: e'inutile che riprovi subito
			intervalMillis = minutesInterval * 60 * 1000;
		}
		
		// Ulteriori esecuzioni
		while(result) {
			Logger.info("Waiting " + (intervalMillis / 1000) + " secs...");
			sleep(intervalMillis);		
			
			startTaskTime = System.currentTimeMillis();
			result = task.doTask();
			
			// Ricalcolo tempo di attesa sulla base di quanto tempo ho perso nell'esecuzione			
			correction = System.currentTimeMillis() - startTaskTime;			
			intervalMillis = minutesInterval * 60 * 1000 - correction;
			if (intervalMillis <= 0) {
				// Richiesta durata troppo: e'inutile che riprovi subito
				intervalMillis = minutesInterval * 60 * 1000;
			}
		}	
		Logger.info("Good night!");
		task.stopped();
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static int compare(int hour1, int minute1, int hour2, int minute2) {
		int i = hour1 - hour2;
		if (i == 0) {
			i = minute1 - minute2;
		}
		return i;
	}
		
	private Calendar getTodayStartTime() {
		Calendar c = DateUtils.now();
		c.set(Calendar.HOUR_OF_DAY, startHour);
		c.set(Calendar.MINUTE, startMinutes);
		c.set(Calendar.SECOND, 0);
		return c;
	}
	
	
	
}
