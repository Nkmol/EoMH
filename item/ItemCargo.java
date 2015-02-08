package item;

public class ItemCargo {
	private ItemFrame item;
	private int id, amount;
	
	public ItemCargo(ItemFrame item, int amount, int id) {
		this.item = item;
		this.amount = amount;
		this.id = id;
	}
	
	public ItemFrame getItemFrame() {
		return item;
	}
	
	public void decrementAmount(int value) {
		amount -= value;
	}
	
	public int getId() {
		return id;
	}
	
	public int getAmount() {
		return amount;
	}
}
