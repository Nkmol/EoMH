package chat.chatCommandHandlers;

import Player.Character;
import Player.CharacterPackets;
import Player.PlayerConnection;
import Buffs.Buff;
import Buffs.ItemBuff;
import Connections.Connection;
import Gamemaster.GameMaster;
import chat.ChatCommandExecutor;

public class bufficon implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public bufficon(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	@Override
	public void execute(String[] parameters, Connection source) {
		System.out.println("Received chat command for a buff");
	    
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
	    if (parameters.length>0) 
	    {
	    	if (parameters.length>2) 
		    {
	    		//source.addWrite(CharacterPackets.getBuffIconPacket(cur, Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2]), Integer.parseInt(parameters[3])));
		    }
	    	else
	    	{
	    		System.out.println("buff id: " + parameters[0]);
	    		Buff buff = new ItemBuff(cur, Short.parseShort(parameters[0]), (short)8, (short)1);
	    		source.addWrite(CharacterPackets.getBuffPacket(cur, Short.parseShort(parameters[0]), Short.parseShort(parameters[1]), buff));
	    	}
	    }
	    else
		    System.out.println("No parameters");

	}

}
