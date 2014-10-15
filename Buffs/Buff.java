package Buffs;

import java.util.Timer;

import Player.Character;
import Player.CharacterPackets;

public abstract class Buff {

	private Character owner;
	private BuffAction action;
	private Timer timer;
	private short value;
	private short buffLength;
	private short timeLeft;
	private short buffId;
	protected boolean started;
	
	public Buff(Character owner, short buffId, short buffLength, short buffValue){
		this.owner=owner;
		this.value=buffValue;
		this.buffLength=buffLength;
		this.timeLeft=this.buffLength;
		this.buffId = buffId;
		this.action=BuffMaster.getBuffAction(buffId);
		this.timer=new Timer();
		this.started=false;
	}
	
	protected boolean startBuff(){
		if(started==false){
			timer.scheduleAtFixedRate(new BuffTimer(this),4000,4000);
			action.startBuff(owner,value);
			started=true;
       
            //Save buff
        	owner.addBuff(buffId, this, getSlot());
			
			return true;
		}
		return false;
	}
	
	protected boolean endBuff(){
		if(started==true){
			this.timer.cancel();
			action.endBuff(owner, value);
			started=false;
					
			owner.removeBuff(buffId);
			
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
	
	private short getSlot() {
		 //Activate buff
    	if(owner.getBuff(buffId) != null) {
    		return owner.getBuffSlot(buffId);
    	}
    	else {
    		return (short)owner.getBuffLength();
    	}
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
}
