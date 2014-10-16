package Buffs;

import java.util.Timer;

import Player.Character;

public abstract class Buff {

	private Character owner;
	private BuffAction action;
	private Timer timer;
	private short value;
	private short buffLength;
	private short timeLeft;
	protected boolean started;
	
	public Buff(Character owner, short buffId, short buffLength, short buffValue){
		this.owner=owner;
		this.value=buffValue;
		this.buffLength=buffLength;
		this.timeLeft=this.buffLength;
		this.action=BuffMaster.getBuffAction(buffId);
		this.timer=new Timer();
		this.started=false;
	}
	
	public void activate(){
		startBuff();
	}
	
	protected boolean startBuff(){
		if(started==false){
			timer.scheduleAtFixedRate(new BuffTimer(this),4000,4000);
        	owner.addBuff(this);
			started=true;
			
			return true;
		}
		return false;
	}
	
	protected boolean endBuff(){
		if(started==true){
			this.timer.cancel();
			owner.removeBuff(this);
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
	
	public short getId(){
		return action.getId();
	}
	
	public long getTimeLeft(){
		return timeLeft;
	}
	
	public Character getOwner(){
		return owner;
	}
	
	public short getBuffValue(){
		return value;
	}
	
	public short getBuffTime() {
		return buffLength;
	}
	
	public BuffAction getAction(){
		return action;
	}
}
