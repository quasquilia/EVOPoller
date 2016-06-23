/**
 * 
 */
package org.fedy2.weather.binding.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author fedy2
 *
 */
public class DateAdapter extends XmlAdapter<String, Date> {


	protected SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String marshal(Date v) throws Exception {
		return dateFormat.format(v);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date unmarshal(String v) throws Exception {

		try {
			return dateFormat.parse(v);
		} catch(Exception e) {
			return null;
		}
	}

}
