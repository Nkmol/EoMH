package chat.chatCommandHandlers;

import Player.Character;
import GameServer.ServerMaster;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Connections.Connection;
import chat.ChatCommandExecutor;

public class GMChat implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public GMChat(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}

	public void execute(String[] parameters, Connection source) {
		System.out.println("Handling GM red chat command");
		
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		if(parameters.length>0)
			ServerMaster.announceChat(parameters[0]);
		
	}

}
