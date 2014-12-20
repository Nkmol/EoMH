package Buffs.BuffActions;

import Buffs.BuffAction;
import Player.Character;
import Player.Fightable;

public class Attack implements BuffAction {
	
	short buffId;
	
	public Attack(short buffId){
		this.buffId=buffId;
	}

	//increase Final Damage
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
		return "bonusAtk";
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
