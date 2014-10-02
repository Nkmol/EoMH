package item;

public class ItemInInv{

	private ItemFrame item;
	private int amount;
	
	public ItemInInv(int itemID){
		
		this(itemID,1);
		
	}
	
	public ItemInInv(int itemID, int amount){
		
		setItem((ItemFrame)(ItemCache.getInstance().getItem(itemID)));
		this.setAmount(amount);
		
	}

	public ItemFrame getItem() {
		return item;
	}

	public void setItem(ItemFrame item) {
		this.item = item;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}	
}