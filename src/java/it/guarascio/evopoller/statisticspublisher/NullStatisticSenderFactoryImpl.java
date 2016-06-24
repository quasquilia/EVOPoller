package it.guarascio.evopoller.statisticspublisher;

public class NullStatisticSenderFactoryImpl implements StatisticsSenderFactory {
	@Override
	public StatisticsSender createSender() {
		return new StatisticsSender() {			
			@Override
			public void send(Statistics statistics) {				
			}
		};
	}
}
