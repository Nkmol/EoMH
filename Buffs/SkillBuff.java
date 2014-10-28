package Buffs;

import Player.Character;

public class SkillBuff extends Buff{
	
	public SkillBuff(Character owner, short buffId, long buffLength, short buffValue){
		super(owner, buffId, buffLength, buffValue);
	}
	
	@Override
	protected boolean startBuff() throws BuffsException{
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
}
