package it.guarascio.evopoller;

import it.guarascio.evopoller.evorequests.RequestFactory;
import it.guarascio.evopoller.mailsender.MailBean;
import it.guarascio.evopoller.mailsender.SendMailTLS;
import it.guarascio.evopoller.meteo.MeteoFactory;
import it.guarascio.evopoller.meteo.MeteoProvider;
import it.guarascio.evopoller.publishers.IEVOBeanPublisher;
import it.guarascio.evopoller.publishers.PublisherFactory;
import it.guarascio.evopoller.scheduler.Scheduler;
import it.guarascio.evopoller.utils.Logger;

public class EvoPoller {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		System.out.println("EVO Poller v 1.4\n");
		
		if (args.length < 3) {
			printUsage();
			return;
		}
		String startTime = args[0];
		String[] tokens = startTime.split("\\:");
		if (tokens.length != 2) {
			Logger.error("Invalid time: " + startTime);
			printUsage();			
			return;			
		}
		
		// primo parametro: hh:mm
		int hour, minutes;
		try {
			hour = Integer.parseInt(tokens[0]);
			minutes = Integer.parseInt(tokens[1]);
		} catch (NumberFormatException e) {
			Logger.error("Invalid time: " + startTime);
			printUsage();			
			return;						
		}
		
		// Poll interval		
		int pollInterval = 0;
		try {
			pollInterval = Integer.parseInt(args[1]);			
		} catch (NumberFormatException e) {
			Logger.error("Invalid poll interval: " + pollInterval);
			printUsage();			
			return;						
		}
		
		// Url
		String url = args[2];
		boolean useAuth = false;
		String username = null;
		String password = null;
		
		String publishMode = null;
		String meteo = null;

		MailBean mail = null;
		
		// Eventuali parametri opzionali		
		int cnt = 2;
		while (cnt < args.length - 1) {
			cnt++;
			
			if (args[cnt].equals("-auth")) {
				if (args.length < cnt + 2) {
					printUsage();			
					return;	
				}
				//cnt++;
				useAuth = true;
				username = args[++cnt];
				password = args[++cnt];
								
			} else if (args[cnt].equals("-mail")) {
				if (args.length < cnt + 3) {
					printUsage();			
					return;	
				}
				//cnt++;				
				String recipients = args[++cnt];
				String sender = args[++cnt];
				String smtpServer = args[++cnt];
				mail = new MailBean(sender, recipients, smtpServer);
				if (args.length >= cnt + 2) {
					mail.setSmtpUser(args[++cnt]);
					mail.setSmtpPassword(args[++cnt]);
				}
			} else if (args[cnt].equals("-publish")) {
				if (args.length < cnt) {
					printUsage();			
					return;	
				}
				//cnt++;
				publishMode = args[++cnt];
			} else if (args[cnt].equals("-meteo")) {
				if (args.length < cnt) {
					printUsage();			
					return;	
				}
				//cnt++;
				meteo = args[++cnt];
			}
			
		}
		
		Logger.info("Url: " + url);
		Logger.info("Morning start time: " + hour + ":" + minutes);
		Logger.info("Polling inteval (minutes): " + pollInterval);
		if (useAuth) {
			Logger.info("Http authentication user: " + username);
		}
		Logger.info("Polling inteval (minutes): " + pollInterval);
		if (mail != null) {
			Logger.info(mail.toString());
		}
		
		Logger.info("Publish mode: " + publishMode);
		Logger.info("Meteo provider: " + meteo);
		
		// Provider dei servizi meteo
		MeteoProvider meteoProvider = MeteoFactory.createProvider(meteo);
		RequestFactory.setMeteoProvider(meteoProvider);
		
		// Creazione task
		RequestFactory.setUrl(url);
		RequestFactory.setUseAuth(useAuth);
		
		if (useAuth) {
			RequestFactory.setUsername(username);
			RequestFactory.setPassword(password);
		}		
		EVOTask task = new EVOTask();
		task.setMail(mail);		
		
		// Inizializzazione del publisher
		IEVOBeanPublisher publisher = PublisherFactory.createPublisher(publishMode);
		task.setPublisher(publisher);
		
		// Inizializzazione dello scheduler
		Scheduler scheduler = new Scheduler(task);
		scheduler.setMinutesInterval(pollInterval);
		scheduler.setStartHour(hour, minutes);
		
		if (mail != null) {
			SendMailTLS mailSender = new SendMailTLS(mail);
			mailSender.send("Startup!", "Task avviato", false);
		}
		try {
			scheduler.start();
		} catch (Exception e) {
			if (mail != null) {
				try {
					SendMailTLS mailSender = new SendMailTLS(mail);					
					mailSender.send("ERROR ON EVOPoller!!!", stackTraceToString(e), false);
					
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		
	}
	
	private static void printUsage() {
		Logger.info("Usage:" +
				"\nevopoller hh:mm pollIntervalMin \"url\"\n\t" + 
					"[-auth username password]\n\t" + 
					"[-mail recipients sender smtpserver [smtpuser] [smtppassword]]\n\t" + 
					"[-publish pv|syso|composite]\n\t" +
					"[-meteo wunderground|yahoo"); 
	}
	
	private static String stackTraceToString(Throwable e) {
	    StringBuilder sb = new StringBuilder();
	    for (StackTraceElement element : e.getStackTrace()) {
	        sb.append(element.toString());
	        sb.append("\n");
	    }
	    return sb.toString();
	}

}
