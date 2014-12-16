package World;

import item.DroppedItem;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import Mob.Mob;
import Mob.MobController;
import NPCs.Npc;
import Player.Character;
import logging.ServerLogger;
// WMap.class
// singleton resource that keeps track of all the grids, connected Characters and mobs inside the game


public class WMap
{
	private final Map<Integer, Grid> grids = Collections.synchronizedMap(new HashMap<Integer, Grid>());	
    private final Map<Integer, Character>  Characters = Collections.synchronizedMap(new HashMap<Integer, Character>());	
	private Map<Integer, Mob> mobs = Collections.synchronizedMap(new HashMap<Integer, Mob>());
	private Map<Integer, Npc> npcs = Collections.synchronizedMap(new HashMap<Integer, Npc>());
	private Map<Integer, DroppedItem> items = Collections.synchronizedMap(new HashMap<Integer, DroppedItem>()); 
	private CopyOnWriteArrayList<Character> vendingList = new CopyOnWriteArrayList<Character>(); 
	private	static WMap instance; 
	private int itemUIDPool = 600000;
	
	
	private WMap(){
		
	}
	public synchronized static WMap getInstance(){
		if (instance == null){
			instance = new WMap();
		}
		return instance;
	}
	
	public CopyOnWriteArrayList<Character> getVendingList() {
		return vendingList;
	}
	
	public boolean addVendorList(Character ch) {
		return vendingList.add(ch);
	}
	
	public boolean removeVendorList(Character ch) {
		return vendingList.remove(ch);
	}
	
	// add mob that has been created to the list
	public void AddMob(int uid, Mob mb) {
		this.mobs.put(uid, mb);
	}
	//remove mob
	public void removeMob(int uid){
		this.mobs.remove(uid);
	}
	// returns Mob identified by uid
	public MobController GetMobController(int uid) {
		return this.mobs.get(uid).getControl();
	}
	public boolean mobExists(int uid){
		return this.mobs.containsKey(uid);
	}
	public Mob getMob(int uid){
		return this.mobs.get(uid);
	}
	// add grid that has been created to the list
	public void addGrid(Grid g){
		ServerLogger.getInstance().info(this, "Added grid " + g.getuid() + " To wmap");
		this.grids.put(g.getuid(), g);
	}
	// returns true if Grid identified by uid exists in the WMap, false if not
	public boolean gridExist(int uid)
	{
		return this.grids.containsKey(uid);
	}
	// adds new Character to the list
	public void addCharacter(Character obj){
	 //System.out.println("New Character "+ obj.getuid()+" added to list");
		this.Characters.put(obj.getuid(), obj);
	}
	// returns Character identified by uid
	public Character getCharacter(int uid){
		return this.Characters.get(uid);
	}
	//search character by name
	public Character getCharacter(String name)
	 {
		  Collection<Character> values=Characters.values();
		  for(Iterator<Character> i=values.iterator();i.hasNext();){
			  Character ch=i.next();
			  if(ch.getName().equals(name))
				  return ch;
		  }
		  return null;
	 }
	// return true if Character obj is in the list, otherwise returns false
	public boolean CharacterExists(Character obj)
    {
		return this.Characters.containsValue(obj);
    }
	// returns true if Character with UID id exists in the list, otherwise returns false
    public boolean CharacterExists(int id)
    {
    	return this.Characters.containsKey(id);
    } 
	// removes Character identified by uid from the list
	public void rmCharacter(int uid)
	{
		this.Characters.remove(uid);
	}
	// returns Map containing all the Characters currently in the game
	public Map<Integer, Character> getCharacterMap()
	{ 
		return this.Characters;
	}	
	// returns the Grid designated by the uid
	public Grid getGrid(int uid) throws OutOfGridException
	{
		if(!this.grids.containsKey(uid)) throw new OutOfGridException();
		return this.grids.get(uid);
	}
	// return map containing all the grids
	public Map<Integer, Grid> returnMap()
	{
		return this.grids;
	}
	// calculates in-game distance between point a and b
	public static float distance(float a, float b)
	{
	   return (float) Math.sqrt(Math.pow( (double)(a - b), 2));
	}
	public static int distance(int a, int b)
	{
	   return (int)Math.sqrt(Math.pow( (double)(a - b), 2));
	}
	// calculates in-game distance between coordinates (tx,ty) and (dx,dy)
	public static float distance(float tx, float ty, float dx, float dy)
	{
	  return ((float) (Math.sqrt( (Math.pow( (double)(tx - dx), (double)2 ) + Math.pow( (double)(ty - dy), (double)2) )) ));	  
	}
	public boolean itemExist(int uid){
		return this.items.containsKey(uid);
	}
	public boolean isUIDFree(int uid){
		if (!this.CharacterExists(uid) && !this.mobExists(uid) && !this.itemExist(uid)){
			return true;
		}
		return false;
	}
	// add dopped item to be tracked
	public boolean addItem(DroppedItem it){
		if (!this.itemExist(it.getuid())){
			this.items.put(it.getuid(), it);
			return true;
		}
		return false;
	}
	// get dropped item instance
	public DroppedItem getItem(int uid){
		return this.items.get(uid);
	}
	
	// free uids for all ???? profit
	public int getFreeUID() {
		int ret = this.itemUIDPool;
		this.itemUIDPool++;
		return ret;
	}
	// remove dropped item
	public void removeItem(Integer uid) {
		this.items.remove(uid);
		
	}
	
	public boolean addNpc(Npc npc){
		if (!this.npcExists(npc.getuid())){
			this.npcs.put(npc.getuid(), npc);
			return true;
		}
		return false;
	}
	
	public boolean npcExists(int uid){
		return this.npcs.containsKey(uid);
	}
	
	public Npc getNpc(int uid){
		return this.npcs.get(uid);
	}
		
	public void removeNpc(Integer uid) {
		this.npcs.remove(uid);
			
	}
	
}
 




















