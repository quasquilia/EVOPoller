package it.guarascio.evopoller.publishers;

import it.guarascio.evopoller.EVOBean;
import it.guarascio.evopoller.http.HttpClientProvider;
import it.guarascio.evopoller.pvoutput.HttpSender;
import it.guarascio.evopoller.pvoutput.ResponseData;
import it.guarascio.evopoller.pvoutput.StatusCode;
import it.guarascio.evopoller.utils.Logger;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

class PVPublisher implements IEVOBeanPublisher {	
	private int requestsCount = 0;
	private DateFormat tfSimple = new SimpleDateFormat("HH:mm");
	private DateFormat dfSimple = new SimpleDateFormat("yyyyMMdd");
	private DecimalFormat df = new DecimalFormat( "#.0", DecimalFormatSymbols.getInstance(Locale.ENGLISH)); 
	private Properties p = new Properties();
	private final HttpClientProvider httpClientProvider;

	public static String MODE = "PV";
	
	private boolean dry = false;
	
	public PVPublisher(HttpClientProvider httpClientProvider, boolean dry) throws Exception {
		this.dry = dry;
		this.httpClientProvider = httpClientProvider;
	}
	
	public void processData(Date date, int power, double energy, double temp, String weather) throws Exception {				
		StringBuffer sb = new StringBuffer();
		
//		1	Output Date	Yes	20100830
		sb.append(dfSimple.format(date));
//		5	Peak Time	No	13:15
		sb.append(",").append(tfSimple.format(date));
//		2	Generated	Yes	15000
		sb.append(",").append((int)energy);
//		4	Peak Power	No	3000
		sb.append(",").append(power);

//		5	Weather conditions
		sb.append(",").append(java.net.URLEncoder.encode(weather, "ISO-8859-1"));
		
//		6	Temp min		
		sb.append(",").append("");
		
//		7	Temp max		
		String sTemperature = df.format(temp);
		sb.append(",").append(sTemperature);		
		send(sb.toString());
	}
	
	
	private void send(String s) throws Exception {
		
		Logger.info("Call n." + requestsCount);
		Logger.info(s.replace(';', '\n'));
		if (!dry) {
			Logger.info("effective call:");
			HttpSender sender = new HttpSender(httpClientProvider,
					"http://pvoutput.org/service/r2/addoutput.jsp", "155e88bb273495c0b00c0d59cb3fdc7c2a78e4e7", "20405");			
			ResponseData response = sender.sendbatch(s);
			if (response == null || !StatusCode.SUCCESS.equals(response.status)) {
				throw new RuntimeException("Error sending data to PVOutput. Error:" 
						+ (response == null ? "<null>" : response.status + " - " + response.message));
			}
		}
		requestsCount++;
		if (!dry) {
			Thread.sleep(1000);
		}
		
	}

	@Override
	public boolean publish(EVOBean bean, double initialEnergy) {
		try {
			double deltaEnergy = (bean.getEnergy() - initialEnergy)*1000;			
			processData(bean.getDate(), (int)bean.getPower(), deltaEnergy, bean.getTemperature(), bean.getWeather());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}

	/**
	 *  Fine
	 *	Partly Cloudy
	 *	Mostly Cloudy
	 *	Cloudy
	 *	Showers
	 *	Snow
	 *	Not Sure
	 * @param yahooWeatherCode
	 * @return
	 */
	
}
