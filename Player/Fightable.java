package Player;

import java.util.HashMap;

import Buffs.Buff;
import Buffs.BuffsException;
import World.OutOfGridException;

public interface Fightable {

	public String getName();
	public int getDefence();
	public int getHp();
	public int getMana();
	public int getLevel();
	public int getuid();
	public void recDamage(int uid, int amount) throws OutOfGridException;
	public int getAtkSuc();
	public int getDefSuc();
	public int getCritRate();
	public short getCritdmg();
	public void addBuff(Buff owner) throws BuffsException;
	public void removeBuff(Buff buff);
	public HashMap<Short, Buff> getBuffs();
	public boolean isAlive();
	public void refreshHpMpSp();
}
