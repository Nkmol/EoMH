package Buffs.BuffActions;

import Buffs.BuffAction;
import Player.Character;

public class buff2 implements BuffAction {

	//increase Max HP
	@Override
	public void startBuff(Character owner, int value) {
		// TODO Auto-generated method stub
		owner.setMaxHp((short)(owner.getMaxHp() + value));
	}

	@Override
	public void endBuff(Character owner, int value) {
		// TODO Auto-generated method stub
		owner.setMaxHp((short)(owner.getMaxHp() - value));
	}

}
