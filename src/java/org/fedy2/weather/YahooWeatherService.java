/**
 * 
 */
package org.fedy2.weather;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.bind.JAXBException;

import org.fedy2.weather.binding.RSSParser;
import org.fedy2.weather.data.Channel;
import org.fedy2.weather.data.Rss;
import org.fedy2.weather.data.unit.DegreeUnit;

/**
 * @author fedy2
 *
 */
public class YahooWeatherService {
	
	public static final String WEATHER_SERVICE_BASE_URL = "http://weather.yahooapis.com/forecastrss";
	public static final String WOEID_PARAMETER_NAME = "w";
	public static final String DEGREES_PARAMETER_NAME = "u";
	
    protected static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	protected RSSParser parser;
	
	public YahooWeatherService() throws JAXBException
	{
		parser = new RSSParser();
	}
	
	public Channel getForecast(String woeid, DegreeUnit unit) throws JAXBException, IOException
	{
		//logger.trace("getForecast woeid: {} unit: {}", woeid, unit.toString());
		String url = composeUrl(woeid, unit);
		//logger.trace("url: {}",url);
		String xml = retrieveRSS(url);
		//logger.trace("xml: {}", xml);
		Rss rss = parser.parse(xml);
		return rss.getChannel();
	}
	
	protected String composeUrl(String woeid, DegreeUnit unit)
	{
		StringBuilder url = new StringBuilder(WEATHER_SERVICE_BASE_URL);
		url.append('?');
		url.append(WOEID_PARAMETER_NAME);
		url.append('=');
		url.append(woeid);
		url.append('&');
		url.append(DEGREES_PARAMETER_NAME);
		url.append('=');
		switch (unit) {
			case CELSIUS: url.append('c'); break;
			case FAHRENHEIT: url.append('f'); break;
		}
		return url.toString();		
	}
	
	protected String retrieveRSS(String serviceUrl) throws IOException
	{
		URL url = new URL(serviceUrl);
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();
		InputStreamReader reader = new InputStreamReader(is);
		StringWriter writer = new StringWriter();
		copy(reader, writer);
		reader.close();
		is.close();
				
		return writer.toString();
	}
	
	public static long copy(Reader input, Writer output) throws IOException {
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
