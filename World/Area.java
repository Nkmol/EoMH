package World;

import java.util.*;
import java.nio.channels.*;

import Connections.Connection;
import ServerCore.ServerFacade;

import Player.Fightable;


// Area.class
// Keeps track of players within small area in side the game
// Each Area are members of some Grid, but Area is unaware of which
// Area is always a square as is its Grid

public class Area
{
  private Map<Integer, Location> members = Collections.synchronizedMap(new HashMap<Integer, Location>());
  private Map<Integer, Location> near = Collections.synchronizedMap(new HashMap<Integer, Location>());  
  private List<SocketChannel> playerList = Collections.synchronizedList(new ArrayList<SocketChannel>());
  
  private int gridx,gridy;
  private int uid;
  private int zone;
  private Grid grid;
  private WMap wmp;
  
  /** initializes Area
   parameter descriptions:
   gx, gy  -  (x,y) coordinates of the area within the grid that it belongs to
   mygrid  -  Pointer to the grid object
  */
  public Area(int gx, int gy, Grid mygrid)
  {
	  	this.wmp = WMap.getInstance();
        this.gridx = gx;
        this.gridy = gy;
        this.grid = mygrid;
        this.zone = 0;
  }
  // adds new member (player, mob, npc, dropped item) to the Area
  public void addMember(Location ob)
  {
    // System.out.println("Item added to area " + this.uid);
    this.members.put(ob.getuid(), ob);
	if (ob.GetChannel() != null) { 
	 this.playerList.add(ob.GetChannel());
	}
	this.updateNear(true, ob);
  }
  public List<Integer> addMemberAndGetMembers(Location loc){
	  List<Integer> ls = new ArrayList<Integer>();
	  ls.addAll(this.near.keySet());
	  this.addMember(loc);
	  return ls;
  }
  // add new near member to list
  protected void addNear(Location loc){
	  if (!this.near.containsValue(loc)){
          this.near.put(loc.getuid(), loc);
          this.notifyMembers(loc.getuid(), true);
	  }
  }
  // remove member from near list
  protected void rmNear(Location loc){
	  if (near.containsValue(loc)){
		  this.near.remove(loc.getuid());
		  this.notifyMembers(loc.getuid(), false);
      }
  }
  public void setuid(int id)
  {
    this.uid = id;
  }
  public int getuid()
  {
    return this.uid;
  }
  private void updateNear(boolean add, Location loc){
	  int area[] = new int[]{this.gridx -1, this.gridy -1};
		Area tmp = null;
		for (int a=0; a <3; a++){
			for (int b=0; b <3; b++){
				if (this.grid.areaExists(area)){
					tmp = this.grid.getArea(area);
					if (add)tmp.addNear(loc);
					else tmp.rmNear(loc);
					//tmp.notifyMembers();
					area[1]++;
				}
			}
			area[1] = this.gridy -1;
			area[0]++;
		}
	// this.notifyMembers(loc.getuid(), add);
  }
  // removes existing member from Area
  public void rmMember(Location loc)
  {
	if(members.containsKey(loc.getuid())) {
		members.remove(loc.getuid());
		this.updateNear(false, loc);
	}
  }
  // returns list containing all the players in this Area
  public List<SocketChannel> returnPlayerList() {
        return this.playerList;
  }
  // returns true if there are players in area, false if not
  public boolean hasPlayers()
  {
    return !playerList.isEmpty();
  }
  // returns true if Area has any members at all, false if not
  public boolean hasMembers()
  {
    return !this.members.isEmpty();
  }
  public Location getMember(int uid)
  {
    return (Location)members.get(uid);
  }
  public Fightable getFightableNear(int uid)
  {
	  if(near.get(uid) instanceof Fightable)
		  return (Fightable)near.get(uid);
	  return null;
  }
  //search enemy by name
  public Fightable getEnemyNear(String name)
  {
	  Collection<Location> values=near.values();
	  for(Iterator<Location> i=values.iterator();i.hasNext();){
		  Location loc=i.next();
		  if(loc instanceof Fightable && ((Fightable) loc).getName().equals(name) && ((Fightable) loc).getHp()!=0)
			  return (Fightable)loc;
	  }
	  return null;
  }
  // returns member map
  public Map<Integer, Location> getMemberMap()
  {
    return members;
  }
  public int[] getcoords()
  {
    int [] tmp = new int []{this.gridx, this.gridy};
    return tmp;
  }
  // return true if obj is member of this Area, else returns false
  public boolean isMember(Location obj)
  {
    return members.containsValue(obj);
  }
  // returns true if member whose uid is id is member of this Area, else returns false
  public boolean isMember(int id)
  {
    return members.containsKey(id);
  }
  
  public boolean isNear(Location obj)
  {
    return near.containsValue(obj);
  }
  
  public boolean isNear(int id)
  {
    return near.containsKey(id);
  }
  
  // send packet buf to all members except one specified by uid
  public void sendToMembers(int uid, byte[] buf)
  {
          SocketChannel tmp;
          Location loc;
          synchronized(this.near)
          {
                  Iterator<Map.Entry<Integer, Location>> iter = this.near.entrySet().iterator();
                  while(iter.hasNext()) {
                          Map.Entry<Integer, Location> pairs = iter.next();
                          loc = pairs.getValue();
                          tmp = loc.GetChannel();
                          
                          if (loc.getuid() != uid && tmp != null){
                              // write buf to player loc
                        	  Connection tmpc = ServerFacade.getInstance().getConnectionByChannel(tmp);
                              	if(tmpc != null) { 
                              		tmpc.addWrite(buf);
                              	}
                          }
                  }
                  
          }
  }
  
  // notify all nearby objects of a change in members
  private void notifyMembers(Integer pl, boolean add) {
	  synchronized(this.members){
		  //MobController cont;
		  Iterator<Map.Entry<Integer, Location>> iter = this.members.entrySet().iterator();
		  Location loc = null;
		  Integer locid = null;
		  while(iter.hasNext()) {
			  
			  Map.Entry<Integer, Location> pairs = iter.next();
			  loc = pairs.getValue();
			  locid = pairs.getKey();
			  if (this.wmp.CharacterExists(pl) && !pl.equals(locid)) {
				  loc.updateEnvironment(pl, add);
			  }
			  /*
			  if (this.wmp.mobExists(loc.getuid())){
				cont = this.wmp.GetMobController(pairs.getKey());
				//if (!cont.isActive()) this.grid.getThreadPool().executeProcess(cont);
			  }*/
		  }
	  }
  }
  public int getZone() {
	return this.zone;
  }
  public void setZone(int val) {
	  this.zone = val;
  }
  public void moveTo(Location loc, Area t) {
	if (this.members.containsValue(loc)){
		int coords[] = t.getcoords();
		int area[] = new int[]{this.gridx -1, this.gridy -1};
		Area tmp = null;
		for (int a=0; a <3; a++){
			for (int b=0; b <3; b++){
				if (this.grid.areaExists(area)){
					tmp = this.grid.getArea(area);
					if (WMap.distance(coords[0], area[0]) > 1){
						tmp.rmNear(loc);
					}
					if (WMap.distance(coords[1], area[1]) > 1){
						tmp.rmNear(loc);
					}
					area[1]++;
				}
			}
			area[1] = this.gridy -1;
			area[0]++;
		}
		this.members.remove(loc.getuid());
	}
	
  }
  
}
