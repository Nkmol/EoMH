package chat.chatCommandHandlers;

import Player.Character;
import Player.CharacterMaster;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import World.WMap;
import Connections.Connection;
import GameServer.ServerPackets.ServerMessage;
import chat.ChatCommandExecutor;

public class PrintStatsCommand implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public PrintStatsCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	public void execute(String[] parameters, Connection con) {
		System.out.println("Received chat command to print stats!");
		
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		
		if(parameters.length>0){
			
			Character ch = WMap.getInstance().getCharacter(parameters[0]);
			if(ch!=null){
				
				if(ch!=cur && !GameMaster.canUseCommand(cur, needsCommandPower)){
					System.out.println("Not enough command power");
					return;
				}
				
				new ServerMessage().execute("NAME:"+ch.getName(),con);
				new ServerMessage().execute("ISBOT:"+ch.isBot(),con);
				new ServerMessage().execute("GMrank:"+ch.getGMrank()+" Commands:"+ch.hasCommands(),con);
				new ServerMessage().execute("CLASS:"+ch.getCharacterClass(),con);
				new ServerMessage().execute("ISABANDONED:"+ch.isAbandoned()+" ISDEAD:"+ch.isDead(),con);
				new ServerMessage().execute("MAP:"+ch.getCurrentMap()+" X:"+ch.getlastknownX()+" Y:"+ch.getlastknownY(),con);
				if(ch.getLevel()<CharacterMaster.getLvlCap())
					new ServerMessage().execute("LVL:"+ch.getLevel()+" EXP:"+ch.getExp()+" EXP%:"+ch.getExp()/CharacterMaster.getLvlExp(ch.getLevel())*100,con);
				else
					new ServerMessage().execute("LVL:"+ch.getLevel()+" EXP:"+ch.getExp(),con);
				new ServerMessage().execute("FACTION:"+ch.getFaction()+" FAME:"+ch.getFame(),con);
				new ServerMessage().execute("KAO:"+ch.getKao()+" SIZE:"+ch.getSize(),con);
				new ServerMessage().execute("HP:"+ch.getHp()+"/"+ch.getMaxhp(),con);
				new ServerMessage().execute("MANA:"+ch.getMana()+"/"+ch.getMaxmana(),con);
				new ServerMessage().execute("STAM:"+ch.getStamina()+"/"+ch.getMaxstamina(),con);
				new ServerMessage().execute("ATK:"+ch.getAttack(),con);
				new ServerMessage().execute("ATKBASICSUC:"+ch.getBasicAtkSuc()+" ATKADDSUC:"+ch.getAdditionalAtkSuc()+" ATKSUCMUL:"+ch.getAtkSucMul()*100+"%", con);
				new ServerMessage().execute("TOTALATKSUC: "+ch.getAtkSuc(),con);
				new ServerMessage().execute("MINDMG:"+ch.getMinDmg()+" MAXDMG:"+ch.getMaxDmg(),con);
				new ServerMessage().execute("DEF:"+ch.getDefence(),con);
				new ServerMessage().execute("DEFBASICSUC:"+ch.getBasicDefSuc()+" DEFADDSUC:"+ch.getAdditionalDefSuc()+" DEFSUCMUL:"+ch.getDefSucMul()*100+"%",con);
				new ServerMessage().execute("TOTALDEFSUC: "+ch.getDefSuc(),con);
				new ServerMessage().execute("CRITDMG:"+ch.getCritdmg(),con);
				new ServerMessage().execute("CRITBASICRATE:"+ch.getBasicCritRate()+" CRITRATESUC:"+ch.getAdditionalCritRate()+" CRITRATEMUL:"+ch.getCritRateMul()*100+"%",con);
				new ServerMessage().execute("TOTALCRITRATE: "+ch.getCritRate(),con);
				short stats[]=ch.getStats();
				new ServerMessage().execute("STR:"+stats[0]+" DEX:"+stats[1]+" VIT:"+stats[2]+" INT:"+stats[3]+" AGI:"+stats[4],con);
				new ServerMessage().execute("CHARACTERPOINTS:"+ch.getStatPoints()+" SKILLPOINTS:"+ch.getSkillPoints(),con);
			
			}else{
				System.out.println("Character not found, command failed");
			}
		
		}else{
			
			System.out.println("Command failed");
			
		}
		
	}
	
}
