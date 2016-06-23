package it.guarascio.evopoller.publishers;

import it.guarascio.evopoller.EVOBean;
import it.guarascio.evopoller.utils.Logger;

import java.text.DecimalFormat;

class SystemOutPublisher implements IEVOBeanPublisher {

	public static String MODE = "SYSO";
	@Override
	public boolean publish(EVOBean bean, double initialEnergy) {
		DecimalFormat numberFormat = new DecimalFormat("#.0");
		double dailyEnergy = bean.getEnergy() - initialEnergy;					
		Logger.info(bean.toString() + ",\tDay Energy= " + numberFormat.format(dailyEnergy) + " KWh");	
		return true;
	}

}
