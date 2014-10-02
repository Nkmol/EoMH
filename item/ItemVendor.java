package item;

public class ItemVendor {
	private ItemFrame item;
	private int id, invIndex, amount;
	private long price;
	
	public ItemVendor(ItemFrame item, int invIndex, long price, int amount, int id) {
		this.item = item;
		this.invIndex = invIndex;
		this.price = price;
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
	
	public int getInvIndex() {
		return invIndex;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public long getPrice() {
		return price;
	}
}
