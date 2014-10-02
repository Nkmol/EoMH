package item;

public class EquipableSetItem extends EquipableItem {

	/*
	 * Set attributes
	 */
	private short[] setStats = new short[5]; //str, int, vit, agi, dex
	private int setHp;
	private int setMana;
	private int setStamina;
	private int setAtk;
	private int setDef;
	private float setAttSucc;
	private float setDefSucc;
	private float setCritSucc;
	private short setCritDmg;
	private float setTypeDmg;
	private int setPieces;
	
	public EquipableSetItem(int id) {
		super(id);
	}
	
	public EquipableSetItem getSetItem(){
		return this;
	}

	public short[] getStats() {
		return setStats;
	}

	public void setStats(short[] stats) {
		this.setStats = stats;
	}

	public int getSetBonusHp() {
		return setHp;
	}

	public void setSetBonusHp(int hp) {
		this.setHp = hp;
	}

	public int getSetBonusMana() {
		return this.setMana;
	}

	public void setSetBonusMana(int mana) {
		this.setMana = mana;
	}

	public int getSetBonusStamina() {
		return setStamina;
	}

	public void setSetBonusStamina(int stamina) {
		this.setStamina = stamina;
	}

	public int getSetBonusAtk() {
		return setAtk;
	}

	public void setSetBonusAtk(int atk) {
		this.setAtk = atk;
	}

	public int getSetBonusDef() {
		return setDef;
	}

	public void setSetBonusDef(int def) {
		this.setDef = def;
	}

	public float getSetBonusAttSucc() {
		return setAttSucc;
	}

	public void setSetBonusAttSucc(float attSucc) {
		this.setAttSucc = attSucc;
	}

	public float getSetBonusDefSucc() {
		return setDefSucc;
	}

	public void setSetBonusDefSucc(float defSucc) {
		this.setDefSucc = defSucc;
	}

	public float getSetBonusCritSucc() {
		return setCritSucc;
	}

	public void setSetBonusCritSucc(float critSucc) {
		this.setCritSucc = critSucc;
	}

	public short getSetBonusCritDmg() {
		return setCritDmg;
	}

	public void setSetBonusCritDmg(short critDmg) {
		this.setCritDmg = critDmg;
	}

	public float getSetBonusTypeDmg() {
		return setTypeDmg;
	}

	public void setSetBonusTypeDmg(float typeDmg) {
		this.setTypeDmg = typeDmg;
	}
	public int getSetPieces() {
		return setPieces;
	}
	public void setSetPieces(int setPieces) {
		this.setPieces = setPieces;
	}

	
}
