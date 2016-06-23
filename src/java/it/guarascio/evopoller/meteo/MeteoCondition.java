package it.guarascio.evopoller.meteo;

import java.util.Date;

public abstract class MeteoCondition {	
	private final String nativeWeather;
	private final double temperature;
	private final Date date;
		
	public MeteoCondition(Date date, String nativeWeather, double temperature) {
		super();
		this.nativeWeather = nativeWeather;
		this.temperature = temperature;
		this.date = date;
	}

	public String getNativeWeather() {
		return nativeWeather;
	}

	public double getTemperature() {
		return temperature;
	}

	public Date getDate() {
		return date;
	}

	public abstract String getPVWeather();
	
}
