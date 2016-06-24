package it.guarascio.evopoller.statisticspublisher;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class Statistics {
	public double dailyEnergy;
	public Date workingTime;
	public double avgPower;
	public double peakPower;
	public Date peakHour;
	public final Map<String, Double> meteoStats = new LinkedHashMap<>();
}
