package it.guarascio.evopoller.meteo;


public class MeteoFactory {

	public static MeteoProvider createProvider(String providerName) {
		MeteoProvider provider = null;
		if (YahooMeteoProvider.CODE.equalsIgnoreCase(providerName)) {
			provider = new YahooMeteoProvider();
		} else {
			provider = new WUndergroundMeteoProvider();
		}
		return provider;
		
	}
}
