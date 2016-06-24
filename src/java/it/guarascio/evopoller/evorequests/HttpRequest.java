package it.guarascio.evopoller.evorequests;

import it.guarascio.evopoller.EVOBean;
import it.guarascio.evopoller.meteo.MeteoProvider;
import it.guarascio.evopoller.utils.Logger;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class HttpRequest extends AbstractHtmlPageRequest {

	
	HttpRequest(String address, MeteoProvider meteoProvider) {
		super(address, meteoProvider);
	}
	
	@Override
	public EVOBean performRequest() {
	
    	    
        // configure the SSLContext with a TrustManager
		try {
	        URL url = new URL(address);
	        Logger.info("Opening page " + url);
	        URLConnection conn = url.openConnection();
	        EVOBean result = null;
	        InputStream is = null;
	        try {		        
	        	is = conn.getInputStream();
				result = processPage(is);
	        } catch (Exception e) {
	        	Charset charset = Charset.forName("UTF-8");
	        	ByteArrayOutputStream os = new ByteArrayOutputStream();
	        	PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, charset));
	        	e.printStackTrace(pw);
	        	pw.close();
	        	String s = new String(os.toByteArray(), charset);
	        	Logger.error(e.getMessage() + " at " + address);
	        	Logger.error(s);
	        } finally {
	        	if (is != null) {
	        		is.close();
	        	}
	        }
	        return result;
	        
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
   
}
