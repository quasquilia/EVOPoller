package it.guarascio.evopoller.evorequests;

import it.guarascio.evopoller.EVOBean;
import it.guarascio.evopoller.meteo.MeteoCondition;
import it.guarascio.evopoller.meteo.MeteoProvider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractHtmlPageRequest implements IEVORequest {
	protected String address;
	private static double lastTemperature = 0;
	private final MeteoProvider meteoProvider;
	
	
	public AbstractHtmlPageRequest(String address, MeteoProvider meteoProvider) {
		this.address = address;
		this.meteoProvider = meteoProvider;
	}

	protected EVOBean processPage(InputStream is) throws Exception {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	    String line = bufferedReader.readLine();
	    int i = 1;                        	
	    Pattern patternW = Pattern.compile("\"([^\"]*) W\"");
	    Pattern patternKWH = Pattern.compile("\"([^\"]*) KWh\"");
	    Double power = null;
	    Double energy = null;
	    
	    while(line != null){
	        //Logger.info("" + i + "\t" + line );        	
	              
	        if (i == 368) {
	        	//Logger.info(line);            	
	            Matcher matcher = patternW.matcher(line);
	            if (matcher.find()) {
	            	power = Double.parseDouble(matcher.group(1));	                		                   
	            }                
	        } else if (i == 209) {
	            Matcher matcher = patternKWH.matcher(line);
	            if (matcher.find()) {
	                energy = Double.parseDouble(matcher.group(1).trim());
	            }                
	        }
	        if (energy != null && power != null) {
	        	break;
	        }
	        line = bufferedReader.readLine();      
	        i++;
	    }
	    

	    MeteoCondition condition = null;
	    if (meteoProvider != null) {
		    try {
		    	condition = meteoProvider.getCondition();
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
	    }
	    
	    if (condition == null) {
	    	// Informazioni meteo non pervenute
	    	return new EVOBean(power, energy, lastTemperature, "Not sure");
	    }
	    
	    lastTemperature = condition.getTemperature();
	    return new EVOBean(power, energy, condition.getTemperature(), condition.getPVWeather());
	}

}