package Buffs;

import Player.Character;

public class SkillBuff extends Buff{

	public SkillBuff(Character owner, int buffId, long buffLength, int buffValue){
		super(owner, buffId, buffLength, buffValue);
		startBuff();
	}
	
	@Override
	protected boolean startBuff(){
		if(super.startBuff()){
			//do skill buff start stuff
			
			return true;
		}
		return false;
	}
	
	@Override
	protected boolean endBuff(){
		if(super.endBuff()){
			//do skill buff end stuff
			
			return true;
		}
		return false;
	}
	
	@Override
	public long getTimeLeft(){
		return super.getTimeLeft();
	}
	
	@Override
	public Character getOwner(){
		return super.getOwner();
	}
	
}
