package item;

public class EquipableItem extends ItemFrame {
	/*
	 * Requirements
	 */
	private boolean[] usableClass = new boolean[4]; //war, sin, mage, monk
	private short[] statRequirements = new short[5]; //str, int, vit, agi, dex
	private short faction;
	private int setHash;
	
	/*
	 * Attributes
	 */
	private short equipSlot;
	private short[] statBonuses = new short[5]; //str, int, vit, agi, dexter
	private int hp;
	private int mana;
	private int stamina;
	private int minDmg;
	private int maxDmg;
	private int atk;
	private int def;
	private float attSucc;
	private float critSucc;
	private float defSucc;
	private short critDmg;
	private short type;
	private float typeDmg;
	private float speed;
	
	
	public EquipableItem(int id) {
		super(id);
	}
	
	public void printData(){
		System.out.println("ID:" + this.getId() +" HP: " + this.hp + " Mana: " + this.mana + " stamina: " + this.stamina);
		System.out.println("Attack: " + this.atk + " Defense: " + this.def);
		System.out.println("Str: " + this.statBonuses[0] + " int: " + this.statBonuses[1] + " vit: " + this.statBonuses[2]);
		System.out.println("Agi: " + this.statBonuses[3] + " Dex: " + this.statBonuses[4]);
	}
	@Override
	public EquipableItem getEquipable(){
		return this;
	}

	public boolean[] getUsableClass() {
		return usableClass;
	}

	public void setUsableClass(boolean[] usableClass) {
		this.usableClass = usableClass;
	}

	public short[] getStatRequirements() {
		return statRequirements;
	}

	public void setStatRequirements(short[] statRequirements) {
		this.statRequirements = statRequirements;
	}

	public short getFaction() {
		return faction;
	}

	public void setFaction(short faction) {
		this.faction = faction;
	}

	public int getSetHash() {
		return setHash;
	}

	public void setSetHash(int setHash) {
		this.setHash = setHash;
	}

	public short[] getStatBonuses() {
		return statBonuses;
	}

	public void setStatBonuses(short[] statBonuses) {
		this.statBonuses = statBonuses;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	public int getStamina() {
		return stamina;
	}

	public void setStamina(int stamina) {
		this.stamina = stamina;
	}
	
	public int getMinDmg(){
		return minDmg;
	}
	
	public void setMinDmg(int minDmg){
		this.minDmg=minDmg;
	}
	
	public int getMaxDmg(){
		return maxDmg;
	}
	
	public void setMaxDmg(int maxDmg){
		this.maxDmg=maxDmg;
	}

	public int getAtk() {
		return atk;
	}

	public void setAtk(int atk) {
		this.atk = atk;
	}

	public int getDef() {
		return def;
	}

	public void setDef(int def) {
		this.def = def;
	}

	public float getAttSucc() {
		return attSucc;
	}

	public void setAttSucc(float attSucc) {
		this.attSucc = attSucc;
	}

	public float getCritSucc() {
		return critSucc;
	}

	public void setCritSucc(float critSucc) {
		this.critSucc = critSucc;
	}

	public float getDefSucc() {
		return defSucc;
	}

	public void setDefSucc(float defSucc) {
		this.defSucc = defSucc;
	}

	public short getCritDmg() {
		return critDmg;
	}

	public void setCritDmg(short critDmg) {
		this.critDmg = critDmg;
	}

	public short angstType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public float getTypeDmg() {
		return typeDmg;
	}

	public void setTypeDmg(float typeDmg) {
		this.typeDmg = typeDmg;
	}

	public short getEquipSlot() {
		return equipSlot;
	}

	public void setEquipSlot(short equipSlot) {
		this.equipSlot = equipSlot;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

}
