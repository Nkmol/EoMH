package item.vendor;

import item.ItemFrame;
import item.ItemVendor;
import item.ItemInInv;
import item.inventory.InventoryException;
import item.vendor.VendorException;
import Player.Character;
import World.WMap;
import timer.VendingPointTimer;

import java.util.HashMap;
import java.util.Map;
import java.awt.Point;
import java.util.Timer;

public class Vendor {
	private Map<Integer, ItemVendor> itemsMap = new HashMap<Integer, ItemVendor>();
	private Map<Integer, Point> itemCoords = new HashMap<Integer, Point>();
	private Character cur;
	private String shopname;
	private Timer vendingPoints;
	private int VendingPointsSpeed = 360000;
	private int moneyCap;
	private int totalMoney = 0;
	
	public Vendor(Character cur, String shopname) {
		this.cur = cur;
		this.shopname = shopname;
		this.moneyCap = cur.getInventory().getMaxCoins();
		vendingPoints = new Timer();
	}
	
	public Map<Integer, ItemVendor> getItems() {
		return itemsMap;
	}
	
	public Map<Integer, Point> getCoords() {
		return itemCoords;
	}
	
	public String getShopname() {
		return shopname;
	} 
	
	public void removeItemList(int id) {
		itemsMap.remove(id);
		itemCoords.remove(id);
	}
	
	public byte[] addToVendorList() throws VendorException {
		if(!WMap.getInstance().addVendorList(cur))
			throw new VendorException("Something went wrong adding this vendor");
		return VendorPackets.getVendorListPacket(cur);
	}
	
	public byte[] removeFromVendorList() throws VendorException {
		if(!WMap.getInstance().removeVendorList(cur))
			throw new VendorException("Something went wrong deleting this vendor");
		return VendorPackets.getVendorListPacket(cur);
	}
	
	public byte[] createVendor() {
		//time it takes to get a vending point
		vendingPoints.scheduleAtFixedRate(new VendingPointTimer(cur), VendingPointsSpeed, VendingPointsSpeed);
		return VendorPackets.createVendorFrame(cur, 1, shopname);
	}
	
	public byte[] deleteVendor() {
		//stops timer
		vendingPoints.cancel();
		return VendorPackets.createVendorFrame(cur, 0, shopname);
	}
	
	public byte[] addItem(ItemVendor item, int state, int x, int y) throws VendorException {
		int check = -1;
		check = cur.getInventory().getSeqSaved().get(item.getInvIndex());
		if(check < 0)
			throw new VendorException("Item does not exist in inventory");
		
		if(totalMoney + item.getPrice()*item.getAmount() > moneyCap - cur.getInventory().getCoins())
			throw new VendorException("You reached the selling value cap. The total of all items cannot be more if the sum is more as " + moneyCap + ".");
		
		totalMoney += item.getPrice();
		itemsMap.put(item.getId(), item);
		itemCoords.put(item.getId(), new Point(x, y));
		return VendorPackets.addItemToVendor(item, state, cur, x, y);
	}
	
	public byte[] removeItem(int index, int state, int x, int y) throws VendorException {
		ItemVendor item = itemsMap.get(index);
		
		if(item == null)
			throw new VendorException("Item does not exist!");
		
		itemsMap.remove(index);
		itemCoords.remove(index);
		return VendorPackets.addItemToVendor(item, state, cur, x, y);
	}
	
	public ItemVendor createItem(ItemFrame item, int invIndex, long price, int amount, int itemUid) throws VendorException {
		int check = -1;
		check = cur.getInventory().getSeqSaved().get(invIndex);
		if(check < 0)
			throw new VendorException("Item does not exist in inventory");
		return new ItemVendor(item, invIndex, price, amount, itemUid);
	}
	
	public byte[] soldItem(Character buy, long price, int index, int invSlot, int x, int y, int amount) throws VendorException {		
		//adjust gold
		if(!buy.getInventory().subtractCoins((int)price*amount))
			throw new VendorException("Not enough gold!");
		
		if(cur.getInventory().getCoins() > moneyCap)
			throw new VendorException("You cannot have more then " + moneyCap + " in your inventory.");
		
		cur.getInventory().addCoins((int)price*amount);
		ItemVendor item = itemsMap.get(index);
		if(item == null)
			throw new VendorException("Item does not exist!");

		if(amount > item.getAmount())
			throw new VendorException("This is not the right amount.");
		
		//when item not deleted
		if(item.getAmount() > 1 &&  amount != item.getAmount()) {
			item.decrementAmount(amount);
			//replace with new values in vendor
			itemsMap.put(index, item);
		}
		else {
			//remove item vendor
			removeItemList(index);
		}
			
		//remove/decrement item
		cur.getInventory().updateInv();
		try {
			cur.getInventory().removeItem(item.getInvIndex(), amount);
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			throw new VendorException(e.getMessage());
		}
		cur.getInventory().saveInv();
		
		//add item
		ItemInInv newItem = new ItemInInv(item.getItemFrame().getId(), item.getAmount());
		System.out.println("ItemID: " + newItem.getItem().getId());
		buy.getInventory().updateInv();
		if(!buy.getInventory().addItem(y, x, newItem)) 
			throw new VendorException("Something went wrong while adding the item");
		buy.getInventory().saveInv();
		
		return VendorPackets.buyItemFromVendor(buy, cur, index, invSlot, x, y, amount);
	}
	
	public byte[] open(Character open, int vendorId) throws VendorException {
		return VendorPackets.openVendorFrame(open, itemsMap, itemCoords, cur.getCharID(), vendorId);
	}
}
