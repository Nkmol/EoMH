package timer;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SystemTimer {
	private Timer timer;
	private static SystemTimer instance;
	
	private SystemTimer(){
		this.timer = new Timer(true);
		
	}
	public static synchronized SystemTimer getInstance(){
		if (instance == null){
			instance = new SystemTimer();
		}
		return instance;
	}
	public void addTask(TimerTask task, long delay){
		this.timer.schedule(task, delay);
		
	}
	public void addTask(TimerTask task, Date date){
		this.timer.schedule(task, date);
	}
	public void addTask(TimerTask task, long delay, long period){
		this.timer.schedule(task, delay, period);
	}

}
