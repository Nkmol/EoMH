package item;

import World.WMap;
import World.Waypoint;
/*
 * TODO: uhhh.. lots of stuff
 */
public class ItemFrame implements Item{
	private int id; // item id
	private int width, height;
	private int npcPrice;
	private long expirationTime;
	private int minLvl, MaxLvl;
	private int type;
	private int setHash;
	private int maxStack;
	private int category;
	
	public ItemFrame(int id) {
		this.id = id;
	}
	public ConsumableItem getConsumable(){
		return null;
	}
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isConsumeable() {
		return false;
	}


	public int getNpcPrice() {
		return npcPrice;
	}

	public void setNpcPrice(int npcPrice) {
		this.npcPrice = npcPrice;
	}

	public long getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(long expirationTime) {
		this.expirationTime = expirationTime;
	}

	public int getMinLvl() {
		return minLvl;
	}

	public void setMinLvl(int minLvl) {
		this.minLvl = minLvl;
	}

	public int getMaxLvl() {
		return MaxLvl;
	}

	public void setMaxLvl(int maxLvl) {
		MaxLvl = maxLvl;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public int getId() {
		return this.id;
	}

	public int getSetHash() {
		return this.setHash;
	}
	
	public void setSetHash(int setHash) {
		this.setHash=setHash;
	}

	public boolean isEquipable() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isConsumable() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isSetEquipment() {
		// TODO Auto-generated method stub
		return false;
	}
	public EquipableItem getEquipable() {
		return null;
	}

	public EquipableSetItem getSetItem() {
		// TODO Auto-generated method stub
		return null;
	}
	public DroppedItem dropItem(int map, Waypoint location, int amount) {
			int uid = WMap.getInstance().getFreeUID();
			DroppedItem tmp = new DroppedItem(this,map, uid, location, amount);
		return tmp;
	}

	public int getMaxStack() {
		return maxStack;
	}

	public void setMaxStack(int maxStack) {
		this.maxStack = maxStack;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}


}
