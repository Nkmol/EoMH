package chat.chatCommandHandlers;


import Player.Character;
import Player.CharacterMaster;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import ServerCore.ServerFacade;
import Tools.StringTools;
import World.WMap;
import Connections.Connection;
import chat.ChatCommandExecutor;

public class SetKaoCommand implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public SetKaoCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	public void execute(String[] parameters, Connection con) {
		System.out.println("Received chat command to set kao!");
		
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		if(parameters.length>1 && StringTools.isInteger(parameters[1])){
			
			Character ch = WMap.getInstance().getCharacter(parameters[0]);
			if(ch!=null){
				short kao=Short.parseShort(parameters[1]);
				ch.setKao(kao);
				if(!ch.isBot()){
					Connection targetCon=ServerFacade.getInstance().getConnectionByChannel(ch.GetChannel());
					if (targetCon!=null)
						targetCon.addWrite(CharacterMaster.backToSelection(targetCon));
				}else{
					ch.rejoin();
				}
			}else{
				System.out.println("Character not found, command failed");
			}
		
		}else{
			
			System.out.println("Command failed");
			
		}
		
	}
	
}
