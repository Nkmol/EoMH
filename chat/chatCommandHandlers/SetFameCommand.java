package chat.chatCommandHandlers;

import Player.Character;
import Player.CharacterMaster;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Connections.Connection;
import Tools.StringTools;
import chat.ChatCommandExecutor;

public class SetFameCommand implements ChatCommandExecutor {  
	
	private int needsCommandPower;
	
	public SetFameCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	 public void execute(String[] parameters, Connection source) {
		  System.out.println("Received chat command to add fame!");
		  
		  Character cur = ((PlayerConnection)source).getActiveCharacter();
		  
		  if(!GameMaster.canUseCommand(cur, needsCommandPower)){
				System.out.println("Not enough command power");
				return;
		  }
		  
		  if(parameters.length>0 && StringTools.isInteger(parameters[0]))
		  {
			  cur.setFame(Integer.parseInt(parameters[0]));
			  cur.setFameTitle(CharacterMaster.getFameTitle(CharacterMaster.getFameTitle(cur.getFame())));
			  cur.updateFame(cur);
		  }else{
			  System.out.println("Command failed");
		  }
		  
	 }  
	  
}