package NPCs;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import logging.ServerLogger;
import Player.Character;
import ServerCore.ServerFacade;
import World.Grid;
import World.Area;
import World.Location;
import World.OutOfGridException;
import World.WMap;
import World.Waypoint;

public class Npc implements Location{
	private WMap wmap = WMap.getInstance();
	private List<Integer> iniPackets = new ArrayList<Integer>();
	private int Uid; // unique id for grid n such
	private Waypoint location;
	private int map;
	private Grid grid;
	private Area area;
	private ServerLogger log = ServerLogger.getInstance();
	private NpcData data;
	
	public Npc(NpcData data,int map, int uid, Waypoint loc){
		this.Uid = uid;
		this.data = data;
		this.map = map;
		this.location = loc;
		this.joinGameWorld();
	}

	public void setuid(int uid) {
		this.Uid = uid;
	}

	public int getuid() {
		return Uid;
	}
	public Waypoint getLocation() {
		return this.location;
	}
	public void updateEnvironment(List<Integer> players) {
		Iterator<Integer> plIter = this.iniPackets.iterator();
		Integer tmp = null;
		
		synchronized(this.iniPackets){
			while(plIter.hasNext()) {
				tmp = plIter.next();
				if(!players.contains(tmp)) {//if character is no longer in range, remove it
					plIter.remove();	
				}
				else { // remove from need ini list if we already have it on the list
					players.remove(tmp);
				}
				if(!this.wmap.CharacterExists(tmp)) { // remove if not a valid character
					players.remove(tmp);
				} 
			}
		}
		
		this.sendInit(players);
	}
	
	public void joinGameWorld() {
		this.wmap.addNpc(this);
		if (this.wmap.gridExist(map)){
			try {
				this.grid = this.wmap.getGrid(this.map);
				this.area = this.grid.update(this);
			} catch (OutOfGridException e) {
				this.log.warning(this, e.getMessage() + " Somehow an npc is outside grid.");
			}
			sendInit(area.addMemberAndGetMembers(this));
		}
		else {
			ServerLogger.getInstance().logMessage(Level.SEVERE, this, "Failed to load grid for npc "+this.Uid +" map:" +this.map + ", disconnecting");
		}
	}
	
	/*
	 * item has been picked and this instance is no longer required
	 */
	public void leaveGameWorld() {
		this.area.rmMember(this);
		this.wmap.removeItem(Uid);
		synchronized(this.iniPackets){
			this.iniPackets.clear();
		}
		sendVanishToAll();
	}
	private void sendInit(List<Integer> sendList) {
		Iterator<Integer> siter = sendList.iterator();
		Integer tmp = null;
		while(siter.hasNext()) {
			tmp = siter.next();
			if (this.wmap.CharacterExists(tmp) && !wmap.getCharacterMap().get(tmp).isBot()){
				Character t = this.wmap.getCharacter(tmp);
				SocketChannel sc = t.GetChannel();
				if(sc!=null)
					ServerFacade.getInstance().getConnectionByChannel(sc).addWrite(this.npcSpawnPacket(t));
			}
		}
		synchronized(this.iniPackets){
			this.iniPackets.addAll(sendList);
		}
	}
	
	private void sendInit(Integer player){
		
		if (this.wmap.CharacterExists(player) && !wmap.getCharacterMap().get(player).isBot()){
			Character t = this.wmap.getCharacter(player);
			SocketChannel sc = t.GetChannel();
			if(sc!=null)
				ServerFacade.getInstance().getConnectionByChannel(sc).addWrite(this.npcSpawnPacket(t));
		}
		
	}
	
	private byte[] npcSpawnPacket(Character ch) {
		return NpcPackets.getNpcSpawnPacket(this, ch);
	}

	@Override
	public float getlastknownX() {
		// TODO Auto-generated method stub
		return this.location.getX();
	}

	@Override
	public float getlastknownY() {
		return this.location.getY();
	}

	@Override
	public SocketChannel GetChannel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short getState() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int getId(){
		return data.getId();
	}
	
	public String getName(){
		return "NPC";
	}

	@Override
	public void updateEnvironment(Integer player, boolean add) {
		
		synchronized(this.iniPackets){
			if (this.iniPackets.contains(player) && !add && !wmap.getCharacter(player).isBot()){
				this.iniPackets.remove(player);
				Character ch=this.wmap.getCharacter(player);
				ServerFacade.getInstance().addWriteByChannel(ch.GetChannel(), vanish(ch));
			}
			if (add && !this.iniPackets.contains(player)){
				this.iniPackets.add(player);
				this.sendInit(player);
			}
		}
		
	}
	
	public void sendVanishToAll(){
		
		Iterator<Map.Entry<Integer, Character>> iter = WMap.getInstance().getCharacterMap().entrySet().iterator();
		Character tmp;
		while(iter.hasNext()) {
			Map.Entry<Integer, Character> pairs = iter.next();
			tmp = pairs.getValue();
			if(!tmp.isBot())
			ServerFacade.getInstance().addWriteByChannel(tmp.GetChannel(), vanish(tmp));
		}
		
	}
	
	private byte[] vanish(Character ch){
		
		return NpcPackets.getNpcVanishPacket(this,ch);
		
	}
}
