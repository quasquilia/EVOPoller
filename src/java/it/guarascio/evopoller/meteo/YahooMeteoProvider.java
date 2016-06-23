package it.guarascio.evopoller.meteo;


import org.fedy2.weather.YahooWeatherService;
import org.fedy2.weather.data.Channel;
import org.fedy2.weather.data.Condition;
import org.fedy2.weather.data.unit.DegreeUnit;

class YahooMeteoProvider implements MeteoProvider {

	public static String CODE = "YAHOO";
	
	@Override
	public MeteoCondition getCondition() {
		Condition condition = null;
	    try {
			YahooWeatherService service = new YahooWeatherService();
			Channel forecast = service.getForecast("712089", DegreeUnit.CELSIUS);
			condition = forecast.item.getCondition();
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return new YahooMeteoCondition(condition);
	}
	
	private static class YahooMeteoCondition extends MeteoCondition {
		private final int yahooWeatherCode;
		YahooMeteoCondition(Condition cond) {
			super(cond.getDate(), cond.getText(), cond.getTemp());
			this.yahooWeatherCode = cond.getCode();
		}
		
		@Override
		public String getPVWeather() {
			switch(yahooWeatherCode) {
				case 1: case 2: case 3: case 4: case 8: case 9: case 10: case 11: case 17: case 23: case 35: case 37: case 38: case 39: case  40: case 45: case 47:
					return "Showers";
				case 5: case 6: case 7: case 13: case 14: case 15: case 16: case 18: case 41: case 42: case 43: case 46:
					return "Snow";
				case 44:  case 29: case 30:
					return "Partly Cloudy";
				case 26: case 27: case 28:
					return "Mostly Cloudy";
				case 31: case 32: case 33: case 34: case 36:
					return "Fine";
			}
			return "Not Sure";
		}
	}
	

}
