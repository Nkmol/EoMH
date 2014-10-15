package Buffs.BuffActions;

import Buffs.BuffAction;
import Player.Character;

public class BuffActionExample implements BuffAction{
	
	public BuffActionExample(){

	}

	public void startBuff(Character ch,int value){
		//for example ch.addBonusAtk(value); ch.calculateCharacterStats();
	}
	
	public void endBuff(Character ch,int value){
		//for example ch.decreaseBonusAtk(value); ch.calculateCharacterStats();
	}	
}
