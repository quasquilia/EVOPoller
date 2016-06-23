/**
 * 
 */
package org.fedy2.weather.binding.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.fedy2.weather.data.unit.PressureUnit;

/**
 * Units of barometric pressure adapter, in for pounds per square inch or mb for millibars.
 * @author fedy2
 *
 */
public class PressureUnitAdapter extends XmlAdapter<String, PressureUnit> {

	protected static final String MB = "mb";
	protected static final String IN = "in";
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PressureUnit unmarshal(String v) throws Exception {
		if (IN.equalsIgnoreCase(v)) return PressureUnit.IN;
		if (MB.equalsIgnoreCase(v)) return PressureUnit.MB;
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String marshal(PressureUnit v) throws Exception {
		switch (v) {
			case IN: return IN;
			case MB: return MB;
			default: return "";
		}
	}

}
