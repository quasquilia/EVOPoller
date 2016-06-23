package it.guarascio.evopoller;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

import org.junit.Test;

public class TestEVOTaskData {

	@Test
	public void testMeteoStats() {
		EVOTaskData data = new EVOTaskData(0, null, null, null, null);
		data.addMeteoStat("Clear");
		data.addMeteoStat("Clear");
		data.addMeteoStat("Clear");
		
		data.addMeteoStat("Showers");
		data.addMeteoStat("Showers");
		
		data.addMeteoStat("Partly Cloudy");
		
		data.addMeteoStat("Mosly Cloudy");
		data.addMeteoStat("Mosly Cloudy");
		data.addMeteoStat("Mosly Cloudy");
		data.addMeteoStat("Mosly Cloudy");
		
		data.registerTemperature(10.3);
		data.registerTemperature(-3.1);
		data.registerTemperature(32.9);
		data.registerTemperature(15);
		
		String s = data.toString();

		TestCase.assertTrue(s.contains("30%: Clear"));
		TestCase.assertTrue(s.contains("10%: Partly Cloudy"));
		
		TestCase.assertTrue(s.contains("-3.1¡C"));
		TestCase.assertTrue(s.contains("32.9¡C"));
		
		System.out.println(data);
	}
	
	@Test
	public void testSerializable() throws Exception {
		EVOBean firstBean = new EVOBean(10, 100, 20, "Clear");
		EVOBean lastBean = new EVOBean(0, 1000, 19, "Partly Cloudy");
		EVOBean peakBean = new EVOBean(2000, 500, 27, "Clear");
		EVOBean lastPowerBean = new EVOBean(15, 1000, 19, "Partly Cloudy");
		
		EVOTaskData data = new EVOTaskData(0, firstBean, lastBean, peakBean, lastPowerBean);
		data.addMeteoStat("Clear");
		data.addMeteoStat("Clear");
		data.addMeteoStat("Clear");
		
		data.addMeteoStat("Showers");
		data.addMeteoStat("Showers");
		
		data.addMeteoStat("Partly Cloudy");
		
		data.addMeteoStat("Mosly Cloudy");
		data.addMeteoStat("Mosly Cloudy");
		data.addMeteoStat("Mosly Cloudy");
		data.addMeteoStat("Mosly Cloudy");
		
		data.enqueueDelayedBean(lastBean, 5);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;		
		out = new ObjectOutputStream(bos);   
		out.writeObject(data);
		byte[] yourBytes = bos.toByteArray();
		out.close();
		bos.close();

		ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
		ObjectInput in = null;	
		in = new ObjectInputStream(bis);
		EVOTaskData data2 = (EVOTaskData)in.readObject(); 			
		bis.close();
		in.close();		
	}


}
