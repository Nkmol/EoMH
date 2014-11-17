package chat.chatCommandHandlers;


import Player.Character;
import GameServer.ServerPackets.ServerMessage;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Connections.Connection;
import chat.ChatCommandExecutor;

public class EnableInfosCommand implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public EnableInfosCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	public void execute(String[] parameters, Connection source) {
		System.out.println("Received test command!");
		
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		cur.swapShowInfos();
		String msg;
		if(cur.getShowInfos())
			msg="Enabled special infos";
		else
			msg="Disabled special infos";
		new ServerMessage().execute(msg, source);
		
	}
	
}
