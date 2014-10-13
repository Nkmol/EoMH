package item;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import logging.ServerLogger;
import Player.Character;
import ServerCore.ServerFacade;
import Tools.BitTools;
import World.Grid;
import World.Area;
import World.Location;
import World.OutOfGridException;
import World.WMap;
import World.Waypoint;

public class DroppedItem implements Location{
	private WMap wmap = WMap.getInstance();
	private List<Integer> iniPackets = new ArrayList<Integer>();
	private int Uid; // unique id for grid n such
	private Waypoint location;
	private ItemFrame item;
	private int map;
	private Grid grid;
	private Area area;
	private ServerLogger log = ServerLogger.getInstance();
	private int amount;
	private Timer timer;
	
	DroppedItem(ItemFrame it,int map, int uid, Waypoint loc, int amount){
		this.Uid = uid;
		this.item = it;
		this.map = map;
		this.location = loc;
		this.amount=amount;
		timer=new Timer();
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
		
		this.sendInit(players);
	}
	public void joinGameWorld() {
		timer.schedule(new TimerTask() {
			  @Override
			  public void run() {
				  DroppedItem.this.leaveGameWorld();
				  cancel();
			  }
		}, 300000);
		this.wmap.addItem(this);
		if (this.wmap.gridExist(map)){
			try {
				this.grid = this.wmap.getGrid(this.map);
				this.area = this.grid.update(this);
			} catch (OutOfGridException e) {
				this.log.warning(this, e.getMessage() + " Somehow, someone, somewhere dropped an item outside grid.");
			}
			sendInit(area.addMemberAndGetMembers(this));
		}
		else {
			ServerLogger.getInstance().logMessage(Level.SEVERE, this, "Failed to load grid for item "+this.Uid +" map:" +this.map + ", disconnecting");
		}
	}
	
	/*
	 * item has been picked and this instance is no longer required
	 */
	public void leaveGameWorld() {
		timer.cancel();
		this.area.rmMember(this);
		this.wmap.removeItem(Uid);
		this.iniPackets.clear();
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
					ServerFacade.getInstance().getConnectionByChannel(sc).addWrite(this.itemSpawnPacket());
				// if (this.vanish.containsKey(tmp)) this.vanish.remove(tmp);
			}
		}
		this.iniPackets.addAll(sendList);
	}
	
	private void sendInit(Integer player){
		
		if (this.wmap.CharacterExists(player) && !wmap.getCharacterMap().get(player).isBot()){
			Character t = this.wmap.getCharacter(player);
			SocketChannel sc = t.GetChannel();
			if(sc!=null)
				ServerFacade.getInstance().getConnectionByChannel(sc).addWrite(this.itemSpawnPacket());
		}
		
	}
	
	public byte[] itemSpawnPacket() {
		byte[] item = new byte[56];
		byte[] spawnX = BitTools.floatToByteArray(this.location.getX());
		byte[] spawnY = BitTools.floatToByteArray(this.location.getY());
		byte[] itid = BitTools.intToByteArray(this.item.getId());
		byte[] chid = BitTools.intToByteArray(this.getuid());
		byte[] amount = BitTools.intToByteArray(this.amount);
		
		item[0] = (byte)item.length;
		item[4] = (byte)0x05;
		item[6] = (byte)0x0e;
		
		for(int i=0;i<4;i++) {
			item[36+i] = spawnX[i];
			item[40+i] = spawnY[i];
			item[20+i] = itid[i];
			item[32+i] = chid[i]; 
			item[28+i] = amount[i];
		}
		
		return item;
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
	public ItemFrame getItem(){
		return this.item;
	}
	
	public int getAmount(){
		return amount;
	}

	@Override
	public void updateEnvironment(Integer player, boolean add) {
		
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
		
		byte[] chid = BitTools.intToByteArray(ch.getCharID());
		byte[] uid=BitTools.intToByteArray(Uid);
		
		byte[] iv = new byte[20];
		iv[0] = (byte)iv.length;
		iv[4] = (byte)0x05;
		iv[6] = (byte)0x0F;
		iv[8] = (byte)0x01;
		
		
		for(int i=0;i<4;i++) {
			iv[12+i] = chid[i];
			iv[16+i] = uid[i];
		}
		
		return iv;
		
	}
}
