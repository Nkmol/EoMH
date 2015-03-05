package item.cargo;

import item.ItemCargo;
import item.ItemInInv;
import item.inventory.InventoryException;

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
	
	public byte[] Depost(int invIndex, int x, int y, int cargoIndex, int state) throws CargoException {
		ItemCargo item = null;
		if(state == 1) {
			ItemInInv itemInInv = cur.getInventory().getInvSaved().get(cur.getInventory().getSeqSaved().get(invIndex));
			item = new ItemCargo(itemInInv.getItem(), 1, 1); 

			cur.getInventory().updateInv();
			//remove deposted item from inventory
			try {
				cur.getInventory().removeItem(invIndex);
			} catch (InventoryException e) {
				throw new CargoException(e.getMessage());
			}
			cur.getInventory().saveInv();
			
			//When item index already exists = swapping from inv to cargo
			ItemCargo itemSwap = itemsMap.get(cargoIndex);
			if(itemSwap != null) {
				ItemInInv holdingItem = cargoToInv(itemSwap);
				try {
					cur.getInventory().swapToOutside(holdingItem);
				} catch (InventoryException e) {
					System.out.print(e.getMessage());
				}
			}
		}
		else {
			if(cur.getInventory().getHoldingItem() == null) {
				item = itemsMap.get(cargoIndex); 
				if(cur.getInventory().getItem(invIndex) != null) {
					cur.getInventory().updateInv();
					//remove swapped before item
					try {
						cur.getInventory().removeItem(invIndex);
					} catch (InventoryException e) {
						throw new CargoException(e.getMessage());
					}
					cur.getInventory().saveInv();
				}
			}
			//If prev has been swapped
			else {
				ItemInInv itemInv = cur.getInventory().getHoldingItem();
				item = new ItemCargo(itemInv.getItem(), itemInv.getAmount() ,itemInv.getItem().getId());
				
				//reset holding item values
				cur.getInventory().removeHoldItem();
			}
		}
		
		if(item == null)
			throw new CargoException("[item missing]Item not found");
				
		itemsMap.put(cargoIndex, item);
		itemCoords.put(cargoIndex, new Point(x, y));
		
		cur.getInventory().printInv();
		
		return CargoPackets.CargoDepost(cur, invIndex, x, y, cargoIndex, state);
	}
	
	public byte[] Withdraw(int cargoIndex, int x, int y)  throws CargoException {
		ItemInInv invIt = cargoToInv(itemsMap.get(cargoIndex));
		int invIndex = -1;

		if(invIt == null)
			throw new CargoException("[item missing]Item not found in cargo");
		
		//swap
		try {
			invIndex = cur.getInventory().swapFromOutside(x, y, invIt);
		} catch (InventoryException e) {
			System.out.print(e.getMessage());
		}
		 
		if(invIndex == -1) {
			cur.getInventory().updateInv();
			if(!cur.getInventory().addItem(x, y, invIt)) 
				throw new CargoException("[Wrong place]Failed to add item");
			cur.getInventory().saveInv();
			
			invIndex = cur.getInventory().getIndex(x, y);
		}
		
		
		itemsMap.remove(cargoIndex);
		itemCoords.remove(cargoIndex);
		
		if(invIndex < 0)
				throw new CargoException("[Wrong index]Item inventory index not found");
		
		cur.getInventory().printInv();
		return CargoPackets.CargoWithdraw(cur, cargoIndex, x, y, invIndex);
	}
	
	public byte[] Swap(int from, int to, int y, int x) throws CargoException {
		if(itemsMap.get(from) == null)
			throw new CargoException("[item missing]Item you're swapping not found " + from);
		
		ItemCargo item = itemsMap.get(to);
		if(item == null)
			throw new CargoException("[item missing]Item you want to swap not found");
		
		itemCoords.put(from, new Point(x, y));
		//Item is still in cargo instance but not placed
		itemCoords.put(to, new Point(-1, -1));
		cur.getInventory().setHoldingItem(cargoToInv(item));
		return CargoPackets.CargoSwap(cur, from, to, y, x);
	}
	
	public byte[] Move(int preFromX, int cargoIndex, int y, int x) throws CargoException  {
		
		ItemCargo item = itemsMap.get(cargoIndex);
		if(item == null)
			throw new CargoException("[item missing]Item you want to swap to not found");
		
		itemCoords.put(cargoIndex, new Point(x, y));
		return CargoPackets.CargoMove(cur, cargoIndex, preFromX, y, x);
	}
	
	private ItemInInv cargoToInv(ItemCargo itemCargo) {
		ItemInInv invIt = new ItemInInv(itemCargo.getItemFrame().getId());
		invIt.setAmount(itemCargo.getAmount());
		
		return invIt;
	}
}
