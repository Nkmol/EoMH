package Buffs.BuffActions;

import Buffs.BuffAction;
import Player.Character;
import Player.Fightable;

public class LifeTransform implements BuffAction {
	
	short buffId;
	int casteruid;
	
	public LifeTransform(short buffId){
		this.buffId=buffId;
	}

	//Damage taken reduces Mana
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
		return "lifeTransform";
	}
	
	public boolean updateOverTime(Fightable owner, short value){
		return false;
	}

	@Override
	public void setCasterId(int uid) {
		casteruid = uid;
	}
}
