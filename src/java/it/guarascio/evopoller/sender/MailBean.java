package it.guarascio.evopoller.sender;

public class MailBean {
	private String sender;
	private String recipients;
	private String smtpServer;
	private String smtpUser = null;
	private String smtpPassword = null;
	public MailBean(String sender, String recipients, String smtpServer) {
		super();
		this.sender = sender;
		this.recipients = recipients;
		this.smtpServer = smtpServer;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getRecipients() {
		return recipients;
	}
	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}
	public String getSmtpServer() {
		return smtpServer;
	}
	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}
	public String getSmtpUser() {
		return smtpUser;
	}
	public void setSmtpUser(String smtpUser) {
		this.smtpUser = smtpUser;
	}
	public String getSmtpPassword() {
		return smtpPassword;
	}
	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}
	@Override
	public String toString() {
		return "Send mail from: " + sender +" to: " + recipients +
				"\nSmtpSender: " + smtpServer +
				((smtpUser != null)
					? "Smtp auth: " + smtpUser + ", " + smtpPassword
					: "");
	}
	
	
}
