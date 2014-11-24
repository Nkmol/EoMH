package GameServer;

public class ServerEvent {

	private final String eventName;
	private final float exp;
	private final float drop;
	private final float coin;
	private final float fame;
	private final float generalStarrate;
	private final int starrate;
	private final int superstarrate;
	private final int multihitmobrate;
	private final float mobhp;
	private final String description;
	
	public ServerEvent(String eventName, float exp, float drop, float coin,
			float fame, float generalStarrate, int starrate, int superstarrate, int multihitmobrate, float mobhp, String description){
		this.eventName=eventName;
		this.exp=exp;
		this.drop=drop;
		this.coin=coin;
		this.fame=fame;
		this.generalStarrate=generalStarrate;
		this.starrate=starrate;
		this.superstarrate=superstarrate;
		this.multihitmobrate=multihitmobrate;
		this.mobhp=mobhp;
		this.description=description;
	}

	public String getEventName() {
		return eventName;
	}

	public float getExp() {
		return exp;
	}

	public float getDrop() {
		return drop;
	}

	public float getCoin() {
		return coin;
	}

	public float getFame() {
		return fame;
	}

	public float getGeneralStarrate() {
		return generalStarrate;
	}

	public int getStarrate() {
		return starrate;
	}

	public int getSuperstarrate() {
		return superstarrate;
	}

	public int getMultihitmobrate() {
		return multihitmobrate;
	}

	public float getMobhp() {
		return mobhp;
	}

	public String getDescription() {
		return description;
	}
	
}
