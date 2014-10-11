package Buffs;

import java.util.Timer;
import Player.Character;

public abstract class Buff {

	private Character owner;
	private BuffAction action;
	private Timer timer;
	private int value;
	private long buffLength;
	private long timeLeft;
	protected boolean started;
	
	public Buff(Character owner, int buffId, long buffLength, int buffValue){
		this.owner=owner;
		this.value=buffValue;
		this.buffLength=buffLength;
		this.timeLeft=this.buffLength;
		this.action=BuffMaster.getBuffAction(buffId);
		this.timer=new Timer();
		this.started=false;
	}
	
	protected boolean startBuff(){
		if(started==false){
			timer.scheduleAtFixedRate(new BuffTimer(this),4000,4000);
			action.startBuff(owner,value);
			started=true;
			return true;
		}
		return false;
	}
	
	protected boolean endBuff(){
		if(started==true){
			this.timer.cancel();
			action.endBuff(owner, value);
			started=false;
			return true;
		}
		return false;
	}
	
	public void decreaseTimeLeft(long time){
		timeLeft-=time;
		if(timeLeft<=0){
			endBuff();
		}
	}
	
	public long getTimeLeft(){
		return timeLeft;
	}
	
	public Character getOwner(){
		return owner;
	}
	
}
