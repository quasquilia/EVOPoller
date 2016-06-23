package it.guarascio.evopoller.meteo;


import it.guarascio.evopoller.utils.DateUtils;
import it.guarascio.evopoller.utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import com.eclipsesource.json.JsonObject;

class WUndergroundMeteoProvider implements MeteoProvider {

	public static String CODE = "WUNDERGROUND";
	
	public static void main(String[] args) throws Exception {
		String res = new WUndergroundMeteoProvider().getJSON("http://api.wunderground.com/api/d645214de6bc3045/conditions/q/IT/Capannori.json", 30000);		
		//System.out.println(res);
		JsonObject  jsonObj = JsonObject.readFrom( res );
		JsonObject obs = (JsonObject)jsonObj.get("current_observation");
		System.out.println(obs.get("temp_c"));
		System.out.println(obs.get("weather"));
		
	}
	
	private String getJSON(String url, int timeout) throws IOException {	    
        URL u = new URL(url);
        HttpURLConnection c = (HttpURLConnection) u.openConnection();
        c.setRequestMethod("GET");
        c.setRequestProperty("Content-length", "0");
        c.setUseCaches(false);
        c.setAllowUserInteraction(false);
        c.setConnectTimeout(timeout);
        c.setReadTimeout(timeout);
        c.connect();
        int status = c.getResponseCode();

        switch (status) {
            case 200:
            case 201:
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                return sb.toString();
        }

	    return null;
	}
	
	@Override
	public MeteoCondition getCondition() {
		MeteoCondition condition = null;
	    try {
	    	String res = new WUndergroundMeteoProvider().getJSON("http://api.wunderground.com/api/d645214de6bc3045/conditions/q/IT/Capannori.json", 30000);		
			JsonObject  jsonObj = JsonObject.readFrom( res );
			JsonObject obs = (JsonObject)jsonObj.get("current_observation");
			double temp = obs.get("temp_c").asDouble();
			String weather = obs.get("weather").asString();
			condition = new WUMeteoCondition(DateUtils.now().getTime(),weather, temp);	    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return condition;
    }
	
	private static class WUMeteoCondition extends MeteoCondition {

		public WUMeteoCondition(Date date, String nativeWeather,
				double temperature) {
			super(date, nativeWeather, temperature);
		}

		@Override
		public String getPVWeather() {
			String weather = getNativeWeather();
			if (weather == null) {
				return "Not Sure";
			}
			weather = weather.toUpperCase();
			
			if (weather.contains("RAIN")  || weather.contains("THUNDERSTORM") || weather.contains("DRIZZLE")) {
				return "Showers";
			}
			
			if (weather.contains("SNOW")) {
				return "Snow";
			}
						
			if (weather.equals("MOSTLY CLOUDY") || weather.contains("FOG")  || weather.contains("OVERCAST")) {
				return "Mostly Cloudy";
			}
			
			if (weather.contains("CLOUD")) {
				return "Partly Cloudy";
			}
			
			if (weather.equals("CLEAR")) {
				return "Fine";
			}
			
			Logger.error("!!! UNRECOGNIZED WUNDERGROUND METEO CONDITION '" + weather +"' !!!");
			return "Not Sure";
		}
		
		
		
	}

}
