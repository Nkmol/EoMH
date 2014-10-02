package chat.chatCommandHandlers;

import java.util.Iterator;
import java.util.LinkedList;

import Connections.Connection;
import GameServer.ServerPackets.ServerMessage;
import Player.Character;
import Player.Dolls.DollMaster;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import chat.ChatCommandExecutor;
import Player.Dolls.Doll;
import Skills.SkillMaster;
import Tools.StringTools;
import World.Location;

public class ChatDollCommand implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public ChatDollCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	public void execute(String[] parameters, Connection source) {
	    
		System.out.println("Received chat command for Doll!");
		
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
	    
		if (parameters[0].equals("spawn")){
			
			int map,characterClass,spawnAmount;
			float x,y;
			String name;
			
			map=cur.getCurrentMap();
			x=cur.getlastknownX();
			y=cur.getlastknownY();
			
			if(parameters.length>1)
				name=parameters[1];
			else
				name="Dave";
			
			if(parameters.length>2 && StringTools.isInteger(parameters[2]) && Integer.parseInt(parameters[2])<5)
				characterClass=Integer.parseInt(parameters[2]);
			else
				characterClass=1;
			
			if(parameters.length>3 && StringTools.isInteger(parameters[3]))
				spawnAmount=Integer.parseInt(parameters[3]);
			else
				spawnAmount=1;
			if(spawnAmount>100)
				spawnAmount=100;
			
			for(int i=0;i<spawnAmount;i++){
				String newname;
				if(spawnAmount>1)
					newname=name+i;
				else
					newname=name;
				Doll doll=new Doll(DollMaster.getUid(), newname);
				DollMaster.getDolls().add(doll);
				
				LinkedList<Integer> testByteIndexExt=new LinkedList<Integer>();
				LinkedList<Byte> testByteValueExt=new LinkedList<Byte>();
				for(int j=0;j<(parameters.length-4)/2;j++)
				if(StringTools.isInteger(parameters[4+j*2]) && Integer.parseInt(parameters[4+j*2])+i<612
						&&  StringTools.isInteger(parameters[5+j*2])){
					testByteIndexExt.add(Integer.parseInt(parameters[4+j*2])+i);
					testByteValueExt.add((byte)Integer.parseInt(parameters[5+j*2]));
				}
				
				doll.spawn(map, x+i*5, y, newname, characterClass, null, testByteIndexExt, testByteValueExt, true);
				
				new ServerMessage().execute("Say hello to "+newname+"!",source);
			}
			
		}
		
		if(parameters.length>1 && parameters[1].equals("despawn")){
			
			Doll doll;
			for(Iterator<Doll> i=DollMaster.getDolls().iterator();i.hasNext();){
				doll=i.next();
				if(parameters[0].equals("all") || doll.getName().equals(parameters[0])){
					new ServerMessage().execute("Bye bye "+doll.getName()+"!",source);
					doll.despawn();
					i.remove();
				}
			}
			
		}
		
		if(parameters.length>1 && parameters[1].equals("come")){
			
			Doll doll;
			for(Iterator<Doll> i=DollMaster.getDolls().iterator();i.hasNext();){
				doll=i.next();
				if(parameters[0].equals("all") || doll.getName().equals(parameters[0])){
						doll.goToLocationApprox(cur);
				}
			}
			
		}
		
		if(parameters.length>2 && StringTools.isInteger(parameters[2]) && parameters[1].equals("equip")){
			
			Doll doll;
			int itemID = Integer.parseInt(parameters[2]);
			for(Iterator<Doll> i=DollMaster.getDolls().iterator();i.hasNext();){
				doll=i.next();
				if(parameters[0].equals("all") || doll.getName().equals(parameters[0])){
					doll.equipItem(itemID);
				}
			}
		}
		
		if(parameters.length>1 && parameters[1].equals("follow")){
			
			Doll doll;
			for(Iterator<Doll> i=DollMaster.getDolls().iterator();i.hasNext();){
				doll=i.next();
				if(parameters[0].equals("all") || doll.getName().equals(parameters[0])){
					doll.startFollowCharacter(cur);
				}
			}
			
		}
		
		if(parameters.length>1 && parameters[1].equals("live")){
			
			Doll doll;
			for(Iterator<Doll> i=DollMaster.getDolls().iterator();i.hasNext();){
				doll=i.next();
				if(parameters[0].equals("all") || doll.getName().equals(parameters[0])){
					doll.startRandomMoves();
				}
			}
			
		}
		
		if(parameters.length>2 && parameters[1].equals("annoy")){
			
			Doll doll;
			for(Iterator<Doll> i=DollMaster.getDolls().iterator();i.hasNext();){
				doll=i.next();
				if(parameters[0].equals("all") || doll.getName().equals(parameters[0])){
					doll.startAnnoyCharacter(parameters[2]);
				}
			}
			
		}

		if(parameters.length>1 && parameters[1].equals("stop")){
	
			Doll doll;
			for(Iterator<Doll> i=DollMaster.getDolls().iterator();i.hasNext();){
				doll=i.next();
				if(parameters[0].equals("all") || doll.getName().equals(parameters[0])){
					doll.stop();
				}
			}
	
		}
		
		if(parameters.length>3 && StringTools.isInteger(parameters[3]) && parameters[1].equals("atk")){
			
			Doll doll;
			int skillID = Integer.parseInt(parameters[3]);
			for(Iterator<Doll> i=DollMaster.getDolls().iterator();i.hasNext();){
				doll=i.next();
				if(parameters[0].equals("all") || doll.getName().equals(parameters[0])){
					try{
						Location loc=(Location)doll.getDoll().getArea().getEnemyNear(parameters[2]);
						if(loc!=null)
							SkillMaster.castSimpleSkill(null, doll.getDoll(), skillID, (byte)0, (byte)1, (byte)0, new int[]{loc.getuid()}, true);
					}catch(Exception e){
						System.out.println("Doll cannot use that skill");
					}
				}
			}
		}
		
		if(parameters.length>2 && StringTools.isInteger(parameters[2]) && parameters[1].equals("learnskill")){
			
			Doll doll;
			int skillID = Integer.parseInt(parameters[2]);
			for(Iterator<Doll> i=DollMaster.getDolls().iterator();i.hasNext();){
				doll=i.next();
				if(parameters[0].equals("all") || doll.getName().equals(parameters[0])){
					doll.learnSkill(skillID,false);
				}
			}
			
		}
		
		if(parameters.length>2 && StringTools.isInteger(parameters[2]) && parameters[1].equals("hardness")){
			
			Doll doll;
			float hardness = ((float)Integer.parseInt(parameters[2]))/100f;
			for(Iterator<Doll> i=DollMaster.getDolls().iterator();i.hasNext();){
				doll=i.next();
				if(parameters[0].equals("all") || doll.getName().equals(parameters[0])){
					doll.setHardness(hardness);
				}
			}
			
		}
	
	}
	
}
