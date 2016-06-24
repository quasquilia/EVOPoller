package it.guarascio.evopoller.sender;

 
import it.guarascio.evopoller.utils.Logger;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
 
class SendMailTLS implements MessageSender {
	
	private final MailBean mail;
 
	SendMailTLS(MailBean mail) {
		super();
		this.mail = mail;
	}

	/* (non-Javadoc)
	 * @see it.guarascio.evopoller.mailsender.MessageSender#send(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public void send(String subject, String body, boolean html) {
 
		Properties props = new Properties();
		if (mail.getSmtpUser() != null) {
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
		}
		props.put("mail.smtp.host", mail.getSmtpServer());
		//props.put("mail.smtp.host", "email.grupposervizi.it");
		props.put("mail.smtp.port", "587");
 
		Session session = Session.getInstance(props,
				mail.getSmtpUser() == null
				? null
				: new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(mail.getSmtpUser(), mail.getSmtpPassword());
					}
  		});
 
		try {			
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(mail.getSender()));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(mail.getRecipients()));
			
			message.setSubject(subject);
			if (html) {
				message.setContent(body, "text/html; charset=utf-8");
			} else {
				message.setText(body);
			} 
			Transport.send(message);
 
			Logger.info("Done");
 
		} catch (MessagingException e) {
			e.printStackTrace();;
		}
	}
}