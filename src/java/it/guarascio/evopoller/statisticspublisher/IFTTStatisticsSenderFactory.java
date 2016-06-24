package it.guarascio.evopoller.statisticspublisher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.eclipsesource.json.JsonObject;

import it.guarascio.evopoller.http.HttpClientProvider;
import it.guarascio.evopoller.utils.Logger;

public class IFTTStatisticsSenderFactory implements StatisticsSenderFactory {
	
	private final String key;
	private final HttpClientProvider httpClientProvider;

	public IFTTStatisticsSenderFactory(HttpClientProvider httpClientProvider, String key) {
		super();
		this.key = key;
		this.httpClientProvider = httpClientProvider;
	}

	@Override
	public StatisticsSender createSender() {
		return new  StatisticsSender() {
			@Override
			public void send(Statistics statistics) {
				HttpClient httpClient = httpClientProvider.getWebClient();
			    try {
			        String url = "https://maker.ifttt.com/trigger/evo_statistics/with/key/" + key;
					HttpPost request = new HttpPost(url);
					
			        StringEntity params =new StringEntity(getJson(statistics));
			        request.addHeader("content-type", "application/json");
			        request.setEntity(params);
			        Logger.info(url);
			        HttpResponse response = httpClient.execute(request);
			        Logger.info(response.toString());
			    }catch (Exception ex) {
			    } finally {
			        httpClient.getConnectionManager().shutdown(); //Deprecated
			    }
			}
			
			private String getJson(Statistics statistics) {
				JsonObject json = new JsonObject();
				SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
				
				json.add("value1", dateFormat.format(new Date()));
				json.add("value2", statistics.avgPower);
				json.add("value3", statistics.dailyEnergy);
				json.add("value4", statistics.peakPower);
				json.add("value5", timeFormat.format(statistics.peakHour));
				json.add("value6", timeFormat.format(statistics.workingTime));
				
				for(Map.Entry<String, Double> e : statistics.meteoStats.entrySet()) {
					json.add(e.getKey(), e.getValue());
				}
				
				return json.toString();
			}
		};
	}
	
	@Override
	public String toString() {
		return "IFTT Sender with key " + key;
	}

}
