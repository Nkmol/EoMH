package Player;

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
	public int getCritdmg();
	
}
