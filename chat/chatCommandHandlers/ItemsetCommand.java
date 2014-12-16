package chat.chatCommandHandlers;


import item.DroppedItem;
import item.ItemCache;
import item.ItemFrame;
import item.ItemInInv;
import item.ItemWithAmount;

import java.util.LinkedList;
import java.util.Map;

import Player.Character;
import GameServer.ServerPackets.ServerMessage;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Tools.StringTools;
import Connections.Connection;
import Database.InstallDAO;
import Database.ItemDAO;
import chat.ChatCommandExecutor;

public class ItemsetCommand implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public ItemsetCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	public void execute(String[] parameters, Connection source) {
		System.out.println("Received chat command for itemsets!");
		
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		InstallDAO installDao=InstallDAO.getInstance();
		ItemDAO itemDao=ItemDAO.getInstance();
		
		//----------ADD OR UPDATE AN ITEMSET----------
		if(parameters.length>3 && parameters[0].equals("add")){
			String name=parameters[1];
			String password=parameters[2];
			if(name.length()>16 || password.length()>16){
				new ServerMessage().execute("Name/Password must be <=16 letters", source);
				return;
			}
			//old pw from db
			String oldPassword=itemDao.getItemsetPassword(name);
			if(oldPassword!=null && !oldPassword.equals(password)){
				new ServerMessage().execute("Name already exists with different password", source);
				return;
			}
			int invpages=0;
			int equipindex=-1;
			int i=3;
			boolean stop=false;
			//search for valid indexes or equip
			while(stop==false && i<parameters.length){
				if (StringTools.isInteger(parameters[i])){
					invpages++;
					i++;
				}else{
					if(parameters[i].equals("e")){
						equipindex=i;
						i++;
					}else{
						stop=true;
					}
				}
			}
			if(invpages==0 && equipindex==-1){
				new ServerMessage().execute("Wrong invpage", source);
				return;
			}
			int page;
			Map<Integer,ItemInInv> inv;
			LinkedList<Integer> itemIds=new LinkedList<Integer>();
			LinkedList<Integer> itemAmounts=new LinkedList<Integer>();
			LinkedList<ItemInInv> items;
			//EQUIPMENT
			if(equipindex!=-1){
				Map<Integer, ItemInInv> eq=cur.getEquips().getEquipmentsSaved();
				for(int j=0;j<17;j++){
					if(eq.containsKey(j)){
						itemIds.add(eq.get(j).getItem().getId());
						itemAmounts.add(eq.get(j).getAmount());
					}
				}
			}
			//INVENTORY
			inv=cur.getInventory().getInvSaved();
			//total amount of valid parameters
			int searchinvs=invpages;
			if(equipindex!=-1)
				searchinvs++;
			for(int j=0;j<searchinvs;j++){
				//only inventory
				if(j!=equipindex-3){
					page=Integer.parseInt(parameters[3+j])-1;
					items=new LinkedList<ItemInInv>();
					for(int k=0;k<5;k++){
						for(int m=0;m<8;m++){
							if(inv.containsKey(page*500+k*100+m) && !items.contains(inv.get(page*500+k*100+m))){
								itemIds.add(inv.get(page*500+k*100+m).getItem().getId());
								itemAmounts.add(inv.get(page*500+k*100+m).getAmount());
								items.add(inv.get(page*500+k*100+m));
							}
						}
					}
				}
			}
			if(oldPassword==null){
				installDao.addItemset(itemDao.getSqlConnection(), name, password, itemIds, itemAmounts);
				new ServerMessage().execute("Added itemset "+name+" with password "+password, source);
			}else{
				itemDao.updateItemset(name, itemIds, itemAmounts);
				new ServerMessage().execute("Updated itemset "+name, source);
			}
			if(itemIds.size()>60){
				new ServerMessage().execute("WARNING: Some items are ignored [max 60 items]", source);
			}
		}
		
		//----------DELETE AN ITEMSET----------
		if(parameters.length>2 && parameters[0].equals("del")){
			String name=parameters[1];
			String password=parameters[2];
			//old pw from db
			String oldPassword=itemDao.getItemsetPassword(name);
			if(oldPassword!=null && !oldPassword.equals(password)){
				new ServerMessage().execute("Itemset does not exist or wrong pw", source);
				return;
			}
			itemDao.deleteItemset(name);
			new ServerMessage().execute("Deleted itemset "+name, source);
		}
		
		//----------UPDATE PASSWORD----------
		if(parameters.length>2 && parameters[0].equals("newpw")){
			String name=parameters[1];
			String password=parameters[2];
			if(itemDao.getItemsetPassword(name)==null){
				new ServerMessage().execute("Itemset does not exist", source);
				return;
			}
			itemDao.changeItemsetPassword(name, password);
			new ServerMessage().execute("New password for itemset "+name+" is "+password, source);
		}
		
		//----------SPAWN ITEMSET----------
		if(parameters.length>1 && parameters[0].equals("spawn")){
			String name=parameters[1];
			LinkedList<ItemWithAmount> items=itemDao.getItemsets(name);
			ItemWithAmount item;
			if(items==null || items.isEmpty()){
				new ServerMessage().execute("The itemset is empty or does not exist", source);
				return;
			}
			while(!items.isEmpty()){
				item=items.removeFirst();
				ItemFrame it = (ItemFrame)ItemCache.getInstance().getItem(item.getId());
				DroppedItem dit = it.dropItem(cur.getCurrentMap(), cur.getLocation(),item.getAmount());
				source.addWrite(dit.itemSpawnPacket());
			}
			new ServerMessage().execute("Spawned the itemset "+name, source);
		}
		
	}
	
}
