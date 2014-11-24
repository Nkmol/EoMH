package chat.chatCommandHandlers;

import java.util.Iterator;
import java.util.LinkedList;

import Database.ItemDAO;
import ExperimentalStuff.CommandException;
import ExperimentalStuff.EffectMaster;
import ExperimentalStuff.IntelligentCommands;
import GameServer.ServerPackets.ServerMessage;
import Gamemaster.GameMaster;
import Mob.MobMaster;
import Player.PlayerConnection;
import Player.Character;
import Tools.StringTools;
import Connections.Connection;
import chat.ChatCommandExecutor;

public class MobListCommand implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public MobListCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}

	public void execute(String[] parameters, Connection source) {
		System.out.println("Received chat command to spawn a filtered moblist!");
		
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		if(parameters.length>0){
			  
			int amount=1;
			if(parameters.length>1 && StringTools.isInteger(parameters[1]))
				amount=Integer.parseInt(parameters[1]);
			if(amount>100)
				amount=100;
			  
			int spawnRadius=1;
			if(parameters.length>2 && StringTools.isInteger(parameters[2]))
					spawnRadius=Integer.parseInt(parameters[2]);
			if(spawnRadius>1000)
				spawnRadius=1000;
			
			try{
				LinkedList<Integer> mobIds=IntelligentCommands.getIdsByCommand(ItemDAO.getInstance().getSqlConnection(), "mob", parameters[0]);
				int size=mobIds.size();
				int amountPerMob=amount/size;
				//could be 0 here with large size so set it to 1
				if(amountPerMob==0)
					amountPerMob=1;
				int counter=0;
				if((parameters.length>3 && parameters[3].equals("random")) || (parameters.length>2 && parameters[2].equals("random")) || (parameters.length>1 && parameters[1].equals("random"))){
					while(counter<amount && !mobIds.isEmpty()){
						int mobId=mobIds.remove((int)(Math.random()*mobIds.size()));
						//dont allow a bigger amount
						if(counter+amountPerMob>amount)
							amountPerMob=amount-counter;
						MobMaster.spawnMob(mobId, amountPerMob, cur.getCurrentMap(), cur.getLocation(), spawnRadius, true, false, 1f);
						counter+=amountPerMob;
					}
				}else{
					Iterator<Integer> it=mobIds.iterator();
					while(counter<amount && it.hasNext()){
						int mobId=it.next();
						//dont allow a bigger amount
						if(counter+amountPerMob>amount)
							amountPerMob=amount-counter;
						MobMaster.spawnMob(mobId, amountPerMob, cur.getCurrentMap(), cur.getLocation(), spawnRadius, true, false, 1f);
						counter+=amountPerMob;
					}
				}
				EffectMaster.spawnEffects(cur.getCurrentMap(), cur.getlastknownX(), cur.getlastknownY(), 1);
			}catch(CommandException e){
				new ServerMessage().execute(e.getMessage(), source);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}else{
		System.out.println("Command failed");
		}
		
	}

}
