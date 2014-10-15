package Buffs;

import Player.Character;

public interface BuffAction {

	public void startBuff(Character owner,int value);
	public void endBuff(Character owner,int value);
}
