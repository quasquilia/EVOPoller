package it.guarascio.evopoller.publishers;

public class PublisherFactory {
	public static IEVOBeanPublisher createPublisher(String publisherCode) throws Exception {
		IEVOBeanPublisher publisher;

		if (PVPublisher.MODE.equalsIgnoreCase(publisherCode)) {
			publisher = new PVPublisher(false);
		} else if (SystemOutPublisher.MODE.equalsIgnoreCase(publisherCode)) {
			publisher = new SystemOutPublisher();
		} else {
			publisher = new CompositePublisher();
			((CompositePublisher)publisher).addPublisher(new SystemOutPublisher());
			((CompositePublisher)publisher).addPublisher(new PVPublisher(false));			
		}
		return publisher;

	}
}
