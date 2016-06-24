package it.guarascio.evopoller.statisticspublisher;

import java.util.GregorianCalendar;

import org.junit.Test;

import it.guarascio.evopoller.http.HttpClientProviderImpl;

public class TestIFTTStatisticsSenderFactory {

	@Test
	public void test() {
		IFTTStatisticsSenderFactory sut = new IFTTStatisticsSenderFactory(new HttpClientProviderImpl(), "c5UvPn8uTdnNaaRaoqYgI9");
		
		Statistics statistics = new Statistics();
		statistics.avgPower = 50.53;
		statistics.dailyEnergy = 1300.33;
		statistics.peakHour= new GregorianCalendar(0, 0, 0, 12, 20, 0).getTime();
		statistics.peakPower = 3600;
		statistics.workingTime = new GregorianCalendar(0, 0, 0, 10, 10, 33).getTime();
		statistics.meteoStats.put("Fine", 66.0);
		statistics.meteoStats.put("Rainy", 33.0);
		sut.createSender().send(statistics);
	}
}
