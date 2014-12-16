package chat.chatCommandHandlers;

import Mob.MobMaster;
import Player.Character;
import ExperimentalStuff.EffectMaster;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Connections.Connection;
import Tools.StringTools;
import chat.ChatCommandExecutor;

public class SpawnMobCommand implements ChatCommandExecutor {  
	
	private int needsCommandPower;
	
	public SpawnMobCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	 public void execute(String[] parameters, Connection source) {
		  System.out.println("Received chat command to spawn mobs!");
		  
		  Character cur = ((PlayerConnection)source).getActiveCharacter();
		  
		  if(!GameMaster.canUseCommand(cur, needsCommandPower)){
				System.out.println("Not enough command power");
				return;
		  }
		  
		  if(parameters.length>0 && StringTools.isInteger(parameters[0]) && MobMaster.doesMobExist(Integer.parseInt(parameters[0])))
		  {
			  
			  int amount=1;
			  if(parameters.length>1 && StringTools.isInteger(parameters[1]))
				  amount=Integer.parseInt(parameters[1]);
			  if(amount>100)
				  amount=100;
			  
			  int spawnRadius=1;
			  if(parameters.length>2 && StringTools.isInteger(parameters[2]))
				  spawnRadius=Integer.parseInt(parameters[2]);
			  if(amount>1000)
				  spawnRadius=1000;
			  
			  if(parameters.length>2) {
				  MobMaster.start = Integer.parseInt(parameters[2]);
				  MobMaster.length = Integer.parseInt(parameters[3]);
				  MobMaster.value = Integer.parseInt(parameters[4]);
			  }
			  
			  MobMaster.spawnMob(Integer.parseInt(parameters[0]), amount, cur.getCurrentMap(), cur.getLocation(), spawnRadius, true, false, 1f);
			  EffectMaster.spawnEffects(cur.getCurrentMap(), cur.getlastknownX(), cur.getlastknownY(), 1);
			  
		  }else{
			  System.out.println("Command failed");
		  }
		  
	 }  
	  
}