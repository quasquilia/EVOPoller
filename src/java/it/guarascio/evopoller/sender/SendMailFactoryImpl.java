package it.guarascio.evopoller.sender;

public class SendMailFactoryImpl implements MessageSenderFactory {
	
	private final MailBean mailBean;

	public SendMailFactoryImpl(MailBean mailBean) {
		this.mailBean = mailBean;
	}
	
	@Override
	public MessageSender createSender() {
		return new SendMailTLS(mailBean);
	}
}
