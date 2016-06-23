/**
 * 
 */
package org.fedy2.weather.binding;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.fedy2.weather.data.Rss;

/**
 * @author fedy2
 *
 */
public class RSSParser {

	protected Unmarshaller unmarshaller;

	public RSSParser() throws JAXBException
	{
		JAXBContext context = JAXBContext.newInstance(Rss.class);
		unmarshaller = context.createUnmarshaller();
	}

	public Rss parse(String xml) throws JAXBException
	{
		return (Rss)unmarshaller.unmarshal(new StringReader(xml));
	}
	
}