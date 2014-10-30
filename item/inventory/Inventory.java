package item.inventory;

import item.ItemCache;
import item.ItemFrame;
import item.EquipableItem;
import item.EquipableSetItem;
import item.ItemInInv;
import ServerCore.ServerFacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Database.CharacterDAO;
import GameServer.ServerPackets.ServerMessage;
import Player.Character;
import Player.CharacterPackets;

public class Inventory {
	private int pages;
	private Map<Integer, ItemInInv> inv = new HashMap<Integer, ItemInInv>();
	private LinkedList<Integer> seq;
	private int indexHold=-1;
	private int indexToSwap=-1;
	private ItemInInv holdingItem;
	private boolean equipping=false;
	private int coins=0;
	private int maxcoins=1000000000;
	private short maxVendingPoints = 225;
	private int vendingPoints=0;
	//saved lists
	private Map<Integer, ItemInInv> invSaved = new HashMap<Integer, ItemInInv>();
	private LinkedList<Integer> seqSaved;
	
	public Inventory(int pages){
		this.pages = pages;
		this.seq = new LinkedList<Integer>();
		this.seqSaved = new LinkedList<Integer>();
		this.fillSequences();
	}
	
	public int getCoins(){
		return coins;
	}
	
	public void setCoins(int coins){
		this.coins=coins;
	}
	
	public int addCoins(int coins){
		this.coins+=coins;
		if(this.coins>maxcoins){
			int dif=coins-maxcoins;
			this.coins=maxcoins;
			return dif;
		}
		return 0;
	}
	
	public LinkedList<Integer> getSeq()
	{
		return this.seq;
	}
	
	public boolean subtractCoins(int coins){
		this.coins-=coins;
		if(this.coins<0){
			this.coins=0;
			return false;
		}
		return true;
	}
	
	public int getMaxCoins(){
		return maxcoins;
	}
	
	public int getVendingPoints() {
		return vendingPoints;
	}
	
	public void setVendingPoints(int vendingPoints) {
		this.vendingPoints = vendingPoints;
	}
	
	public synchronized void VendingPointsTimer(Character cur) {
		try {
			addVendingPoints(1);
		} catch (InventoryException e) {
			if(!cur.isBot())
				new ServerMessage().execute(e.getMessage(), ServerFacade.getInstance().getConnectionByChannel(cur.GetChannel()));
		}
		updateVendingPoints(cur);
	}

	public void updateVendingPoints(Character cur) {
		byte[] vendingp = CharacterPackets.getFameVendingPacket(cur);
		cur.addWritePacketWithId(vendingp);
	}
	
	public void addVendingPoints(int vp) throws InventoryException{
		if(vendingPoints+vp>maxVendingPoints){
			throw new InventoryException("Cannot add vp [vp limit]");
		}
		//System.out.print(vendingPoints + " + " + vp + " = " + (vendingPoints+vp));
		vendingPoints+=vp;
	}
	
	//save seq and inv
	public void saveInv(){
			
		invSaved.clear();
		seqSaved.clear();
		invSaved.putAll(inv);
		seqSaved.addAll(seq);
			
	}
	
	//save seq and inv and put inv to database
	public void saveInv(Character cur){
		
		invSaved.clear();
		seqSaved.clear();
		invSaved.putAll(inv);
		seqSaved.addAll(seq);
		CharacterDAO.saveInventories(cur.getuid(), this);
		
	}
	
	//update seq and inv
	public void updateInv(){
		
		inv.clear();
		seq.clear();
		inv.putAll(invSaved);
		seq.addAll(seqSaved);
		
	}
	
	public int getIndexHold(){
		return indexHold;
	}
	
	public ItemInInv getHoldingItem(){
		return holdingItem;
	}
	
	public int getPages(){
		return pages;
	}
	
	public Map<Integer, ItemInInv> getInvSaved(){
		return invSaved;
	}
	
	public void setInvSaved(Map<Integer, ItemInInv> invSaved){
		this.invSaved=invSaved;
	}
	
	public LinkedList<Integer> getSeqSaved(){
		return seqSaved;
	}
	
	public void setSeqSaved(LinkedList<Integer> seqSaved){
		this.seqSaved=seqSaved;
	}
	
	//print complete inv
	public void printInv(){
		
		//slots
		for(int j=0;j<pages*5;j++){
			if(j%5==0)
				System.out.println();
			for(int i=0;i<8;i++){
				if(inv.containsKey((j*100)+i))
					System.out.print("X");
				else
					System.out.print("0");
			}
			System.out.println();
		}
		
		//list
		for(Iterator<Integer> i=seq.iterator();i.hasNext();)
			System.out.print("["+(i.next())+"]");
		
		System.out.println();
		
	}
	
	// make sure all sequences are present
	private void fillSequences(){
		for (int i = 0; i <= 240; i++){
			seq.add(new Integer(-1));
			seqSaved.add(new Integer(-1));
		}
	}
	// find first free sequence (aka. first that has value of 0)
	public int nextFreeSequence()
	{
		int c = this.seq.indexOf(-1);
		System.out.println("New sequence; " + c);
		return c;
	}
	// increase amount of pages in inventory
	public void addPages(int amount){
		if (amount > 0 || amount < 4) this.pages += amount;
	}
	
	//decrement item with given seq index and delete it when amount is 0
	public ItemInInv decrementItem(int seqIndex) throws InventoryException{
		
		if(seqIndex<0){
			throw new InventoryException("Cannot decrement item [illegal index]");
		}
		
		if(seq.get(seqIndex)==-1){
			throw new InventoryException("Cannot decrement item [missing item]");
		}
		
		ItemInInv item=inv.get(seq.get(seqIndex));
		
		if(item==null){
			throw new InventoryException("Cannot decrement item [missing item]");
		}
		
		if(item.getAmount()==0){
			throw new InventoryException("Cannot decrement item [item amount is 0]");
		}
		
		item.setAmount(item.getAmount()-1);
		
		if(item.getAmount()==0){
			removeItemFromInv(seq.get(seqIndex));
			seq.set(seqIndex, -1);
		}
		
		return item;
		
	}
	
	//equip an item
	public void equipItem(int fromInvID, int toEquipID, Equipments equip) throws InventoryException{
		
		if(equipping==true){
			fromInvID=seq.indexOf(8);
			if(fromInvID==-1){
				throw new InventoryException("Cannot equip item [item index missing]");
			}
		}
		
		ItemInInv itemF;
		//swap if holdingItem
		if(indexHold!=-1){
			int saveSwapHash=seq.get(indexToSwap);
			seq.set(indexToSwap, -1);
			seq.set(nextFreeSequence(), seq.get(indexHold));
			seq.set(indexHold, saveSwapHash);
			indexHold=-1;
			itemF=holdingItem;
		}else{
			//remove item from inv
			if(seq.get(fromInvID)==-1){
				throw new InventoryException("Cannot equip item [item missing]");
			}
			itemF=inv.get(seq.get(fromInvID));
			if(itemF==null){
				throw new InventoryException("Cannot equip item [item null(ghost)]");
			}
			removeItemFromInv(seq.get(fromInvID));
			seq.set(fromInvID, -1);
		}
		
		//check stats
		if(!(itemF.getItem() instanceof EquipableItem) && !(itemF.getItem() instanceof EquipableSetItem)){
			throw new InventoryException("Cannot equip item [not an equipable item]");
		}
		EquipableItem itemE=(EquipableItem)(itemF.getItem());
		if(!itemE.getUsableClass()[equip.getOwner().getCharacterClass()-1]){
			throw new InventoryException("Cannot equip item [not for your class]");
		}
		if(itemF.getItem().getMinLvl()>equip.getOwner().getLevel()){
			throw new InventoryException("Cannot equip item [lvl too low]");
		}
		if(itemF.getItem().getMaxLvl()<equip.getOwner().getLevel()){
			throw new InventoryException("Cannot equip item [lvl too high]");
		}
		short[] equipstats=((EquipableItem)(itemF.getItem())).getStatRequirements();
		short[] charstats=equip.getOwner().getStats();
		for(int i=0;i<5;i++){
			if(equipstats[i]>charstats[i]){
				throw new InventoryException("Cannot equip item [stats too low]");
			}
		}
		
		//equip
		if(equip.getEquipments().containsKey(toEquipID)){
			//8 is imaginary slot
			seq.set(nextFreeSequence(), 8);
			putIntoInv(8, 0, equip.getEquipments().get(toEquipID));
			equip.getEquipments().put(toEquipID, itemF);
			equipping=true;
		}else{
			equip.getEquipments().put(toEquipID, itemF);
		}
		
	}
	
	//unequip an item
	public void unequipItem(int fromEquipID, int line, int row, Equipments equip) throws InventoryException{
		
		//REMOVE FROM EQUIP
		
		if(!equip.getEquipments().containsKey(fromEquipID)){
			throw new InventoryException("Cannot unequip item [item missing]");
		}
		ItemInInv itemF=equip.getEquipments().get(fromEquipID);
		equip.getEquipments().remove(fromEquipID);
		
		//ADD TO INVENTORY
		
		//get the hashes from all blocking items
		List<Integer> hash = this.checkBlockingItems(line, row, itemF);
				
		//exception in checkBlockingItems
		if (hash == null){
			throw new InventoryException("Cannot unequip item [crosses inventory border]");
		}
				
		//move to an empty slot
		if (hash.size() == 0 ){
			seq.set(nextFreeSequence(), (row*100)+line);
			putIntoInv(line, row, itemF);
			return;
		}
		
		//swap
		if (hash.size() == 1 ){
			indexHold=seq.indexOf(hash.get(0));
			indexToSwap=nextFreeSequence();
			if(indexToSwap==-1){
				throw new InventoryException("Cannot unequip item [no free space in inv]");
			}
			holdingItem=inv.get(hash.get(0));
			removeItemFromInv(hash.get(0));
			putIntoInv(line, row, itemF);
			seq.set(indexToSwap, (row*100)+line);
			seq.set(indexHold, -1);
			return;
		}
		
		throw new InventoryException("Cannot unequip item [too many items blocking]");
		
	}
	
	//drop item from inv to ground
	public int dropItem(int fromInvID, int amount, Character cur, boolean isCoin) throws InventoryException{
		
		int uid;
		
		//amount must be >0
		if(amount==0){
			throw new InventoryException("Cannot drop item [amount is 0]");
		}
		
		//amount must be <=10000
		if(amount>10000 && !isCoin){
			throw new InventoryException("Cannot drop item [amount is >=10000]");
		}
		
		//if it was an equipped item
		if(equipping==true){
			fromInvID=seq.indexOf(8);
			if(fromInvID==-1){
				throw new InventoryException("Cannot equip item [item index missing]");
			}
		}
		
		//SWAPPED BEFORE
		if(indexHold!=-1){
			
			//swap indexes first
			int saveSwapHash=seq.get(indexToSwap);
			seq.set(indexToSwap, -1);
			seq.set(nextFreeSequence(), seq.get(indexHold));
			seq.set(indexHold, saveSwapHash);
			
			//create drop
			ItemFrame it = (ItemFrame)ItemCache.getInstance().getItem(holdingItem.getItem().getId());
			uid=it.dropItem(cur.getCurrentMap(), cur.getLocation(), amount).getuid();
			
			indexHold=-1;
		}else{
		//NOT SWAPPED BEFORE
			
			ItemFrame it;
			//coin is not an item in inv
			if(isCoin==false){
				if(seq.get(fromInvID)==-1){
					throw new InventoryException("Cannot drop item [item missing]");
				}
				ItemInInv itemF=inv.get(seq.get(fromInvID));
				if(itemF==null){
					throw new InventoryException("Cannot drop item [item null(ghost)]");
				}
				//wrong amount
				if(amount>itemF.getAmount()){
					throw new InventoryException("Cannot drop item [item amount not sync]");
				}
				
				//create drop
				it = (ItemFrame)ItemCache.getInstance().getItem(inv.get(seq.get(fromInvID)).getItem().getId());
				
				//reduce amount or remove item
				itemF.setAmount(itemF.getAmount()-amount);
				if(itemF.getAmount()==0){
					removeItemFromInv(seq.get(fromInvID));
					seq.set(fromInvID,-1);
				}
				
				//if it was an equipped item
				equipping=false;
				
			}else{
				it = (ItemFrame)ItemCache.getInstance().getItem(217000501);
				//subtract money and check if possible
				if(subtractCoins(amount)==false){
					throw new InventoryException("Cannot drop item [not enough coins]");
				}
			}
			
			uid=it.dropItem(cur.getCurrentMap(), cur.getLocation(), amount).getuid();
		}
		return uid;
		
	}
	
	//move item in inventory
	public void moveItem(int fromInvID, int toInvID, int amount, int line, int row) throws InventoryException{
		
		//equipping uses toInvID as fromInvID what the hell and mysterious things when swapped
		if(equipping==true){
			int seq8=seq.indexOf(8);
			if(seq8==-1)
				fromInvID=toInvID;
			else
				fromInvID=seq8;
		}
		
		//index must exist
		if(indexHold==-1 && fromInvID>=0 && seq.get(fromInvID)==-1){
			throw new InventoryException("Cannot move item [item missing]");
		}
			
		//get item1
		ItemInInv itemF;
		if(indexHold==-1)
			itemF=inv.get(seq.get(fromInvID));
		else
			itemF=holdingItem;
		
		//there must be item1
		if(itemF==null){
			throw new InventoryException("Cannot move item [item null(ghost)]");
		}
		
		//wrong amount
		if(amount>itemF.getAmount() || amount==0){
			throw new InventoryException("Cannot move item [item amount not sync]");
		}
		if(amount>10000){
			throw new InventoryException("Cannot move item [amount is >10000]");
		}
			
		//get the hashes from all blocking items
		List<Integer> hash = this.checkBlockingItems(line, row, itemF);
		
		//exception in checkBlockingItems
		if (hash == null){
			throw new InventoryException("Cannot move item [crosses inventory border]");
		}
		
		//move to an empty slot
		if (hash.size() == 0 ){
			//SWAPPED BEFORE
			if(indexHold!=-1){
				putIntoInv(line, row, holdingItem);
				seq.set(indexHold, seq.get(indexToSwap));
				seq.set(indexToSwap, -1);
				seq.set(nextFreeSequence(), (row*100)+line);
				indexHold=-1;
			//NOT SWAPPED BEFORE
			}else{
				itemF.setAmount(itemF.getAmount()-amount);
				if(itemF.getAmount()==0){
					removeItemFromInv(seq.get(fromInvID));
					seq.set(fromInvID, (row*100)+line);
				}else{
					int nfs=nextFreeSequence();
					if(nfs!=-1){
						seq.set(nextFreeSequence(), (row*100)+line);
					}else{
						throw new InventoryException("Cannot move item [no free space in inv]");
					}
				}
					
				ItemInInv newItemF=new ItemInInv(itemF.getItem().getId());
				newItemF.setAmount(amount);
				putIntoInv(line,row,newItemF);
				
				equipping=false;
			}
			return;	
		}
		
		if (hash.size() == 1 ){
			if(seq.indexOf(hash.get(0))!=-1){
				//SWAPPED BEFORE
				if(indexHold!=-1){
						
					//swap indexes first
					int saveSwapHash=seq.get(indexToSwap);
					seq.set(indexToSwap, -1);
					seq.set(nextFreeSequence(), seq.get(indexHold));
					seq.set(indexHold, saveSwapHash);
					indexHold=indexToSwap;
						
					holdingItem=inv.get(hash.get(0));
					if(holdingItem.getItem().getId()==itemF.getItem().getId() && holdingItem.getAmount()+amount<=holdingItem.getItem().getMaxStack()){
						holdingItem.setAmount(holdingItem.getAmount()+amount);
						indexHold=-1;
					}else{
						//do not allow bigger stacks than maxstack
						if(holdingItem.getItem().getId()==itemF.getItem().getId() && holdingItem.getAmount()+amount>holdingItem.getItem().getMaxStack()){
							throw new InventoryException("Cannot move item [stack too big]");
						}
						if(removeItemFromInv(hash.get(0))==false){
							throw new InventoryException("Cannot move item [swapped item error]");
						}
						putIntoInv(line, row, itemF);
						int saveIndexHold=indexHold;
						indexToSwap=indexHold;
						indexHold=seq.indexOf(hash.get(0));
						if(indexHold==-1){
							throw new InventoryException("Cannot move item [item missing]");
						}
						seq.set(saveIndexHold, (row*100)+line);
						seq.set(indexHold, -1);
					}
				//NOT SWAPPED BEFORE
				}else{
					ItemInInv itemTo=inv.get(hash.get(0));
					if(itemTo.getItem().getId()==itemF.getItem().getId() && itemTo.getAmount()+amount<=itemTo.getItem().getMaxStack()){
						itemF.setAmount(itemF.getAmount()-amount);
						if(itemF.getAmount()==0){
							removeItemFromInv(seq.get(fromInvID));
							seq.set(fromInvID, -1);
						}
						itemTo.setAmount(itemTo.getAmount()+amount);
					}else{
						//do not allow bigger stacks than maxstack
						if(itemTo.getItem().getId()==itemF.getItem().getId() && itemTo.getAmount()+amount>itemTo.getItem().getMaxStack()){
							throw new InventoryException("Cannot move item [stack too big]");
						}
						indexHold=seq.indexOf(hash.get(0));
						indexToSwap=fromInvID;
						holdingItem=itemTo;
						removeItemFromInv(hash.get(0));
						removeItemFromInv(seq.get(fromInvID));
						putIntoInv(line, row, itemF);
						seq.set(indexToSwap, (row*100)+line);
						seq.set(indexHold, -1);
						
						equipping=false;
					}
				}
				return;
			}
		}
		throw new InventoryException("Cannot move item [too many items blocking]");
		
	}
	
	//try to put item into inv
	public int pickItem(ItemFrame it, int amount) throws InventoryException{
		
		ItemInInv newItem= new ItemInInv(it.getId(),amount);
		
		if(it.getId()==0){
			throw new InventoryException("Cannot pick item [item has wrong id]");
		}
		
		//coin
		if (it.getId()==217000501){
			//add money
			int dif=addCoins(newItem.getAmount());
			if(dif!=0){
				//throw new InventoryException("Cannot pick item [coin limit]");
				return dif;
			}
			return 0;
		}
		
		int line=0;
		int row=0;
		int i=0;
		boolean noMoreSeq=false;
		
		//move through seq to search stack
		while(noMoreSeq==false && i<240){
			if(seq.get(i)!=-1){
				ItemInInv item=inv.get(seq.get(i));
				if(item==null){
					throw new InventoryException("Cannot pick item [item in inv missing]");
				}
				if(item.getItem().getId()==it.getId() && item.getAmount()+newItem.getAmount()<=item.getItem().getMaxStack()){
					//stack
					item.setAmount(item.getAmount()+newItem.getAmount());
					return 0;
				}
			}else{noMoreSeq=true;}
			i++;
		}
		
		//move through all lines and rows until free slot is found
		while(row<pages*5){
			if(line==8)
				line=0;
			while(line<8){
				if(addItem(line,row,newItem))
					return 0;
				line++;
			}
			row++;
		}
		//no slot is found
		throw new InventoryException("Cannot pick item [no free space in inv]");
		
	}
	
	// adds item to inventory only if all needed slots are empty, return true is success, false otherwise
	public boolean addItem(int line, int row, ItemInInv it){
		
		//get the hashes from all blocking items
		List<Integer> hash = this.checkBlockingItems(line, row, it);
		
		//exception in checkBlockingItems
		if (hash == null){
			System.out.println("Error occured in checkingBlockingItems");
			return false;
		}
		
		//move to an empty slot
		if (hash.size() == 0 ){
			if(put(line, row, it)==false){
				System.out.println("Cannot put item into given slot");
				return false;
			}
			return true;	
		}
		
		//stack
		if (hash.size() == 1){
			if(inv.get(hash.get(0)).getItem().getId()==it.getItem().getId() && inv.get(hash.get(0)).getAmount()+it.getAmount()<=it.getItem().getMaxStack()){
				inv.get(hash.get(0)).setAmount(inv.get(hash.get(0)).getAmount()+it.getAmount());
				return true;
			}
		}
		
		//items block
		//System.out.println("Items blocking ("+hash.size()+")");
		return false;
		
	}
	
	//remove only in inv list
	private boolean removeItemFromInv(int hash){
		
		//cannot handle empty hash
		if (hash==-1){
			System.out.println("Cannot remove item with hash=-1");
			return false;
		}
		
		//get item
		ItemInInv itemF =inv.get(hash);
		
		//item must be valid
		if(itemF==null){
			System.out.println("Item with given hash is null");
			return false;
		}
		
		int width=itemF.getItem().getWidth();
		int height=itemF.getItem().getHeight();
		int line=hash%100;
		int row=(hash-line)/100;
			
		//remove all items from inv with given hash
		for(int j=0;j<height;j++){
			for(int i=0;i<width;i++){
				if(inv.containsKey(((row+j)*100+line+i))){
					inv.remove((row+j)*100+line+i);
				}
				else{
					System.out.println("Item key in inv missing");
					return false;
				}
			}
		}
			
		return true;
		
	}
		
	//remove item with given id
	public void removeItem(int invID) throws InventoryException{
		
		//if it was an equipped item
		if(equipping==true){
			invID=seq.indexOf(8);
			if(invID==-1){
				throw new InventoryException("Cannot remove item [item index missing]");
			}
		}
		
		if(indexHold!=-1){
			//swap indexes first
			int saveSwapHash=seq.get(indexToSwap);
			seq.set(indexToSwap, seq.get(indexHold));
			seq.set(indexHold, saveSwapHash);
			
			indexHold=-1;
		}else{
			int hash=seq.get(invID);
			//try to remove item from inv
			if(removeItemFromInv(hash)==false){
				throw new InventoryException("Cannot remove item [item index missing]");
			}
			//reset index slot
			seq.set(invID,-1);
		}
		
		//if it was an equipped item
		equipping=false;
		
	}
	
	//remove from stack
	public void removeItem(int invID, int amount) throws InventoryException{
		
		int hash=seq.get(invID);
		if(hash==-1){
			throw new InventoryException("Cannot remove item [item index missing]");
		}
		ItemInInv item=inv.get(hash);
		if(item==null){
			throw new InventoryException("Cannot remove item [item missing]");
		}
		if(amount<item.getAmount()){
			item.setAmount(item.getAmount()-amount);
		}else{
			removeItem(invID);
		}
		
	}
	
	//put into seq
	private boolean putIntoSeq(int line, int row){
		
		//try to find next free sequence
		int nextSeq=nextFreeSequence();
		//free sequence not found
		if (nextSeq==-1){
			System.out.println("No empty slot in seq found");
			return false;
		}
		//sequence found and adding hash
		seq.set(nextSeq, (row*100) + line);
		return true;
		
	}
	
	//put into inv
	public void putIntoInv(int line, int row, ItemInInv it){
		
		for (int i = line;  i < (line + it.getItem().getWidth()); i++){
			for (int u=row; u < (row + it.getItem().getHeight()); u++){
				this.inv.put(Integer.valueOf((u*100)+i), it);
			}
		}
		
	}
	
	// insert item to all the slots it requires, no boundary checks are performed
	private boolean put(int line, int row, ItemInInv it) {
		
		//try to put item into sequence
		if(putIntoSeq(line, row)==false){
			System.out.println("Cannot put item into line "+line+" and row "+row);
			return false;
		}
		putIntoInv(line, row, it);
		return true;
		
	}
	
	/* check if item will fit in inventory
	 *  Returns list containing all items that are using same slots as tmp
	 */
	public List<Integer> checkBlockingItems(int line, int row, ItemInInv tmp){
		
		int width = tmp.getItem().getWidth();
		List<ItemInInv> ls = new ArrayList<ItemInInv>();
		int height = tmp.getItem().getHeight();
		ItemInInv it = null;
		List<Integer> hash= new ArrayList<Integer>();
		 
		// boundary check
		if (!this.checkWlimit(line, width)) return null;
		if (!this.checkHlimit(row, height)) return null;
		
		// main loop
		for (int i=line; i < (line + width); i++){
			for (int u=row; u < (row + height); u++){
				// if item is in slot add it to list
				if (this.inv.containsKey((u*100)+i)){
					it = this.inv.get((u*100) +i);
					if (!ls.contains(it) && it!=tmp){
						//move to top left corner spot
						int ii=i;
						int uu=u;
						boolean found=false;
						do{
							ii--;
						}while(found==false && ii>=0 && inv.get((uu*100)+ii)==it);
						ii++;
						do{
							uu--;
						}while(found==false && uu>=0 && inv.get((uu*100)+ii)==it);
						uu++;
						ls.add(it);
						hash.add((uu*100)+ii);
					}
				}
			}
			
		}
		return hash;
		
	}
	
	// check if item will fit to inventory by height
	private boolean checkHlimit(int row, int height) {
		
		if ((this.pages*5) >= (row + height)){
			int page = (int)Math.floor(row / 5);
			if ((row+height) <= ((page+1)*5) && row >= (page*5)){
				return true;
			}
		}
		return false;
		
	}
	
	// check if item will fit to inventory by width
	private boolean checkWlimit(int line, int width) {
		
		if (line + width <= 8) return true;
		return false;
		
	}
	
}
