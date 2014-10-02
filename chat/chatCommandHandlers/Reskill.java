package chat.chatCommandHandlers;


import Player.Character;
import Player.CharacterMaster;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Skills.SkillMaster;
import Connections.Connection;
import chat.ChatCommandExecutor;

public class Reskill implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public Reskill(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	public void execute(String[] parameters, Connection source) {
		System.out.println("Received chat command to reskill!");
		
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		SkillMaster.reSkill(cur);
		
		source.addWrite(CharacterMaster.backToSelection(source));
		
	}
	
}
