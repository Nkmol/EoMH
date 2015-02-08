package item.cargo;

import item.ItemCargo;
import item.ItemInInv;
import item.ItemVendor;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import Player.Character;

public class Cargo {
	private Map<Integer, ItemCargo> itemsMap = new HashMap<Integer, ItemCargo>();
	private Map<Integer, Point> itemCoords = new HashMap<Integer, Point>();
	private Character cur;
	
	public Cargo(Character cur) {
		this.cur = cur;
	}
	
	public Map<Integer, ItemCargo> getItems() {
		return itemsMap;
	}
	
	public Map<Integer, Point> getCoords() {
		return itemCoords;
	}
	
	public byte[] Depost(int invIndex, int x, int y) {
		ItemInInv itemInInv = cur.getInventory().getInvSaved().get(cur.getInventory().getSeqSaved().get(invIndex));
		ItemCargo item = new ItemCargo(itemInInv.getItem(), 1, 1); //Item amount? item uid?
		
		itemsMap.put(item.getId(), item);
		itemCoords.put(item.getId(), new Point(x, y));
		
		System.out.println("returning depost cargo packet");
		
		return CargoPackets.CargoWithdrawDepost(cur, invIndex, x, y, 0);
	}
}
