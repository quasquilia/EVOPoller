package it.guarascio.evopoller.publishers;

import it.guarascio.evopoller.EVOBean;

import java.util.ArrayList;
import java.util.List;

class CompositePublisher implements IEVOBeanPublisher {

	public static String MODE = "COMPOSITE";
	
	private List<IEVOBeanPublisher> publishers = new ArrayList<IEVOBeanPublisher>();
	@Override
	public boolean publish(EVOBean bean, double initialEnergy) {
		// TODO Auto-generated method stub
		boolean result = true;
		for (IEVOBeanPublisher publisher : publishers) {
			result &= publisher.publish(bean, initialEnergy);
		}
		return result;
	}
	
	public void addPublisher(IEVOBeanPublisher publisher) {
		publishers.add(publisher);
	}

}
