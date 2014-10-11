package Buffs;

import java.util.TimerTask;

public class BuffTimer extends TimerTask{

	private Buff owner;
	private long oldTime;
	
	public BuffTimer(Buff owner){
		
		this.owner=owner;
		this.oldTime=System.currentTimeMillis();
		
	}
	
	public void run(){
		
		long newTime=System.currentTimeMillis();
		owner.decreaseTimeLeft(newTime-oldTime);
		oldTime=newTime;
		
	}
	
}
