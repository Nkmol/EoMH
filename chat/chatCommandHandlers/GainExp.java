package chat.chatCommandHandlers;


import Player.Character;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Tools.StringTools;
import Connections.Connection;
import chat.ChatCommandExecutor;

public class GainExp implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public GainExp(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	public void execute(String[] parameters, Connection con) {
		System.out.println("Received chat command to gain exp!");
		
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		if(parameters.length>0 && StringTools.isInteger(parameters[0])){
		
			cur.gainExp(Long.parseLong(parameters[0]),true);
		
		}else{
			
			System.out.println("Command failed");
			
		}
		
	}
	
}
