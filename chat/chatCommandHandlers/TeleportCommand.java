package chat.chatCommandHandlers;

import item.DroppedItem;
import Connections.Connection;
import Gamemaster.GameMaster;
import Mob.Mob;
import NPCs.Npc;
import Player.Character;
import Player.PlayerConnection;
import Tools.StringTools;
import World.WMap;
import chat.ChatCommandExecutor;

public class TeleportCommand implements ChatCommandExecutor {

private int needsCommandPower;
	
	public TeleportCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	@Override
	public void execute(String[] parameters, Connection source) {
		System.out.println("Received chat command to teleport");
	    
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
	    
	    if (parameters.length>0) 
	    {
	    	
	    	WMap wMap=WMap.getInstance();
	    	Character ch;
	    	
	    	//tp to coords
	    	if(parameters.length>2 && StringTools.isInteger(parameters[0])){
	    		cur.teleportTo(Integer.parseInt(parameters[0]), Float.parseFloat(parameters[1]), Float.parseFloat(parameters[2]));
	    		return;
	    	}
	    	
	    	//tp to general uid
	    	if(parameters.length>1 && parameters[0].equals("uid") && StringTools.isInteger(parameters[1])){
	    		int uid=(Integer.parseInt(parameters[1]));
	    		if(wMap.CharacterExists(uid)){
	    			ch=wMap.getCharacter(uid);
	    			cur.teleportTo(ch.getCurrentMap(), ch.getlastknownX(), ch.getlastknownY());
	    		}else if(wMap.mobExists(uid)){
	    			Mob mob=wMap.getMob(uid);
	    			cur.teleportTo(mob.getControl().getMap(), mob.getlastknownX(), mob.getlastknownY());
	    		}else if(wMap.npcExists(uid)){
	    			Npc npc=wMap.getNpc(uid);
	    			cur.teleportTo(npc.getMap(), npc.getlastknownX(), npc.getlastknownY());
	    		}else if(wMap.itemExist(uid)){
	    			DroppedItem item=wMap.getItem(uid);
	    			cur.teleportTo(item.getMap(), item.getlastknownX(), item.getlastknownY());
	    		}
	    		return;
	    	}
	    	
	    	//tp to char uid
	    	if(parameters.length>1 && parameters[0].equals("char") && StringTools.isInteger(parameters[1])){
	    		int uid=(Integer.parseInt(parameters[1]));
	    		if(wMap.CharacterExists(uid)){
	    			ch=wMap.getCharacter(uid);
	    			cur.teleportTo(ch.getCurrentMap(), ch.getlastknownX(), ch.getlastknownY());
	    		}
	    		return;
	    	}
	    	
	    	//tp to mob uid
	    	if(parameters.length>1 && parameters[0].equals("mob") && StringTools.isInteger(parameters[1])){
	    		int uid=(Integer.parseInt(parameters[1]));
	    		if(wMap.mobExists(uid)){
	    			Mob mob=wMap.getMob(uid);
	    			cur.teleportTo(mob.getControl().getMap(), mob.getlastknownX(), mob.getlastknownY());
	    		}
	    		return;
	    	}
	    	
	    	//tp to npc uid
	    	if(parameters.length>1 && parameters[0].equals("npc") && StringTools.isInteger(parameters[1])){
	    		int uid=(Integer.parseInt(parameters[1]));
	    		if(wMap.npcExists(uid)){
	    			Npc npc=wMap.getNpc(uid);
	    			cur.teleportTo(npc.getMap(), npc.getlastknownX(), npc.getlastknownY());
	    		}
	    		return;
	    	}
	    	
	    	//tp to item uid
	    	if(parameters.length>1 && parameters[0].equals("item") && StringTools.isInteger(parameters[1])){
	    		int uid=(Integer.parseInt(parameters[1]));
	    		if(wMap.itemExist(uid)){
	    			DroppedItem item=wMap.getItem(uid);
	    			cur.teleportTo(item.getMap(), item.getlastknownX(), item.getlastknownY());
	    		}
	    		return;
	    	}
	    	
	    	//last try: tp to charname
	    	ch = WMap.getInstance().getCharacter(parameters[0]);
	    	if(ch!=null)
	    		cur.teleportTo(ch.getCurrentMap(), ch.getlastknownX(), ch.getlastknownY());
	    	
	    }
	    else
		    System.out.println("Command needs parameters");

	}

}
