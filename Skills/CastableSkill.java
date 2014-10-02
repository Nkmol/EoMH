package Skills;

public class CastableSkill extends SkillFrame{
	
	private int dmg;
	private float speed;
	private int needsWepToCast;
	private int ultiSetId;
	private int healCost;
	private int manaCost;
	private int staminaCost;
	private int targets;
	
	public CastableSkill(int id){
		super(id);
	}

	public int getDmg() {
		return dmg;
	}

	public void setDmg(int dmg) {
		this.dmg = dmg;
	}

	public int getNeedsWepToCast() {
		return needsWepToCast;
	}

	public void setNeedsWepToCast(int needsWepToCast) {
		this.needsWepToCast = needsWepToCast;
	}

	public int getUltiSetId() {
		return ultiSetId;
	}

	public void setUltiSetId(int ultiSetId) {
		this.ultiSetId = ultiSetId;
	}

	public int getHealCost() {
		return healCost;
	}

	public void setHealCost(int healCost) {
		this.healCost = healCost;
	}

	public int getManaCost() {
		return manaCost;
	}

	public void setManaCost(int manaCost) {
		this.manaCost = manaCost;
	}

	public int getStaminaCost() {
		return staminaCost;
	}

	public void setStaminaCost(int staminaCost) {
		this.staminaCost = staminaCost;
	}

	public int getTargets() {
		return targets;
	}

	public void setTargets(int targets) {
		this.targets = targets;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
}
