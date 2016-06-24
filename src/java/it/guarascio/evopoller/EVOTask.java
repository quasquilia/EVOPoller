package it.guarascio.evopoller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import it.guarascio.evopoller.EVOTaskData.QueueBeanElement;
import it.guarascio.evopoller.evorequests.RequestFactory;
import it.guarascio.evopoller.publishers.IEVOBeanPublisher;
import it.guarascio.evopoller.scheduler.IScheduledTask;
import it.guarascio.evopoller.sender.MessageSender;
import it.guarascio.evopoller.sender.MessageSenderFactory;
import it.guarascio.evopoller.statisticspublisher.Statistics;
import it.guarascio.evopoller.statisticspublisher.StatisticsSenderFactory;
import it.guarascio.evopoller.utils.DateUtils;
import it.guarascio.evopoller.utils.Logger;

public class EVOTask implements IScheduledTask {

	private static final int ZERO_WAIT_NUM = 4;
	private static final double THRESHOLD_POWER = 1;
	
	private EVOTaskData dailyData = null;
	private EVOTaskData residualData = null;
	private IEVOBeanPublisher publisher;
	
	private final MessageSenderFactory messageSenderFactory;
	private final StatisticsSenderFactory statisticsSenderFactory;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public EVOTask(MessageSenderFactory messageSenderFactory, StatisticsSenderFactory statisticsSenderFactory) {
		this.messageSenderFactory = messageSenderFactory;
		this.statisticsSenderFactory = statisticsSenderFactory;
	}
	
	
	private void reset() {
		residualData = null;
		dailyData = new EVOTaskData(0, null, null, null, null);
	}
	
	@Override
	public boolean doTask() {

		EVOBean bean = RequestFactory.createRequest().performRequest();

		// Se stamani non sono ancora partito, controllo se il messaggio e'utile per partire
		if (dailyData.firstBean == null && bean != null && bean.getPower() > THRESHOLD_POWER) {
			dailyData.powerPeakBean = dailyData.lastBean = dailyData.lastPowerBean = dailyData.firstBean = bean;
			processResidualData();
			sendGoodMorningMail();
		}
			
		// Se sono partito ...
		if (dailyData.firstBean != null) {
			
			// Se la potenza e'andata a zero o l'inverter non e'piu'raggiungibile
			// --> Conto fino ad un tot e se la situazione non si risolve 
			//     presumo che la giornata sia finita
			if ((bean == null || bean.getPower() < THRESHOLD_POWER)
					&& DateUtils.now().get(Calendar.HOUR_OF_DAY) >= 17) {
				dailyData.zeroCount++;
				if (dailyData.zeroCount == ZERO_WAIT_NUM) {
					
					if (dailyData.powerPeakBean != null && dailyData.lastBean != null && dailyData.firstBean != null) {
						sendGoodNightMail();
					}
					
					Logger.info("Good night!");
					Logger.info("");
					reset();
					return false;
				}
			}
			
			if(bean != null) {
				// Pubblicazione del dato (se significativo)
				if (publisher != null && dailyData.zeroCount <= 1) {
 					double initialEnergy = dailyData.firstBean.getEnergy();
 					boolean published = publisher.publish(bean, initialEnergy);
 					dailyData.addMeteoStat(bean.getWeather());
 					if (!published) {
 						Logger.info("Enqueued non-published data");
 						dailyData.enqueueDelayedBean(bean, initialEnergy);
 					}
				}
				
				dailyData.registerTemperature(bean.getTemperature());
				
				dailyData.lastBean = bean;
				if (bean.getPower() > THRESHOLD_POWER) {
					dailyData.lastPowerBean = bean;
					if (bean.getPower() > dailyData.powerPeakBean.getPower()) {
						dailyData.powerPeakBean = bean;
					}
					dailyData.zeroCount = 0; // resetto il contatore di messaggi a potenza zero
				}
			}
			
			// Di quella via provo a pubblicare qualche bean rimasto in sospeso
			processDelayedBeans(1);
		}
		
		writeTaskData();
		
		return true;
	}

	@Override
	public void stopped() {
		// TODO Auto-generated method stub
		Logger.info(dateFormat.format(Calendar.getInstance().getTime()) + " STOP.");
		reset();
	}

	
	@Override
	public void started() {
		Logger.info("-------------------------------------------------");
		Logger.info(" Good morning!");
		reset();
		EVOTaskData savedData = readTaskData();
		if (savedData != null && savedData.lastBean != null) {
			int daysBetween = Math.abs(DateUtils.daysBetween(DateUtils.now().getTime(), savedData.lastBean.getDate()));
			
			if (daysBetween == 0) {
				// Ho trovato roba salvata relativa alla giornata di oggi --> la recupero			
				Logger.info("Following information has been recovered for today: " + savedData.toString());
				dailyData = savedData;
				residualData = null;
				
			} else if (daysBetween > 1 || savedData.zeroCount == 0) {
				// Ho trovato roba che risale almeno a ierilaltro oppure a ieri ma la giornata di ieri non era conclusa
				// --> memorizzo le informazioni per salvare (appena ho le info riguardo all'energia di oggi) i dati residui dei giorni passati 
				residualData = savedData;
				
				// Trasferisco eventuali bean da pubblicare rimasti in sospeso nella coda "odierna" dei bean sospesi
				if (residualData.hasDelayedBeans()) {
					for(QueueBeanElement elem = residualData.dequeueDelayedBean(); elem != null; residualData.dequeueDelayedBean()) {
						dailyData.enqueueDelayedBean(elem);
					}
				}
			}			
		}		
	}

	public IEVOBeanPublisher getPublisher() {
		return publisher;
	}

	public void setPublisher(IEVOBeanPublisher publisher) {
		this.publisher = publisher;
	}
	
	private void sendGoodMorningMail() {
		MessageSender mailSender = messageSenderFactory.createSender();
		mailSender.send("Good morning!", "L'inverter si e'avviato alle " + new Date(), false);
	}

	private void sendGoodNightMail() {
		
		Date diff = new Date(dailyData.lastPowerBean.getDate().getTime() - dailyData.firstBean.getDate().getTime());		
		
		Statistics statistics = new Statistics();
		statistics.dailyEnergy = dailyData.lastBean.getEnergy() - dailyData.firstBean.getEnergy();
		statistics.avgPower = statistics.dailyEnergy*1000/(diff.getTime()/(1000*3600));
		statistics.peakPower = dailyData.powerPeakBean.getPower();
		statistics.workingTime = diff;
		statistics.peakHour = dailyData.powerPeakBean.getDate();
		
		sendStatisticsMail(statistics);
		statisticsSenderFactory.createSender().send(statistics);
	}


	private void sendStatisticsMail(Statistics statistics) {
		MessageSender mailSender = messageSenderFactory.createSender();
		String energy = new DecimalFormat("#.00").format(statistics.dailyEnergy) + " KWh";
		SimpleDateFormat output = new SimpleDateFormat("HH:mm:ss");
		String workingTime = output.format(statistics.workingTime);
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(statistics.workingTime);
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);		
		String sAvgPower = new DecimalFormat("#.00").format(statistics.avgPower) + " W";
		
		String sPeakPower = new DecimalFormat("#.00").format(statistics.peakPower) + " W";
		String sPeakHour = output.format(statistics.peakHour);
		mailSender.send("Good night!",
				 "L'inverter si e'spento alle " + new Date() + "\n\n" +
				 "Energia generata: " + energy + "\n" +
				 "Ore di funzionamento: " + workingTime + "\n" +
				 "Potenza media: " + sAvgPower + "\n" + 
				 "Potenza di picco: " + sPeakPower + " raggiunta alle " + sPeakHour + "."  + "\n\n" + 
				 dailyData.printMeteoStats(),
				 false);
	}
	
	private void writeTaskData() {
		try{
	      //use buffering
	      OutputStream file = new FileOutputStream( "status.ser" );
	      OutputStream buffer = new BufferedOutputStream( file );
	      ObjectOutput output = new ObjectOutputStream( buffer );
	      try{
	        output.writeObject(dailyData);
	      }
	      finally{
	        output.close();
	      }
	    }  
	    catch(IOException ex){
	      ex.printStackTrace();
	    }
	}
	
	private EVOTaskData readTaskData() {
		try{
		  if (!new File("status.ser").exists()) {
			  return null;
		  }
	      //use buffering
	      InputStream file = new FileInputStream( "status.ser" );
	      InputStream buffer = new BufferedInputStream( file );
	      ObjectInput input = new ObjectInputStream( buffer );
	      try{
	        //deserialize the List
	    	return (EVOTaskData)input.readObject();
	      }
	      finally{
	        input.close();
	      }
	    } catch (Exception e) {
	    	e.printStackTrace();	    	
	    }
		return null;
	}
	
	private void processResidualData() {		
		
		if (residualData == null) {
			return;
		}
		int daysBetween = Math.abs(DateUtils.daysBetween(new Date(DateUtils.now().getTimeInMillis()), residualData.lastBean.getDate()));
		EVOBean firstBeanOfToday = dailyData.firstBean;

		// Roba piu'vecchia. Calcolo l'energia che e'stata prodotta nel frattempo e la redistribuisco "alla meglio"
		// frai giorni che mancano ad oggi
		double lastLoggedEnergy = residualData.lastBean.getEnergy();
		double energyOfToday = firstBeanOfToday.getEnergy();
		double deltaEnergyToDistribute = energyOfToday - lastLoggedEnergy;

		// Percentuale di tempo in cui e'mancato il log nell'ultimo giorno loggato
		double percFirstDay = 1;
		
		if (daysBetween > 1) {
			percFirstDay = 1 - DateUtils.percentageOfDay(residualData.lastBean.getDate());
		}

		// Quantita'da dare ad un singolo giorno
		double dailyQuantity = deltaEnergyToDistribute / (percFirstDay + daysBetween - 1);
		
		// Quantita'da dare al primo giorno
		double firstDailyQuantity = dailyQuantity * percFirstDay;
		
		double pseudoInitialEnergy = residualData.firstBean.getEnergy();			
		for (int i = 0; i < daysBetween; i++) {
			EVOBean pseudoBean = new EVOBean(residualData.lastBean);
			Date pseudoDate = DateUtils.addMinutes(residualData.lastBean.getDate(), 1);
			double pseudoEnergy;
			if (i == 0) {
				pseudoEnergy = residualData.lastBean.getEnergy() + firstDailyQuantity;					
			} else {
				pseudoEnergy = pseudoInitialEnergy + dailyQuantity;					
				pseudoDate = DateUtils.addDays(pseudoDate, i);
			}
			pseudoBean.setDate(pseudoDate);							
			pseudoBean.setEnergy(pseudoEnergy);
			Logger.info("Dumping residual energy: " + pseudoEnergy + " Wh at pseudo-date/time:" 
						+ Logger.dateFormat.format(pseudoDate));
			if (publisher !=  null) {
				boolean published = publisher.publish(pseudoBean, pseudoInitialEnergy);		
				if (!published) {
					dailyData.enqueueDelayedBean(pseudoBean, pseudoInitialEnergy);
				}
			}
			pseudoInitialEnergy = pseudoEnergy;
		}
		
		residualData = null;

	}
	
	private void processDelayedBeans(int maxBeansToProcess) {
		if (dailyData.hasDelayedBeans()) {
			for (int i = 0; i < maxBeansToProcess; i++) {
				QueueBeanElement element = dailyData.dequeueDelayedBean();
				if (element == null) {
					return;
				}
				boolean published = publisher.publish(element.bean, element.initialEnergy);		
				if (!published) {
					// Se non sono riuscito a pubblicarlo, lo rimetto in coda.
					dailyData.enqueueDelayedBean(element);
					return; //E'inutile che continui... riprovero'tra un po!
				}
			}
		}
	}
	



}
