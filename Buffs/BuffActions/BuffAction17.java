package Buffs.BuffActions;

import Buffs.BuffAction;
import Player.Character;

public class BuffAction17 implements BuffAction {
	
	short buffId;
	
	public BuffAction17(short buffId){
		this.buffId=buffId;
	}

	//increase Final Damage
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
		return "bonusDmg";
	}

}
