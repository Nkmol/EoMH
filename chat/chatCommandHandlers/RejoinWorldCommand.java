package chat.chatCommandHandlers;


import java.util.LinkedList;

import Player.Character;
import Player.CharacterMaster;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Tools.StringTools;
import Connections.Connection;
import chat.ChatCommandExecutor;

public class RejoinWorldCommand implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public RejoinWorldCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	public void execute(String[] parameters, Connection con) {
		System.out.println("Received chat command to leave the world debugged!");
		
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		//debug character packet stuff
		LinkedList<Integer> testByteIndex=new LinkedList<Integer>();
		LinkedList<Byte> testByteValue=new LinkedList<Byte>();
		for(int j=0;j<parameters.length/2;j++){
			if(StringTools.isInteger(parameters[j*2]) && Integer.parseInt(parameters[j*2])<653
						&&  StringTools.isInteger(parameters[1+j*2])){
					testByteIndex.add(Integer.parseInt(parameters[j*2]));
					testByteValue.add((byte)Integer.parseInt(parameters[1+j*2]));
			}
			
		}
		cur.setTestByteIndex(testByteIndex);
		cur.setTestByteValue(testByteValue);
		
		con.addWrite(CharacterMaster.backToSelection(con));
		
	}
	
}
