package Buffs;

import java.util.TimerTask;

public class BuffTimer extends TimerTask{

	private Buff owner;
	private long oldTime;
	
	public BuffTimer(Buff owner){
		this.owner=owner;
		this.oldTime=System.currentTimeMillis();
	}
	
	@Override
	public void run(){
		long newTime=System.currentTimeMillis();
		owner.decreaseTimeLeft(newTime-oldTime);
		oldTime=newTime;	
		if(owner.getAction().getValueType() == "DoT") {
			if(!owner.getAction().updateOverTime(owner.getOwner(), owner.getBuffValue()))
				owner.endBuff();
		}
	}
	
}
