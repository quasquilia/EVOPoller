package it.guarascio.evopoller.evorequests;

import it.guarascio.evopoller.EVOBean;
import it.guarascio.evopoller.meteo.MeteoProvider;

import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;

public class SSHRequest extends AbstractHtmlPageRequest {

	private final String auth;	
	
	SSHRequest(String address, String username, String password, MeteoProvider meteoProvider) {
		super(address, meteoProvider);
		
		if (username != null && password != null) {
			String userPassword = username + ":" + password;					
			auth = DatatypeConverter.printBase64Binary(userPassword.getBytes());
		} else {
			auth = null;
		}
	}
	
	private static final String base64code = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "abcdefghijklmnopqrstuvwxyz" + "0123456789" + "+/";
    private static final int splitLinesAt = 76;
	
	@Override
	public EVOBean performRequest() {
	
    	    
        // configure the SSLContext with a TrustManager
		try {
	        SSLContext ctx = SSLContext.getInstance("TLS");
	        ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
	        SSLContext.setDefault(ctx);
	        
	        URL url = new URL(address);
	        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
	        EVOBean result;
	        try {
		        if (auth != null) {	        	
		        	conn.setRequestProperty("Authorization", "Basic " + auth);
		        }
		        
		        conn.setHostnameVerifier(new HostnameVerifier() {
		            @Override
		            public boolean verify(String arg0, SSLSession arg1) {
		                return true;
		            }
		        });
		        
	        	result = processPage(conn.getInputStream());
	        } finally {
	        	conn.disconnect();
	        }
	        return result;
	        
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }	
	
}
