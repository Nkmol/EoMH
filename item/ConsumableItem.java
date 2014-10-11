package item;

import java.util.HashMap;
import java.util.Map;

import Player.CharacterPackets;
import Player.Character;

public class ConsumableItem extends ItemFrame {
	private short[] buffId;
	private short[] buffTime;
	private short[] buffValue;
	
	private short healhp;
	private short healmana;
	
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
		
	}

}
