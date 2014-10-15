package Buffs.BuffActions;

import Buffs.BuffAction;
import Player.Character;

public class buff17 implements BuffAction {

	//increase Final Damage
	@Override
	public void startBuff(Character owner, int value) {
		// TODO Auto-generated method stub
		System.out.println("NEW VALUE" + (short)(owner.getMinDmg() * value));
		owner.setMinDmg((short)(owner.getMinDmg() * value));
	}

	@Override
	public void endBuff(Character owner, int value) {
		// TODO Auto-generated method stub
		owner.setMinDmg((short)(owner.getMinDmg() / value));
	}

}
