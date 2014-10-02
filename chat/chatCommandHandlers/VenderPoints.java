package chat.chatCommandHandlers;

import Connections.Connection;
import Player.Character;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import chat.ChatCommandExecutor;

public class VenderPoints implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public VenderPoints(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	@Override
	public void execute(String[] parameters, Connection source) {
		System.out.println("Received chat command to change fame and vendingpoint");
	    
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
	    
	    if (parameters.length>0) 
	    {
	    	int points = Integer.getInteger(parameters[0]);
	    	if( points > 255)
	    		points = 255;

	    	cur.getInventory().setVendingPoints(points);
	    	cur.getInventory().updateVendingPoints(cur);
	    }
	    else
		    System.out.println("No parameters");

	}

}
