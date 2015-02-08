package Buffs.BuffActions;

import Buffs.BuffAction;
import Player.Character;
import Player.Fightable;

public class Summon implements BuffAction {
	
	short buffId;
	int casteruid;
	
	public Summon(short buffId){
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
		return "summon";
	}
	
	public boolean updateOverTime(Fightable owner, short value){
		return false;
	}

	@Override
	public void setCasterId(int uid) {
		casteruid = uid;
	}
}
