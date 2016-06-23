package it.guarascio.evopoller;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

public class EVOTaskData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6287379760265259011L;
	public int zeroCount;
	public EVOBean firstBean;
	public EVOBean lastBean;
	public EVOBean lastPowerBean;
	public EVOBean powerPeakBean;
	public double minTemp = Double.MAX_VALUE;
	public double maxTemp = Double.MIN_VALUE;
	public Map<String, Integer> meteoStats = null;
	public int sampleCount = 0;
	public Queue<QueueBeanElement> delayedBeans = new LinkedList<QueueBeanElement>();

	public EVOTaskData(int zeroCount, EVOBean firstBean, EVOBean lastBean,
			EVOBean lastPowerBean, EVOBean powerPeakBean) {
		this.zeroCount = zeroCount;
		this.firstBean = firstBean;
		this.lastBean = lastBean;
		this.lastPowerBean = lastPowerBean;
		this.powerPeakBean = powerPeakBean;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (firstBean != null) {
			sb.append("\n\tFirst request: " + firstBean.getDate());				
		}
		if (lastBean != null) {
			sb.append("\n\tLast request: " + lastBean.getDate());
			sb.append("\n\tLast request power: " + lastBean.getPower());
		}
		
		sb.append(printMeteoStats());
		
		return sb.toString();
	}
	
	public void registerTemperature(double temp) {
		if (temp > maxTemp) {
			maxTemp = temp;
		}
		if (temp < minTemp) {
			minTemp = temp;
		}
	}
	
	public String printMeteoStats() {
		DecimalFormat df = new DecimalFormat( "#.0", DecimalFormatSymbols.getInstance(Locale.ENGLISH)); 
		
		StringBuffer sb = new StringBuffer();
		sb.append("Temperature range: min " + df.format(minTemp) + "¡C, max " + df.format(maxTemp) + "¡C");
		if (meteoStats != null && meteoStats.size() > 0) {
			Map<String, Integer> mTemp = new TreeMap<String, Integer>(new ValueComparator(meteoStats));
			mTemp.putAll(meteoStats);
			sb.append("\n\nDaily weather statistics:");			
			for(Map.Entry<String, Integer> e : mTemp.entrySet()) {
				int percentage = (int)(e.getValue()*100 / sampleCount);
				sb.append("\n\t" + percentage + "%: " +  e.getKey());
			}
		}
		
		return sb.toString();
	}
	
	public void addMeteoStat(String meteoCondition) {
		if (meteoStats == null) {
			meteoStats = new HashMap<String, Integer>();
			sampleCount = 0;
		}
		Integer origCondition = meteoStats.get(meteoCondition);
		if (origCondition == null) {
			origCondition = 0;
		}
		meteoStats.put(meteoCondition, ++origCondition);
		sampleCount++;
	}
	
	private static class ValueComparator implements Comparator<String> {

	    Map<String, Integer> base;
	    public ValueComparator(Map<String, Integer> base) {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with equals.    
	    public int compare(String a, String b) {
	        if (base.get(a) >= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
	
	public void enqueueDelayedBean(EVOBean bean, double initialEnergy) {
		enqueueDelayedBean(new QueueBeanElement(bean, initialEnergy));
	}
	
	public void enqueueDelayedBean(QueueBeanElement element) {
		delayedBeans.offer(element);
	}
	
	public QueueBeanElement dequeueDelayedBean() {
		return delayedBeans.poll();
	}

	public boolean hasDelayedBeans() {
		return !delayedBeans.isEmpty();
	}
	
	public static class QueueBeanElement implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6136758100244466069L;
		public EVOBean bean;
		public double initialEnergy;
		
		public QueueBeanElement() {}
		public QueueBeanElement(EVOBean bean, double initialEnergy) {
			this.bean = bean;
			this.initialEnergy = initialEnergy;
		}
		
	}


}