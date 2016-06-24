package it.guarascio.evopoller.sender;

public interface MessageSender {

	void send(String subject, String body, boolean html);

}