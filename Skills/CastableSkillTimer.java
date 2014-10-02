package Skills;

import java.util.Timer;
import java.util.TimerTask;

public class CastableSkillTimer {

	private int id;
	private boolean isReady=true;
	private Timer timer;
	
	public CastableSkillTimer(int id){
		this.id=id;
		timer=new Timer();
	}
	
	public int getId() {
		return id;
	}

	public boolean isReady() {
		return isReady;
	}
	
	public void setReadyFalse() {
		isReady=false;
		timer.schedule(new TimerTask() {
			  @Override
			  public void run() {
				  CastableSkillTimer.this.isReady=true;
				cancel();
			  }
		}, 4500);
	}
	
}
