package chat.chatCommandHandlers;


import java.util.LinkedList;

import Parties.Party;
import Player.Character;
import GameServer.ServerPackets.ServerMessage;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import World.WMap;
import Connections.Connection;
import Duel.PartyDuel;
import chat.ChatCommandExecutor;

public class PartyDuelCommand implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public PartyDuelCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	public void execute(String[] parameters, Connection source) {
		System.out.println("Received chat command for handling pt duel!");
		
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		if(parameters.length>0){
			
			Party pt1=cur.getPt();
			PartyDuel pd;
			
			if(parameters[0].equals("y")){
				if(pt1!=null){
					pd=pt1.getPartyDuel();
					if(pd!=null && pd.getDuelState()==0){
						pt1.acceptPartyDuel(cur,true);
						return;
					}
				}
				new ServerMessage().execute("there is no request to accept",source);
				return;
			}else if(parameters[0].equals("n")){
				if(pt1!=null){
					pd=pt1.getPartyDuel();
					if(pd!=null && pd.getDuelState()==0){
						pt1.acceptPartyDuel(cur,false);
						return;
					}
				}
				new ServerMessage().execute("there is no request to refuse",source);
				return;
			}
			
			if(parameters[0].equals("leave")){
				if(cur.isInPtDuel()){
					cur.die();
					new ServerMessage().execute("SUICIDE!",source);
					cur.leavePtDuel();
					return;
				}
				new ServerMessage().execute("there is no partyduel to leave",source);
				return;
			}
			
			if(parameters[0].equals("giveup")){
				if(pt1!=null && pt1.getPartyDuel()!=null){
					if(cur!=pt1.getLeader()){
						new ServerMessage().execute("Cannot give up duel [You are not the leader]",source);
						return;
					}
					pt1.losePartyDuel(true);
					return;
				}
				new ServerMessage().execute("there is no partyduel to give up",source);
				return;
			}
			
			//INVITE TO PARTYDUEL
			if(pt1==null){
				new ServerMessage().execute("Cannot ask for party duel [You are not in a party]",source);
				return;
			}
			if(cur!=pt1.getLeader()){
				new ServerMessage().execute("Cannot ask for party duel [You are not the leader]",source);
				return;
			}
			if(!pt1.canPartyDuel()){
				new ServerMessage().execute("Cannot ask for party duel [Your pt needs faction+lvl36+]",source);
				return;
			}
			if(pt1.getPartyDuel()!=null){
				new ServerMessage().execute("Cannot ask for party duel [You are in a party duel]",source);
				return;
			}
			int amount=parameters.length;
			Character ch[]=new Character[amount];
			Party pt2;
			LinkedList<Party> pts=new LinkedList<Party>();
			pts.add(pt1);
			for(int i=0;i<amount;i++){
				ch[i] = WMap.getInstance().getCharacter(parameters[i]);
				if(ch[i]==null){
					new ServerMessage().execute("Cannot ask for party duel [Player does not exist]",source);
					return;
				}
				pt2=ch[i].getPt();
				if(pt2==null){
					new ServerMessage().execute("Cannot ask for party duel [Target is not in a party]",source);
					return;
				}
				if(!pt2.canPartyDuel()){
					new ServerMessage().execute("Cannot ask for party duel [Target pt needs faction+lvl36+]",source);
					return;
				}
				if(pt2.getPartyDuel()!=null){
					new ServerMessage().execute("Cannot ask for party duel [Target is already in a party duel]",source);
					return;
				}
				if(pt1==pt2){
					new ServerMessage().execute("Cannot ask for party duel [Same Party]",source);
					return;
				}
				pts.add(pt2);
			}
			PartyDuel newpd=new PartyDuel(pts,pt1);
			pt1.setPartyDuel(newpd);
			pt1.sendMessageToMembers("Party duel request sent");
			for(int i=1;i<amount+1;i++){
				pts.get(i).sendPartyDuelRequest(cur, newpd);
			}
		
		}
		
	}
	
}
