package Buffs;

import Player.Character;

public interface BuffAction {

	public void startBuff(Character owner,Object value);
	public void endBuff(Character owner,Object value);
	public short getId();
	public String getValueType();
}
