package chat.chatCommandHandlers;


import Player.Character;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Tools.StringTools;
import Connections.Connection;
import chat.ChatCommandExecutor;

public class HealCommand implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public HealCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	public void execute(String[] parameters, Connection source) {
		System.out.println("Received chat command to heal up!");
		
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		boolean isValid = true;
		
		if(parameters.length==3){
			for(int i=0;i<parameters.length;i++) {
				System.out.println("Command param[" + (i+1) + "] : " + parameters[i]);
				if(!StringTools.isInteger(parameters[i])) {
					isValid = false;
					System.out.println("parameter is not an integer");
				}
			}
		}else{
			isValid = false;
			System.out.println("3 parameters required");
		}
		
		if(isValid) {
			System.out.println("Healing the character");
			short hp = (short)Integer.parseInt(parameters[0]);
			short mana = (short)Integer.parseInt(parameters[1]);
			short stam = (short)Integer.parseInt(parameters[2]);	
			
			cur.addHpMpSp(hp,mana,stam);
		}else{
			System.out.println("Command failed");
		}
	}
	
}
