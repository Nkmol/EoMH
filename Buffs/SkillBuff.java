package Buffs;

import Player.Fightable;

public class SkillBuff extends Buff{
	public SkillBuff(Fightable owner, short buffId, long buffLength, short buffValue, int casterid){
			super(owner, buffId, buffLength, buffValue);
			super.getAction().setCasterId(casterid);
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
	public boolean endBuff(){
		if(super.endBuff()){
			//do skill buff end stuff
			
			return true;
		}
		return false;
	}
}
