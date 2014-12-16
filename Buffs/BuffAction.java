package Buffs;

import Player.Fightable;
import Player.Character;

public interface BuffAction {

	public void startBuff(Character owner,short value);
	public void endBuff(Character owner,short value);
	public short getId();
	public String getValueType();
	public boolean updateOverTime(Fightable owner, short buffValue);
	public void setCasterId(int uid);
}
