package it.guarascio.evopoller.sender;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import it.guarascio.evopoller.http.HttpClientProvider;
import it.guarascio.evopoller.utils.Logger;

public class IFTTMessageSenderFactory implements MessageSenderFactory {
	
	private final String key;
	private final HttpClientProvider httpClientProvider;

	public IFTTMessageSenderFactory(HttpClientProvider httpClientProvider, String key) {
		super();
		this.key = key;
		this.httpClientProvider = httpClientProvider;
	}

	@Override
	public MessageSender createSender() {
		return new MessageSender() {
			
			@Override
			public void send(String subject, String body, boolean html) {
				//curl -H "Content-Type: application/json" -X POST https://maker.ifttt.com/trigger/evo_started/with/key/c5UvPn8uTdnNaaRaoqYgI9 -d '{"value1":"Pippo"}'
				HttpClient httpClient = httpClientProvider.getWebClient();
			    try {
			        String url = "https://maker.ifttt.com/trigger/" + getMsgName(subject) + "/with/key/" + key;
					HttpPost request = new HttpPost(url);
					JsonObject json = new JsonObject();
					json.add("value1", body);
					
			        StringEntity params =new StringEntity(json.toString());
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
			
			private String getMsgName(String subject) {
				return "evo_message";
			}
		};
	}
	
	@Override
	public String toString() {
		return "IFTT Sender with key " + key;
	}

}
