package Mob;

import Skills.CastableSkill;

/*
 * MobData.class
 * Stores all mobs data 
 */

public class MobData implements Cloneable{
	private int lvl, minatk, maxatk, defence, maxhp, basefame, coins;
	private int atksuc, defsuc, critsuc;
	private long basexp;
	private CastableSkill[] skills;
	private int deathId;
	private int aggroRange = 30;
	private int followRange = 200;
	private int moveRange = 300;
	private int attackRange = 5;
	private long respawnTime = 10000;
	private int [] grid;
	private int mobID;
	private int gridID;
	private int waypointCount, waypointHop;
	private int moveSpeed = 50;
	private int waypointDelay = 4;
	private int[] drops;
	private float[] dropchances;
	
	
	public int getLvl() {
		return lvl;
	}
	public void setLvl(int lvl) {
		this.lvl = lvl;
	}
	public int getDefence() {
		return defence;
	}
	public void setDefence(int defence) {
		if(defence>1000)
			defence=1000;
		this.defence = defence;
	}
	public long getBasexp() {
		return basexp;
	}
	public void setBasexp(long basexp) {
		this.basexp = basexp;
	}

	public int getAggroRange() {
		return aggroRange;
	}
	public void setAggroRange(int aggroRange) {
		this.aggroRange = aggroRange;
	}
	public int getFollowRange() {
		return followRange;
	}
	public void setFollowRange(int followRange) {
		this.followRange = followRange;
	}
	public int getMoveRange() {
		return moveRange;
	}
	public void setMoveRange(int moveRange) {
		this.moveRange = moveRange;
	}
	public long getRespawnTime() {
		return respawnTime;
	}
	public void setRespawnTime(long respawnTime) {
		this.respawnTime = respawnTime;
	}
	public int[] getGrid() {
		return grid;
	}
	public void setGrid(int[] grid) {
		this.grid = grid;
	}
	public int getMobID() {
		return mobID;
	}
	public void setMobID(int mobID) {
		this.mobID = mobID;
	}
	public int getGridID() {
		return gridID;
	}
	public void setGridID(int gridID) {
		this.gridID = gridID;
	}
	public int getWaypointHop() {
		return waypointHop;
	}
	public void setWaypointHop(int waypointHop) {
		this.waypointHop = waypointHop;
	}
	public int getWaypointCount() {
		return waypointCount;
	}
	public void setWaypointCount(int waypointCount) {
		this.waypointCount = waypointCount;
	}
	public int getAttackRange() {
		return attackRange;
	}
	public void setAttackRange(int attackRange) {
		this.attackRange = attackRange;
	}
	public int getBasefame() {
		return basefame;
	}
	public void setBasefame(int basefame) {
		this.basefame = basefame;
	}
	public int getMaxhp() {
		return maxhp;
	}
	public void setMaxhp(int maxhp) {
		this.maxhp = maxhp;
	}
	public int getMoveSpeed() {
		return this.moveSpeed;
	}
	public int getWaypointDelay() {
		return waypointDelay;
	}
	public void setWaypointDelay(int waypointDelay) {
		this.waypointDelay = waypointDelay;
	}
	public CastableSkill[] getSkills() {
		return skills;
	}
	public void setSkills(CastableSkill[] skills) {
		this.skills = skills;
	}
	public int getDeathId() {
		return deathId;
	}
	public void setDeathId(int deathId) {
		this.deathId = deathId;
	}
	public int getCoins() {
		return coins;
	}
	public void setCoins(int coins) {
		this.coins = coins;
	}
	public int[] getDrops() {
		return drops;
	}
	public void setDrops(int[] drops) {
		this.drops = drops;
	}
	public float[] getDropchances() {
		return dropchances;
	}
	public void setDropchances(float[] dropchances) {
		this.dropchances = dropchances;
	}
	public int getMinatk() {
		return minatk;
	}
	public void setMinatk(int minatk) {
		this.minatk = minatk;
	}
	public int getMaxatk() {
		return maxatk;
	}
	public void setMaxatk(int maxatk) {
		this.maxatk = maxatk;
	}
	public int getAtksuc() {
		return atksuc;
	}
	public void setAtksuc(int atksuc) {
		this.atksuc = atksuc;
	}
	public int getDefsuc() {
		return defsuc;
	}
	public void setDefsuc(int defsuc) {
		this.defsuc = defsuc;
	}
	public int getCritsuc() {
		return critsuc;
	}
	public void setCritsuc(int critsuc) {
		this.critsuc = critsuc;
	}
	
}
