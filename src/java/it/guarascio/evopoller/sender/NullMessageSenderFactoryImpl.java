package it.guarascio.evopoller.sender;

public class NullMessageSenderFactoryImpl implements MessageSenderFactory {
	@Override
	public MessageSender createSender() {
		return new MessageSender() {			
			@Override
			public void send(String subject, String body, boolean html) {				
			}
		};
	}
}
