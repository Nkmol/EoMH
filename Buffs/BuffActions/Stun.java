package Buffs.BuffActions;

import Buffs.BuffAction;
import Player.Character;
import Player.Fightable;

public class Stun implements BuffAction {
	
	short buffId;
	
	public Stun(short buffId){
		this.buffId=buffId;
	}

	//increase Final Damage
	public void startBuff(Character ch,short value) {
		//no action except for updating character stats
	}

	public void endBuff(Character ch,short value) {
		//no action except for updating character stats
	}
	
	public short getId(){
		return buffId;
	}
	
	public String getValueType(){
		return "stun";
	}

	@Override
	public boolean updateOverTime(Fightable owner, short buffValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCasterId(int uid) {
		// TODO Auto-generated method stub
		
	}

}
