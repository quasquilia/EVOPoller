package it.guarascio.evopoller.sender;

import org.junit.Test;

import it.guarascio.evopoller.http.HttpClientProviderImpl;

public class TestIFTTMessageSenderFactory {

	@Test
	public void testIFTTMessageSenderFactory() {
		IFTTMessageSenderFactory sut = new IFTTMessageSenderFactory(new HttpClientProviderImpl(), "c5UvPn8uTdnNaaRaoqYgI9");
		
		sut.createSender().send("Good morning!", "Pippo pluto e paperino", false);
	}
}
