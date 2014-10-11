package chat.chatCommandHandlers;

import Connections.Connection;
import Gamemaster.GameMaster;
import Player.Character;
import Player.CharacterMaster;
import Player.PlayerConnection;
import World.WMap;
import chat.ChatCommandExecutor;

public class teleportCommand implements ChatCommandExecutor {

private int needsCommandPower;
	
	public teleportCommand(int needsCommandPower){
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
	    	if(parameters.length==3) {
	    		cur.setCurrentMap(Integer.parseInt(parameters[0]));
	    		cur.setX(Float.parseFloat(parameters[1]));
	    		cur.setY(Float.parseFloat(parameters[2]));
	    	}
	    	else if(parameters.length==1) {
	    		Character ch = WMap.getInstance().getCharacter(parameters[0]);
	    		cur.setCurrentMap(ch.getCurrentMap());
	    		cur.setX(ch.getlastknownX());
	    		cur.setY(ch.getlastknownX());
	    	}
	    	source.addWrite(CharacterMaster.backToSelection(source));
	    }
	    else
		    System.out.println("Command needs parameters");

	}

}
