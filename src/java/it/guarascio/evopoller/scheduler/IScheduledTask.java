package it.guarascio.evopoller.scheduler;

public interface IScheduledTask {
	public boolean doTask();
	public void started();
	public void stopped();
}
