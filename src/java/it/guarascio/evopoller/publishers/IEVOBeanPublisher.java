package it.guarascio.evopoller.publishers;

import it.guarascio.evopoller.EVOBean;

public interface IEVOBeanPublisher {
	public boolean publish(EVOBean bean, double initialEnergy);
}
