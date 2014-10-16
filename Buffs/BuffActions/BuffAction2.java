package Buffs.BuffActions;

import Buffs.BuffAction;
import Player.Character;

public class BuffAction2 implements BuffAction {
	
	short buffId;
	
	public BuffAction2(short buffId){
		this.buffId=buffId;
	}

	//increase Max HP
	public void startBuff(Character ch,Object value) {
		//no action except for updating character stats
		ch.calculateCharacterStats();
	}

	public void endBuff(Character ch,Object value) {
		//no action except for updating character stats
		ch.calculateCharacterStats();
	}
	
	public short getId(){
		return buffId;
	}
	
	public String getValueType(){
		return "";
	}

}
