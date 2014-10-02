package item;

public interface Item {
	public int getId();
	public int getSetHash();
	public int getCategory();
	public int getWidth();
	public int getHeight();
	public boolean isEquipable();
	public boolean isConsumable();
	public boolean isSetEquipment();
	public EquipableItem getEquipable();
	public EquipableSetItem getSetItem();
	public ConsumableItem getConsumable();
}
