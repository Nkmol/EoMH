package item.upgrades;

public class Upgrade {

	private final int id;
	private final int oldit;
	private final int upgrader;
	private final int newit;
	private final float itstage;
	private final float upgradelvl;
	private final float failrate;
	private final float breakoption;
	private final int upgradeskill;
	
	public Upgrade(int id, int oldIt, int upgrader, int newIt,
			float itstage, float upgradelvl, float failrate, float breakoption, int upgradeskill){
		this.id=id;
		this.oldit=oldIt;
		this.upgrader=upgrader;
		this.newit=newIt;
		this.itstage=itstage;
		this.upgradelvl=upgradelvl;
		this.failrate=failrate;
		this.breakoption=breakoption;
		this.upgradeskill=upgradeskill;
		
	}

	public int getId() {
		return id;
	}

	public int getOldit() {
		return oldit;
	}

	public int getUpgrader() {
		return upgrader;
	}

	public int getNewit() {
		return newit;
	}

	public float getItstage() {
		return itstage;
	}

	public float getUpgradelvl() {
		return upgradelvl;
	}

	public float getFailrate() {
		return failrate;
	}

	public float getBreakoption() {
		return breakoption;
	}

	public int getUpgradeskill() {
		return upgradeskill;
	}
	
}
