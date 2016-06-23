/**
 * 
 */
package org.fedy2.weather.binding.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.fedy2.weather.data.unit.DegreeUnit;

/**
 * Degree unit adapter: f for Fahrenheit or c for Celsius (character)
 * @author fedy2
 */
public class DegreeUnitAdapter extends XmlAdapter<String, DegreeUnit> {

	protected static final String CELSIUS = "c";
	protected static final String FAHRENHEIT = "f";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DegreeUnit unmarshal(String v) throws Exception {
		if (FAHRENHEIT.equalsIgnoreCase(v)) return DegreeUnit.FAHRENHEIT;
		if (CELSIUS.equalsIgnoreCase(v)) return DegreeUnit.CELSIUS;
		//logger.warning("Unknown degree unit \"{}\"", v);		
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String marshal(DegreeUnit v) throws Exception {
		switch (v) {
			case CELSIUS: return CELSIUS;
			case FAHRENHEIT: return FAHRENHEIT;
			default: return "";
		}
	}

}
