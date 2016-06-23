/**
 * 
 */
package org.fedy2.weather.binding.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.fedy2.weather.data.unit.SpeedUnit;

/**
 * @author fedy2
 *
 */
public class SpeedUnitAdapter extends XmlAdapter<String, SpeedUnit> {
	
	/**
	 * Official documentation says kph but the service returns km/h.
	 */
	protected static final String KMH = "km/h";
	protected static final String MPH = "mph";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SpeedUnit unmarshal(String v) throws Exception {
		if (MPH.equalsIgnoreCase(v)) return SpeedUnit.MPH;
		if (KMH.equalsIgnoreCase(v)) return SpeedUnit.KMH;
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String marshal(SpeedUnit v) throws Exception {
		switch (v) {
			case KMH: return KMH;
			case MPH: return MPH;
			default: return "";
		}
	}

}
