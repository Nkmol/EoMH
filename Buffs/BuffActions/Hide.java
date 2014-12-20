package Buffs.BuffActions;

import Buffs.BuffAction;
import Mob.Mob;
import Player.Character;
import Player.Fightable;
import ServerCore.ServerFacade;
import Tools.BitTools;
import World.OutOfGridException;

public class Hide implements BuffAction {
	
	short buffId;
	int casteruid;
	
	public Hide(short buffId){
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
		return "hiding";
	}
	
	public boolean updateOverTime(Fightable owner, short value){
		return false;
	}

	@Override
	public void setCasterId(int uid) {
		casteruid = uid;
	}
}
