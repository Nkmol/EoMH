package item;

public class ConsumableItem extends ItemFrame {
	private short[] buffId;
	private short[] buffTime;
	private short[] buffValue;
	private short healhp;
	private short healmana;
	private int telemap;
	private float telex,teley;
	
	public ConsumableItem(int id) { 
		super(id);
		buffId = new short[2];
		buffTime = new short[2];
		buffValue = new short[2];
	}
	
	public ConsumableItem getConsumable(){
		return this;
	}

	public short getHealhp() {
		return healhp;
	}


	public void setHealhp(short healhp) {
		this.healhp = healhp;
	}

	public short getHealmana() {
		return healmana;
	}

	public void setHealmana(short healmana) {
		this.healmana = healmana;
	}
	

	public void setBuff(short[] buffId, short[] buffTime, short[] buffValue) {
		this.buffId = buffId;
		this.buffTime = buffTime;
		this.buffValue = buffValue;
	}
	
	public short[] getBuffId() {
		return buffId;
	}
	
	public short[] getBuffTime() {
		return buffTime;
	}
	
	public short[] getBuffValue() {
		return buffValue;
	}

	public int getTelemap() {
		return telemap;
	}

	public void setTelemap(int telemap) {
		this.telemap = telemap;
	}

	public float getTeley() {
		return teley;
	}

	public void setTeley(float teley) {
		this.teley = teley;
	}

	public float getTelex() {
		return telex;
	}

	public void setTelex(float telex) {
		this.telex = telex;
	}

}
