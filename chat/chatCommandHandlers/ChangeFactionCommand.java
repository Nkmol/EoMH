package chat.chatCommandHandlers;


import Player.Character;
import Player.CharacterMaster;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Tools.StringTools;
import Connections.Connection;
import chat.ChatCommandExecutor;

public class ChangeFactionCommand implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public ChangeFactionCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	public void execute(String[] parameters, Connection con) {
		System.out.println("Received chat command to change faction!");
		
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		if(parameters.length>0 && StringTools.isInteger(parameters[0])){
		
			int faction=Integer.parseInt(parameters[0]);
			if(faction>2)
				faction=2;
			cur.changeFaction(faction);
			
			con.addWrite(CharacterMaster.backToSelection(con));
		
		}else{
			
			System.out.println("Command failed");
			
		}
		
	}
	
}
