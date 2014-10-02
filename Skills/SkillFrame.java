package Skills;

public class SkillFrame{

	private int id;
	private int chclass;
	private int reqSkill1;
	private int reqSkill2;
	private int reqSkill3;
	private int faction;
	private int lvl;
	private int skillpoints;
	private int typeGeneral;
	private int typeSpecific;
	private int stage;
	
	public SkillFrame(int id){
		this.id=id;
	}
	
	public int getId(){
		return id;
	}

	public int getChclass() {
		return chclass;
	}

	public void setChclass(int chclass) {
		this.chclass = chclass;
	}

	public int getReqSkill1() {
		return reqSkill1;
	}

	public void setReqSkill1(int reqSkill1) {
		this.reqSkill1 = reqSkill1;
	}

	public int getReqSkill2() {
		return reqSkill2;
	}

	public void setReqSkill2(int reqSkill2) {
		this.reqSkill2 = reqSkill2;
	}

	public int getReqSkill3() {
		return reqSkill3;
	}

	public void setReqSkill3(int reqSkill3) {
		this.reqSkill3 = reqSkill3;
	}

	public int getFaction() {
		return faction;
	}

	public void setFaction(int faction) {
		this.faction = faction;
	}
	
	public int getLvl() {
		return lvl;
	}

	public void setLvl(int lvl) {
		this.lvl = lvl;
	}
	
	public int getSkillpoints() {
		return skillpoints;
	}

	public void setSkillpoints(int skillpoints) {
		this.skillpoints = skillpoints;
	}

	public int getTypeSpecific() {
		return typeSpecific;
	}

	public void setTypeSpecific(int typeSpecific) {
		this.typeSpecific = typeSpecific;
	}

	public int getTypeGeneral() {
		return typeGeneral;
	}

	public void setTypeGeneral(int typeGeneral) {
		this.typeGeneral = typeGeneral;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}
	
}
