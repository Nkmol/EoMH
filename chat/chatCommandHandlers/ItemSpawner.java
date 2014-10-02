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

public class ItemSpawner implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public ItemSpawner(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}

	public void execute(String[] parameters, Connection source) {
		System.out.println("Received chat command to spawn an item!");
		
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		for(int i=0;i<parameters.length;i++) {
			System.out.println("Command param[" + (i+1) + "] : " + parameters[i]);
			if(StringTools.isInteger(parameters[0]) && parameters[i].length() == 9) {
				System.out.println("Param[" + (i+1) + "] is of integer type length 9!");
				int itemID = Integer.parseInt(parameters[0]);
				System.out.println("About to spawn item: " + itemID + " at coordinates: " + cur.getlastknownX() + "," + cur.getlastknownY() );
				ItemFrame it = (ItemFrame)ItemCache.getInstance().getItem(itemID);
				if(it!=null){
					int amount=1;
					if(parameters.length>1 && StringTools.isInteger(parameters[1]))
						amount=Integer.parseInt(parameters[1]);
					DroppedItem dit = it.dropItem(cur.getCurrentMap(), cur.getLocation(),amount);
					source.addWrite(dit.itemSpawnPacket());
					System.out.println("Loaded following item from DB: " + it.getId());
					System.out.println("Height: " + it.getHeight() + " Width: " + it.getWidth());
					System.out.println("Type: " + it.getType() + " Min lvl: "  + it.getMinLvl());
				}
			}
		}
	}

}
