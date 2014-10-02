package Duel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import Parties.Party;
import Player.Character;

public class PartyDuel {

	private LinkedList<Party> parties;
	private int duelState; //0=request, 1=start
	private int requestedPlayers=0;
	private LinkedList<Character> acceptingPlayers;
	private Timer acceptingTimer;
	private LinkedList<Character> remainingPlayers;
	private LinkedList<Character> allPlayers;
	private LinkedList<Party> remainingParties;
	private Party requestingPt;
	
	public PartyDuel(LinkedList<Party> parties, Party requestingPt){
		
		this.parties=parties;
		duelState=0;
		allPlayers=new LinkedList<Character>();
		for(Iterator<Party> i=parties.iterator();i.hasNext();){
			Party pt=i.next();
			if(pt!=requestingPt){
				requestedPlayers+=pt.getPlayerAmount();
			}
			for(int j=0;j<pt.getMemberAmount();j++){
				allPlayers.add(pt.getMember(j));
			}
		}
		acceptingPlayers=new LinkedList<Character>();
		acceptingTimer=new Timer();
		acceptingTimer.schedule(new TimerTask() {
			  @Override
			  public void run() {
				  PartyDuel.this.acceptPartyDuel(null,false);
				  cancel();
			  }
		}, 30000);
		this.requestingPt=requestingPt;
		
	}
	
	public synchronized void acceptPartyDuel(Character ch, boolean accept){
		
		if(duelState==0){
			if(accept && !acceptingPlayers.contains(ch)){
				acceptingPlayers.add(ch);
				requestingPt.sendMessageToMembers(ch.getName()+" accepted the pt duel request ["+acceptingPlayers.size()+"/"+requestedPlayers+"]");
				if(acceptingPlayers.size()==requestedPlayers){
					//start duel
					acceptingTimer.cancel();
					startDuel();
				}
			}else{
				//refuse duel
				acceptingTimer.cancel();
				for(Iterator<Party> i=parties.iterator();i.hasNext();){
					Party pt=i.next();
					pt.refusePartyDuel();
				}
				requestingPt.sendMessageToMembers("Someone refused the pt duel request");
			}
		}
		
	}
	
	public int getDuelState(){
		return duelState;
	}
	
	public LinkedList<Party> getOtherParties(Party pt){
		LinkedList<Party> tmp=new LinkedList<Party>();
		Party pttmp;
		for(Iterator<Party> i=parties.iterator();i.hasNext();){
			pttmp=i.next();
			if(pt!=pttmp)
				tmp.add(pttmp);
		}
		return tmp;
	}
	
	public void startDuel(){
		duelState=1;
		remainingPlayers=allPlayers;
		for(Iterator<Party> i=parties.iterator();i.hasNext();){
			Party pt=i.next();
			pt.startPartyDuel();
		}
		remainingParties=parties;
	}
	
	public boolean isStillInDuel(Character ch){
		if(duelState!=1)
			return false;
		if(!remainingPlayers.contains(ch))
			return false;
		return true;
	}
	
	public void leaveDuel(Character ch){
		if(duelState==1){
			remainingPlayers.remove(ch);
			ch.getPt().leavePartyDuel(ch);
		}
	}
	
	public void kickParty(Party pt, boolean updateLosers){
		remainingParties.remove(pt);
		LinkedList<Character> chs;
		for(Iterator<Party> i=remainingParties.iterator();i.hasNext();){
			Party tmp=i.next();
			chs=pt.remainingDuelPlayers();
			for(int j=0;j<chs.size();j++){
				tmp.sendKaoPacketToMembers(chs.get(j));
			}
			if(updateLosers){
				chs=tmp.remainingDuelPlayers();
				for(int j=0;j<chs.size();j++){
					pt.sendKaoPacketToMembers(chs.get(j));
				}
			}
			tmp.sendAnnounceToMembers("The party of "+pt.getLeader().getName()+" lost the duel");
		}
		if(remainingParties.size()==1)
			endDuel(remainingParties.get(0));
	}
	
	public void endDuel(Party winner){
		winner.winPartyDuel();
	}
	
	public int getRandomTarget(Party notme){
		int target=-1;
		LinkedList<Character> chs=new LinkedList<Character>();
		for(Iterator<Character> i=remainingPlayers.iterator();i.hasNext();){
			Character ch=i.next();
			if(ch.getPt()!=notme)
				chs.add(ch);
		}
		if(chs.size()!=0){
			target=chs.get((int)(Math.random()*chs.size())).getuid();
		}
		return target;
	}
	
}
