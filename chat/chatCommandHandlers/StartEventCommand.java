package chat.chatCommandHandlers;

import Connections.Connection;
import Database.MobDAO;
import Player.Character;
import Player.CharacterMaster;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import chat.ChatCommandExecutor;

public class StartEventCommand implements ChatCommandExecutor {
  
	private int needsCommandPower;
	
	public StartEventCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
  
	public void execute(String[] parameters, Connection source) {
		System.out.println("Received chat command to start the event!");
		
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
    
		MobDAO.initMobs();
		CharacterMaster.announceEventStart();
		
	}  
}
