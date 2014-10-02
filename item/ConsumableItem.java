package item;

public class ConsumableItem extends ItemFrame {
	
	private short healhp;
	private short healmana;
	
	public ConsumableItem(int id) { 
		super(id);
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

}
