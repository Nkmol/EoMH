package chat.chatCommandHandlers;

import java.util.Iterator;
import java.util.LinkedList;

import item.DroppedItem;
import item.ItemCache;
import item.ItemFrame;
import Database.ItemDAO;
import ExperimentalStuff.CommandException;
import ExperimentalStuff.IntelligentCommands;
import GameServer.ServerPackets.ServerMessage;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Player.Character;
import Tools.StringTools;
import Connections.Connection;
import chat.ChatCommandExecutor;

public class ItemListCommand implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public ItemListCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}

	public void execute(String[] parameters, Connection source) {
		System.out.println("Received chat command to spawn a filtered itemlist!");
		
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		int amount;
		if(parameters.length>1 && StringTools.isInteger(parameters[1])){
			
			amount=Integer.parseInt(parameters[1]);
			if(amount>100)
				amount=100;
			
		}else{amount=1;}
		
		if(parameters.length>0){
			
			try{
				LinkedList<Integer> itemIds=IntelligentCommands.getIdsByCommand(ItemDAO.getInstance().getSqlConnection(), "item", parameters[0]);
				int counter=0;
				if((parameters.length>2 && parameters[2].equals("random")) || (parameters.length>1 && parameters[1].equals("random"))){
					while(counter<amount && !itemIds.isEmpty()){
						int itemId=itemIds.remove((int)(Math.random()*itemIds.size()));
						ItemFrame item;
						DroppedItem ditem;
						item = (ItemFrame)ItemCache.getInstance().getItem(itemId);
						if(item!=null){
							ditem = item.dropItem(cur.getCurrentMap(), cur.getLocation(),1);
							source.addWrite(ditem.itemSpawnPacket());
						}
						counter++;
					}
				}else{
					Iterator<Integer> it=itemIds.iterator();
					while(counter<amount && it.hasNext()){
						int itemId=it.next();
						ItemFrame item;
						DroppedItem ditem;
						item = (ItemFrame)ItemCache.getInstance().getItem(itemId);
						if(item!=null){
							ditem = item.dropItem(cur.getCurrentMap(), cur.getLocation(),1);
							source.addWrite(ditem.itemSpawnPacket());
						}
						counter++;
					}
				}
			}catch(CommandException e){
				new ServerMessage().execute(e.getMessage(), source);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}

}
