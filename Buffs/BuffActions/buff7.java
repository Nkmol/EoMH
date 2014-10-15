package Buffs.BuffActions;

import Buffs.BuffAction;
import Player.Character;

public class buff7 implements BuffAction {

	//increase Max HP
	@Override
	public void startBuff(Character owner, int value) {
		// TODO Auto-generated method stub
		System.out.println("NEW VALUE: " + (owner.getMaxhp() + value));
		owner.setMaxHp((short)(owner.getMaxhp() + value));
	}

	@Override
	public void endBuff(Character owner, int value) {
		// TODO Auto-generated method stub
		owner.setMaxHp((short)(owner.getMaxhp() - value));
	}

}
