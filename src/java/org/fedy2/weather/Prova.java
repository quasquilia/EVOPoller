package org.fedy2.weather;

import it.guarascio.evopoller.utils.Logger;

import org.fedy2.weather.data.Channel;
import org.fedy2.weather.data.Condition;
import org.fedy2.weather.data.unit.DegreeUnit;

public class Prova {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		YahooWeatherService service = new YahooWeatherService();
		Channel forecast = service.getForecast("712089", DegreeUnit.CELSIUS);
		Condition condition = forecast.item.getCondition();
		Logger.info("Current condition:" + condition.getText());
		Logger.info("Current temperature:" + condition.getTemp() + " °C");
		

	}

}
