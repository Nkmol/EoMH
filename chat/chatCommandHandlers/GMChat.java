package chat.chatCommandHandlers;

import java.util.Iterator;
import java.util.Map;



import World.WMap;
import Player.Character;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Connections.Connection;
import ServerCore.ServerFacade;
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
		
		byte[] gmsg = new byte[14+parameters[0].length()];
		byte[] msg = parameters[0].getBytes();
		
		gmsg[0] = (byte)gmsg.length;
		gmsg[4] = (byte)0x03;
		gmsg[6] = (byte)0x50;
		gmsg[7] = (byte)0xC3;
		gmsg[8] = (byte)0x01;
		gmsg[9] = (byte)0x23;
		
		for(int i=0;i<msg.length;i++) {
			gmsg[i+13] = msg[i];
		}
		
		Iterator<Map.Entry<Integer, Character>> iter = WMap.getInstance().getCharacterMap().entrySet().iterator();
		Character tmp;
		while(iter.hasNext()) {
			Map.Entry<Integer, Character> pairs = iter.next();
			tmp = pairs.getValue();
			if(!tmp.isBot())
			ServerFacade.getInstance().addWriteByChannel(tmp.GetChannel(), gmsg);
		}
		
	}

}
