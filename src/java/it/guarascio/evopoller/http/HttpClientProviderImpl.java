package it.guarascio.evopoller.http;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import it.guarascio.evopoller.pvoutput.Constant;
import it.guarascio.evopoller.utils.Logger;

public class HttpClientProviderImpl implements HttpClientProvider {
	@Override
	public HttpClient getWebClient() {	
		
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager();
		cm.setMaxTotal(100);
		
		HttpClient httpclient = new DefaultHttpClient(cm);
		HttpParams params = httpclient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, Constant.HTTP_CONNECT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, Constant.HTTP_SO_TIMEOUT);
		HttpConnectionParams.setLinger(params, 30);
		
		TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
					public void checkServerTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
				}
		};
		
		try
		{
			SSLContext sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null, trustAllCerts, new java.security.SecureRandom());
			SSLSocketFactory sf = new SSLSocketFactory(sslcontext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			Scheme https = new Scheme("https", 443, sf);
			cm.getSchemeRegistry().register(https);
		}
		catch(Exception e)
		{
			Logger.error("Could not add SSL");
			e.printStackTrace();
		}
		
		return httpclient;
	}
}
