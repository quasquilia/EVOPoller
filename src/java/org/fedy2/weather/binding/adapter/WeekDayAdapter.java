/**
 * 
 */
package org.fedy2.weather.binding.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.fedy2.weather.data.unit.WeekDay;

/**
 * @author fedy2
 *
 */
public class WeekDayAdapter extends XmlAdapter<String, WeekDay> {
	

	@Override
	public WeekDay unmarshal(String v) throws Exception {
		try {
			return WeekDay.valueOf(v.toUpperCase());
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}		
	}

	@Override
	public String marshal(WeekDay v) throws Exception {
		return v!=null?v.toString():null;
	}

}
