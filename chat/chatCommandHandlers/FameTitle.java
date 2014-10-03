package chat.chatCommandHandlers;

import Connections.Connection;
import Gamemaster.GameMaster;
import Player.Character;
import Player.PlayerConnection;
import chat.ChatCommandExecutor;

public class FameTitle implements ChatCommandExecutor {

private int needsCommandPower;
	
	public FameTitle(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	@Override
	public void execute(String[] parameters, Connection source) {
		System.out.println("Received chat command to change fame title " + parameters[0]);
	    
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
	    
	    if (parameters.length>0) 
	    {
	    	cur.setFameTitle(Short.parseShort(parameters[0]));
	    	cur.updateFame(cur);
	    }
	    else
		    System.out.println("No parameters");

	}


}
