package it.guarascio.evopoller.pvoutput;

import it.guarascio.evopoller.utils.Logger;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;


public class HttpSender {
	
	private String hostname;
	private String query;
	private String apikey;
	private String sid;
	private int port = 80;
	public static final String VERSION = "1.4.4";
	private String format = null;
	
	public HttpSender(String url, String apiKey, String sid) {
		parseURL(url);
		this.apikey = apiKey;
		this.sid = sid;
	}
	
	private void parseURL(String url) 
	{
		if(url.startsWith("http://"))
		{
			url = url.substring(7);
		}
				
		int portIndex = url.indexOf(':');
		int queryIndex = url.indexOf('/');
		
		if(portIndex > -1)
		{
			hostname = url.substring(0, portIndex);
			
			if(queryIndex > -1)
			{
				port = Util.getNumber(url.substring(portIndex+1, queryIndex));
			}
			else
			{
				port = Util.getNumber(url.substring(portIndex+1));
			}
			
			if(queryIndex > -1)
			{
				query = url.substring(queryIndex);
			}
		}
		else
		{
			if(queryIndex > -1)
			{
				hostname = url.substring(0, queryIndex);
				
				query = url.substring(queryIndex);
			}
			else
			{
				hostname = url;
			}
		}
		
		if(query != null)
		{
			query = query.trim();
		}
		
		query = "/service/r2/addbatchstatus.jsp";
	}
	
	private static HttpGet getPVOutputGet(String hostname, int port, String query, String sid, String key, String format)
	{
		StringBuffer url = new StringBuffer();
		url.append("http://").append(hostname).append(":").append(port).append(query);
		
		Logger.info(">>> " + url.toString());
		
		HttpGet http = new HttpGet(url.toString());
		http.setHeader("X-Pvoutput-SystemId", sid);
		http.setHeader("X-Pvoutput-Apikey", key);
		
		if(format != null)
		{
			http.setHeader("User-Agent", "PVOutput/1 (SystemId:" + sid + "; Inverter:" + format + ")");
		}
		else
		{
			http.setHeader("User-Agent", "PVOutput/1");
		}
		
		return http;
	}
	
//	private static HttpPost getPVOutputPost(String hostname, int port, String query, String sid, String key, String format, String data)
//	{
//		StringBuffer url = new StringBuffer();
//		url.append("http://").append(hostname).append(":").append(port).append(query);
//		
//		HttpPost http = new HttpPost(url.toString());
//		http.setHeader("X-Pvoutput-SystemId", sid);
//		http.setHeader("X-Pvoutput-Apikey", key);
//				
//		if(format != null)
//		{
//			http.setHeader("User-Agent", "PVOutput/" + VERSION + " (SystemId:" + sid + "; Inverter:" + format + ")");
//		}
//		else
//		{
//			http.setHeader("User-Agent", "PVOutput/" + VERSION);
//		}
//		
//		if(data != null)
//		{
//			try
//			{
//				StringEntity in = new StringEntity(data);
//				http.setEntity(in);
//			}
//			catch(Exception e)
//			{
//				
//			}
//		}
//		
//		return http;
//	}
	
	private static HttpClient getWebClient()
	{	
		
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
	
	public ResponseData sendbatch(String request)
	{
		ResponseData response = new ResponseData();
				
		response.status = StatusCode.UNKNOWN;
		
		String[] lines = request.split(";");
		
		if(lines != null)
		{
			for(int i = 0; i < lines.length; i++)
			{
				Logger.info(">>> " + lines[i]);
			}
		}
		
		int rc = -1;

		String q = query + "?data=" + request;
		
		org.apache.http.client.HttpClient httpclient = getWebClient();
		HttpGet httpget = getPVOutputGet(hostname, port, q, sid, apikey, format);
		
		try
		{
			HttpContext context = new BasicHttpContext();
			HttpResponse response1 = httpclient.execute(httpget, context);
			rc = response1.getStatusLine().getStatusCode();
			
			HttpEntity entity = response1.getEntity();
			String s = null;
			
			if(entity != null)
				s = EntityUtils.toString(entity);
	
			if(s != null && s.trim().length() > 0)
			{
				if(rc == 200)
				{
					Logger.info("<<< " + s.trim());
				}
				else
				{
					Logger.info("[WARN] <<< [" + rc + "] " + s.trim());
				}
				
				response.response = s.trim();
				response.message = response.response;
			}
			else
			{
				Logger.error("<<< Empty response [" + rc + "]");
			}
			
			if(rc == 200)
			{
				response.status = StatusCode.SUCCESS;
			}
			else if(rc == 401)
			{
				response.status = StatusCode.ERROR_SECURITY;
			}
			else if(rc == 400 || rc == 403)
			{
				response.status = StatusCode.ERROR_DATA;
			}
		}
		catch(UnknownHostException e)
		{
			response.status = StatusCode.ERROR_CONNECTION;
			Logger.error("Unknown Host: " + e.getMessage());
			
			if(httpget != null)
			{
				httpget.abort();
			}
		}
		catch(IOException e)
		{
			response.status = StatusCode.ERROR_CONNECTION;
			Logger.error("Connection Error: " + e.getMessage());
			
			if(httpget != null)
			{
				httpget.abort();
			}
		}
		catch(Exception e)
		{			
			response.status = StatusCode.ERROR_UNKNOWN;
			Logger.error("Unknown error");
			e.printStackTrace();
			
			if(httpget != null)
			{
				httpget.abort();
			}
		}
		
		if(response.status != StatusCode.SUCCESS)
		{		
			// simulate the error response
			StringBuffer s = new StringBuffer();
			
			int energy = -1;
			
			if(response.status == StatusCode.ERROR_DATA 
					&& response.message.indexOf("lower than previously recorded value") > 0)
			{
				int start = response.message.indexOf('[');
				int end = response.message.indexOf(']');
				
				if(end > start)
				{
					energy = Util.getInt(response.message.substring(start+1, end));
				}
			}
			
			lines = request.split(";");
			
			if(lines != null)
			{
				for(int i = 0; i < lines.length; i++)
				{
					String[] fields = lines[i].split(",");
					
					if(fields != null && fields.length >= 4)
					{
						s.append(";").append(fields[0])
						.append(",").append(fields[1])
						.append(",").append(fields[2])
						.append(",").append(fields[3]);
						
						// mark as unrecoverable
						if(energy > -1 && energy == (int)Util.getDouble(fields[2]))
						{
							s.append(",3");
						}
						else
						{
							s.append(",0");
						}
					}
				}
			}
			
			if(s.length() > 0)
			{
				s.deleteCharAt(0);
				
				response.response = s.toString();
				
				Logger.info("<<< [" + rc + "] " + response.response);
			}
		}
		
		return response;
	}
}

