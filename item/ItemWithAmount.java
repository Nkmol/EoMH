package item;

public class ItemWithAmount {

	private int id;
	private int amount;
	public ItemWithAmount(int id, int amount){
		this.setId(id);
		this.setAmount(amount);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
}
