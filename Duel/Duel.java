package Duel;

import java.util.Iterator;
import java.util.LinkedList;

import Player.Character;

public class Duel {

	LinkedList<Character> members;
	
	public Duel(Character askingPerson, Character askedPerson){
		members=new LinkedList<Character>();
		members.add(askingPerson);
		members.add(askedPerson);
	}
	
	public void sendToMembers(byte[] buf, Character notThisOne){
		
		Iterator<Character> iter = members.iterator();
		while(iter.hasNext()) {
			Character ch = iter.next();
			if(ch != null && ch!=notThisOne && !ch.isBot()) {
				ch.addWritePacketWithId(buf);
			}
		}
	}
	
	public void winDuel(Character winner){
		Character loser=getOpponentOf(winner);
		sendToMembers(DuelPackets.getEndDuelPacket(winner, loser),null);
		winner.setDuel(null);
		loser.setDuel(null);
	}
	
	public void loseDuel(Character loser){
		Character winner=getOpponentOf(loser);
		sendToMembers(DuelPackets.getEndDuelPacket(winner, loser),null);
		winner.setDuel(null);
		loser.setDuel(null);
	}
	
	public Character getOpponentOf(Character ch){
		if(members.get(0)==ch)
			return members.get(1);
		return members.get(0);
	}
	
}
