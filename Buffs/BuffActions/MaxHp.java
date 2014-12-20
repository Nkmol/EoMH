package Buffs.BuffActions;

import Buffs.BuffAction;
import Player.Character;
import Player.Fightable;

public class MaxHp implements BuffAction {
	
	short buffId;
	
	public MaxHp(short buffId){
		this.buffId=buffId;
	}

	//increase Max HP
	public void startBuff(Character ch,short value) {
		//no action except for updating character stats
		ch.calculateCharacterStats();
	}

	public void endBuff(Character ch,short value) {
		//no action except for updating character stats
		ch.calculateCharacterStats();
	}
	
	public short getId(){
		return buffId;
	}
	
	public String getValueType(){
		return "maxhp";
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
