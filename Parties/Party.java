package Parties;

import item.ItemFrame;

import java.util.Iterator;
import java.util.LinkedList;

import Duel.PartyDuel;
import ExperimentalStuff.EffectMaster;
import GameServer.ServerPackets.ServerMessage;
import Mob.MobMaster;
import Player.Character;
import Player.CharacterPackets;
import ServerCore.ServerFacade;

public class Party {

	LinkedList<Character> members;
	Character leader;
	int ptlvl;
	int ptexp;
	private PartyDuel pd;
	
	public Party(Character askingPerson, Character askedPerson){
		ptlvl=1;
		ptexp=0;
		members=new LinkedList<Character>();
		members.add(askingPerson);
		members.add(askedPerson);
		leader=askingPerson;
	}
	
	public int getMemberAmount(){
		return members.size();
	}
	
	public Character getMember(int index){
		return members.get(index);
	}
	
	public boolean addMember(Character ch){
		return members.add(ch);
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
	
	public Character getLeader(){
		return leader;
	}
	
	public void setLeader(Character newleader){
		
		if(members.contains(newleader)){
			members.remove(newleader);
			members.addFirst(newleader);
			this.leader=newleader;
		}
		
	}
	
	public void leaveParty(Character ch){
		
		if(members.contains(ch)){
			ch.setPt(null);
			members.remove(ch);
			if(members.size()<2 || isBotParty()){
				deleteParty();
				return;
			}
			if(ch==leader){
				setLeader(firstPlayer());
			}
		}
		
	}
	
	private boolean isBotParty(){
		
		boolean b=true;
		Iterator<Character> i=members.iterator();
		while(b && i.hasNext()){
			if(!i.next().isBot())
				b=false;
		}
		return b;
		
	}
	
	private void deleteParty(){
		Character ch;
		Iterator<Character> i=members.iterator();
		while(i.hasNext()){
			ch=i.next();
			ch.setPt(null);
			if(!ch.isBot())
				ch.addWritePacketWithId(PartyPackets.getLeavePacket(ch, this));
		}
		leader=null;
	}
	
	private Character firstPlayer(){
		Character ch=null;
		Character tmp;
		Iterator<Character> i=members.iterator();
		while(ch==null && i.hasNext()){
			tmp=i.next();
			if(!tmp.isBot())
				ch=tmp;
		}
		return ch;
	}
	
	public void killMob(Character ch, long totalExp){
		sendExpToMembers(totalExp);
		gainPtExp(ch, 1);
	}
	
	private void sendExpToMembers(long totalExp){
		Character ch;
		Iterator<Character> i=members.iterator();
		while(i.hasNext()){
			ch=i.next();
			ch.gainExp((long)(totalExp*PartyMaster.getExpFactorByParty(this,ch)*PartyMaster.getExpFactorByPtLvl(this)), true);
		}
	}
	
	public int getHighestLvl(){
		int highestLvl=0;
		int tmp;
		Iterator<Character> i=members.iterator();
		while(i.hasNext()){
			tmp=i.next().getLevel();
			if(tmp>highestLvl)
				highestLvl=tmp;
		}
		return highestLvl;
	}
	
	public int getLowestLvl(){
		int lowestLvl=255;
		int tmp;
		Iterator<Character> i=members.iterator();
		while(i.hasNext()){
			tmp=i.next().getLevel();
			if(tmp<lowestLvl)
				lowestLvl=tmp;
		}
		return lowestLvl;
	}
	
	private void gainPtExp(Character ch, int exp){
		ptexp+=exp;
		while(ptexp>=PartyMaster.getPtLvlExp(ptlvl)){
			ptexp-=PartyMaster.getPtLvlExp(ptlvl);
			String s;
			if(ptlvl<PartyMaster.getLvlCap()){
				ptlvl+=1;
				s="Party lvled up to lvl"+ptlvl+"! New exprate is "+(int)(PartyMaster.getExpFactorByPtLvl(this)*100)+"%.";
				sendMessageToMembers(s);
				EffectMaster.spawnEffects(ch.getCurrentMap(), ch.getlastknownX(), ch.getlastknownY(), 4);
			}else{
				int lvl=getHighestLvl();
				if(lvl>=36){
					int mobid;
					mobid=7000+(lvl-30)/5;
					if(mobid>7026)
						mobid=7026;
					MobMaster.spawnMob(mobid, 1, ch.getCurrentMap(), ch.getLocation(), 1, true, true, 10);
					s="Party spawned a secret mob!";
					sendMessageToMembers(s);
					EffectMaster.spawnEffects(ch.getCurrentMap(), ch.getlastknownX(), ch.getlastknownY(), 4);
				}
			}
			
		}
	}
	
	public int getPtlvl(){
		return ptlvl;
	}
	
	public void sendMessageToMembers(String s){
		Character ch;
		Iterator<Character> i=members.iterator();
		while(i.hasNext()){
			ch=i.next();
			if(!ch.isBot())
				new ServerMessage().execute(s,ServerFacade.getInstance().getConnectionByChannel(ch.GetChannel()));
		}
	}
	
	public void sendAnnounceToMembers(String s){
		Character ch;
		Iterator<Character> i=members.iterator();
		while(i.hasNext()){
			ch=i.next();
			if(!ch.isBot())
				new ServerMessage().executeAnnounce(s,ServerFacade.getInstance().getConnectionByChannel(ch.GetChannel()));
		}
	}
	
	public void sendItemToMembers(ItemFrame it, int amount, int uid){
		int am=amount/getPlayerAmount();
		int amleft=amount%getPlayerAmount();
		Character ch;
		Iterator<Character> i=members.iterator();
		ch=i.next();
		if(ch!=null){
			if(am+amleft!=0){
				try{
					if(!ch.isBot())
						amleft=ch.getInventory().pickItem(it,am+amleft);
					ServerFacade.getInstance().addWriteByChannel(ch.GetChannel(),CharacterPackets.getPickItemPacket(ch, it, am+amleft, uid, (byte)0, (byte)0, (byte)0));
				}catch(Exception e){}
			}
			if(am!=0){
				while(i.hasNext()){
					ch=i.next();
					try{
						if(!ch.isBot())
							amleft+=ch.getInventory().pickItem(it,am);
						ServerFacade.getInstance().addWriteByChannel(ch.GetChannel(),CharacterPackets.getPickItemPacket(ch, it, am, uid, (byte)0, (byte)0, (byte)0));
					}catch(Exception e){}
				}
			}
		}
		if(amleft!=0){
			try{
				if(leader.getInventory().pickItem(it,amleft)!=0)
					new ServerMessage().execute("Cannot pick item [coin limit]",ServerFacade.getInstance().getConnectionByChannel(leader.GetChannel()));
			}catch(Exception e){}
		}
	}
	
	public int getPlayerAmount(){
		int amount=0;
		Character ch;
		Iterator<Character> i=members.iterator();
		while(i.hasNext()){
			ch=i.next();
			if(!ch.isBot())
				amount+=1;
		}
		return amount;
	}
	
	public void refreshChar(Character ch){
		
		sendToMembers(PartyPackets.getRefreshPtPacket(ch, this), null);
		
	}
	
	public void refreshFullPt(){
		
		for(int j=0;j<getMemberAmount();j++){
			sendToMembers(PartyPackets.getRefreshPtPacket(members.get(j), this), null);
		}
		
	}
	
	public void sendPartyDuelRequest(Character ch, PartyDuel pd){
		
		this.pd=pd;
		sendAnnounceToMembers("Party duel request by "+ch.getName());
		sendMessageToMembers("To accept/refuse the party duel type pd:y/pd:n");
		
	}
	
	public PartyDuel getPartyDuel(){
		return pd;
	}
	
	public void setPartyDuel(PartyDuel pd){
		this.pd=pd;
	}
	
	public void acceptPartyDuel(Character ch,boolean accept){
		if(pd!=null){
			pd.acceptPartyDuel(ch,accept);
		}
	}
	
	public void refusePartyDuel(){
		pd=null;
		sendMessageToMembers("Party duel refused");
	}
	
	public void startPartyDuel(){
		LinkedList<Party> parties=pd.getOtherParties(this);
		Party pt;
		for(Iterator<Party> i=parties.iterator();i.hasNext();){
			pt=i.next();
			for(int j=0;j<getMemberAmount();j++){
				pt.sendKaoPacketToMembers(getMember(j));
			}
		}
		sendAnnounceToMembers("Party duel started");
		sendMessageToMembers("To leave the duel type pd:leave [BE CAREFUL ABOUT MOB LAST HITS]");
		sendMessageToMembers("The leader can type pd:giveup to give up the duel");
	}
	
	public void sendKaoPacketToMembers(Character enemy){
		Iterator<Character> iter = members.iterator();
		while(iter.hasNext()) {
			Character ch = iter.next();
			if(ch != null && !ch.isBot()) {
				ServerFacade.getInstance().addWriteByChannel(ch.GetChannel(), CharacterPackets.getVanishByID(enemy.getCharID()));
				ServerFacade.getInstance().addWriteByChannel(ch.GetChannel(), CharacterPackets.getExtCharPacket(enemy,ch));
			}
		}
	}
	
	public boolean everyoneHasFaction(){
		boolean b=true;
		Iterator<Character> i=members.iterator();
		while(b && i.hasNext()){
			if(i.next().getFaction()==0)
				b=false;
		}
		return b;
	}
	
	public boolean canPartyDuel(){
		if(getLowestLvl()<36 || !everyoneHasFaction())
			return false;
		return true;
	}
	
	public void leavePartyDuel(Character ch){
		int amount=remainingDuelPlayers().size();
		sendMessageToMembers(ch.getName()+" left the duel ["+amount+" left]");
		if(amount==0)
			losePartyDuel(false);
	}
	
	public void losePartyDuel(boolean updateKao){
		pd.kickParty(this,updateKao);
		pd=null;
		sendAnnounceToMembers("YOUR PARTY LOST THE DUEL!");
	}
	
	public void winPartyDuel(){
		pd=null;
		sendAnnounceToMembers("YOUR PARTY WON THE DUEL!");
	}
	
	public LinkedList<Character> remainingDuelPlayers(){
		LinkedList<Character> chs=new LinkedList<Character>();
		Iterator<Character> iter = members.iterator();
		while(iter.hasNext()) {
			Character ch = iter.next();
			if(ch != null) {
				if(ch.isInPtDuel()){
					chs.add(ch);
				}
			}
		}
		return chs;
	}
	
	public void dollsGetNewAggro(Character ch, int uid){
		if(ch!=leader)
			return;
		Iterator<Character> iter = members.iterator();
		while(iter.hasNext()) {
			Character doll = iter.next();
			if(doll != null && doll.getDoll()!=null) {
				doll.getDoll().startAnnoyCharacter(uid);
			}
		}
	}
	
}
