package it.guarascio.evopoller;

import it.guarascio.evopoller.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

public class EVOBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2765296239590885667L;
	private double power;
	private double energy;
	private double temperature;
	private String weather;	
	
	private Date date = DateUtils.now().getTime();
	public double getPower() {
		return power;
	}
	public void setPower(double power) {
		this.power = power;
	}
	public double getEnergy() {
		return energy;
	}
	public void setEnergy(double energy) {
		this.energy = energy;
	}
	public EVOBean(double power, double energy, double temperature, String weather) {
		super();
		this.power = power;
		this.energy = energy;
		this.temperature = temperature;
		this.weather = weather;				
	}
	
	public EVOBean(EVOBean other) {
		this(other.getPower(), other.getEnergy(), other.getTemperature(), other.getWeather());
		this.date = other.getDate();
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return date +  "\tEnergy= " + energy + " KWh,\tPower= " + power + " W,\tTemp= " + temperature + "°C,\t Weather= " + weather;  
	}
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	public double getTemperature() {
		return temperature;
	}
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	public String getWeather() {
		return weather;
	}
	
	public void setWeather(String weather) {
		this.weather = weather;
	}	
}
