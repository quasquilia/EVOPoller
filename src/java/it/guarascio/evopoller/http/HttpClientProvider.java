package it.guarascio.evopoller.http;

import org.apache.http.client.HttpClient;

public interface HttpClientProvider {

	HttpClient getWebClient();

	
}
