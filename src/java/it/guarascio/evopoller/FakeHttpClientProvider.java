package it.guarascio.evopoller;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import it.guarascio.evopoller.http.HttpClientProvider;

public class FakeHttpClientProvider implements HttpClientProvider {

	@Override
	public HttpClient getWebClient() {
		return new HttpClient() {
			
			@Override
			public HttpParams getParams() {
				return null;
			}
			
			@Override
			public ClientConnectionManager getConnectionManager() {
				return null;
			}
			
			@Override
			public <T> T execute(HttpHost arg0, HttpRequest arg1, ResponseHandler<? extends T> arg2, HttpContext arg3)
					throws IOException, ClientProtocolException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T> T execute(HttpHost arg0, HttpRequest arg1, ResponseHandler<? extends T> arg2)
					throws IOException, ClientProtocolException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T> T execute(HttpUriRequest arg0, ResponseHandler<? extends T> arg1, HttpContext arg2)
					throws IOException, ClientProtocolException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public HttpResponse execute(HttpHost arg0, HttpRequest arg1, HttpContext arg2)
					throws IOException, ClientProtocolException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T> T execute(HttpUriRequest arg0, ResponseHandler<? extends T> arg1)
					throws IOException, ClientProtocolException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public HttpResponse execute(HttpHost arg0, HttpRequest arg1) throws IOException, ClientProtocolException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public HttpResponse execute(HttpUriRequest arg0, HttpContext arg1) throws IOException, ClientProtocolException {
				System.out.println("Sending " + arg0 + " with context " + arg1);
				return new BasicHttpResponse(new StatusLine() {
					
					@Override
					public int getStatusCode() {
						return 0;
					}
					
					@Override
					public String getReasonPhrase() {
						return null;
					}
					
					@Override
					public ProtocolVersion getProtocolVersion() {
						return null;
					}
				});
			}
			
			@Override
			public HttpResponse execute(HttpUriRequest arg0) throws IOException, ClientProtocolException {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

}
