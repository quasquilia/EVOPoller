package it.guarascio.evopoller.publishers;

import it.guarascio.evopoller.http.HttpClientProvider;

public class PublisherFactory {
	private final HttpClientProvider httpClientProvider;
	
	public PublisherFactory(HttpClientProvider httpClientProvider) {
		this.httpClientProvider = httpClientProvider;
	}
	
	public IEVOBeanPublisher createPublisher(String publisherCode) throws Exception {
		IEVOBeanPublisher publisher;

		if (PVPublisher.MODE.equalsIgnoreCase(publisherCode)) {
			publisher = new PVPublisher(httpClientProvider, false);
		} else if (SystemOutPublisher.MODE.equalsIgnoreCase(publisherCode)) {
			publisher = new SystemOutPublisher();
		} else {
			publisher = new CompositePublisher();
			((CompositePublisher)publisher).addPublisher(new SystemOutPublisher());
			((CompositePublisher)publisher).addPublisher(new PVPublisher(httpClientProvider, false));			
		}
		return publisher;

	}
}
