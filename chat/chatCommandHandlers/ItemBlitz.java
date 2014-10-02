package chat.chatCommandHandlers;

import item.DroppedItem;
import item.ItemCache;
import item.ItemFrame;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Player.Character;
import Tools.StringTools;
import Connections.Connection;
import chat.ChatCommandExecutor;

public class ItemBlitz implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public ItemBlitz(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}

	public void execute(String[] parameters, Connection source) {
		System.out.println("Received chat command to spawn alot of items!");
		
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
		
		int steps;
		if(parameters.length>2 && StringTools.isInteger(parameters[2])){
			
			steps=Integer.parseInt(parameters[2]);
			
		}else{steps=1;}
		
		if(parameters.length>0 && StringTools.isInteger(parameters[0]) && parameters[0].length() == 9){
			
			int itemID=Integer.parseInt(parameters[0]);
			ItemFrame it;
			DroppedItem dit;
			int j=0;
			for(int i=0;i<amount;j++){
				itemID +=steps;
				if(itemID>300000000 || j>1000)
					return;
				it = (ItemFrame)ItemCache.getInstance().getItem(itemID);
				if(it!=null){
					dit = it.dropItem(cur.getCurrentMap(), cur.getLocation(),1);
					source.addWrite(dit.itemSpawnPacket());
					i++;
				}
			}
			
		}
		
	}

}
