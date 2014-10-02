package chat.chatCommandHandlers;


import Player.Character;
import GameServer.ServerPackets.ServerMessage;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Connections.Connection;
import chat.ChatCommandExecutor;

public class ShowCommands implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public ShowCommands(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	public void execute(String[] parameters, Connection source) {
		System.out.println("Received chat command to show commands!");
		
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		new ServerMessage().execute("pd:nameOfCharacter = ask for party duel",source);
		
	}
	
}
