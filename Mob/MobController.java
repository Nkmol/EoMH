package Mob;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import World.OutOfGridException;
import World.WMap;
import World.Waypoint;
import logging.ServerLogger;
import Database.MobDAO;

public class MobController implements Runnable {
	private Map<Integer, Mob> mobs = Collections.synchronizedMap(new HashMap<Integer, Mob>());
	private int mobID, mobCount, uidPool;
	private int map, spawnx, spawny, spawnRadius,wpCount,wpHop, respawnTime;
	private volatile boolean active;
	private MobData data;
	private ServerLogger log = ServerLogger.getInstance();
	private MobTickRate ticks = new MobTickRate(10);
	private LinkedBlockingQueue<Mob> activeMobs = new LinkedBlockingQueue<Mob>();
	private boolean isTemp;
	private boolean onlyStars;
	private float expFactor;
	
	
	public MobController(int ID, int Count, int Pool, int []data, boolean isTemp, boolean onlyStars, float expFactor) {
		this.mobID = ID;
		this.mobCount = Count;
		this.uidPool = Pool;
		this.map = data[0];
		this.spawnx = data[1];
		this.spawny = data[2];
		this.spawnRadius = data[3];
		this.wpCount = data[4];
		this.wpHop = data[5];
		this.respawnTime = data[6];
		this.isTemp=isTemp;
		this.onlyStars=onlyStars;
		this.expFactor=expFactor;
		this.setActive(false);
		
		this.init();
	}
	private void init(){
		this.data = MobDAO.getMobData(mobID);
		this.data.setGridID(map);
		this.data.setWaypointCount(wpCount);
		this.data.setWaypointHop(wpHop);
		this.data.setRespawnTime(respawnTime);
		Random r = new Random();
		Waypoint spawn;
		Mob mob = null;
		int uid = uidPool;
		int x,y;
		this.log.info(this, "Creating mob objects ");
		for (int i=0; i < this.mobCount; i++){
			// randomize spawn coordinates 
			x = this.spawnx + r.nextInt(2*this.spawnRadius) - this.spawnRadius;
			y = this.spawny + r.nextInt(2*this.spawnRadius) - this.spawnRadius;
			spawn = new Waypoint(x,y);
			mob = new Mob(this.mobID, uid, spawn, this);
			try {
				mob.run();
			} catch (OutOfGridException e) {
				this.log.severe(this, e.getMessage() + " Removing mob: " + uid + " in map: " + this.map);
				continue;
			}
			mobs.put(i, mob);
			this.ticks.addMob(mob);
			uid++;
		}
		this.log.info(this, this.mobCount + " mobs succesfully created");
	}
	public boolean isActive() {
		return active;
	}
	private void setActive(boolean active) {
		this.active = active;
	}
	
	public synchronized void run(){
		this.setActive(true);
		this.log.info(this, "Controller active in thread " + Thread.currentThread());
		
		while(this.active) {
			if(!this.activeMobs.isEmpty()) {
				List<Mob> cycle = this.ticks.getNextMobs();
				LinkedList<Mob> deletedMobs=new LinkedList<Mob>();
				Iterator<Mob> it = cycle.iterator();
				Mob m = null;
				while(it.hasNext()) {
					m = it.next();
					try {
						if(!m.isDeleted()){
							m.run();
						}else{
							deletedMobs.add(m);
							//System.out.println("Removed mob: "+m.getuid());
							//deleteMob(m);
							//it.remove();
						}
							
					} catch (OutOfGridException e) {
						deletedMobs.add(m);
						this.log.severe(this, e.getMessage() + " Removing mob: " + m.getuid() + " in map: " + this.map);
						//deleteMob(m);
						//it.remove();
					}
				}
				for(int i=0;i<deletedMobs.size();i++){
					deleteMob(deletedMobs.get(i));
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				this.setActive(false);
			}
		}
		this.log.info(this, "Controller deactivated in thread " + Thread.currentThread());
	}
	
	protected MobData getData(){
		return this.data;
	}
	
	protected void register(Mob mob) {
		this.activeMobs.offer(mob);
	}
	
	protected void unregister(Mob mob) {
		this.activeMobs.remove(mob);
	}
	
	public synchronized void deleteMob(Mob mob){
		this.mobs.remove(mob.getuid());
		unregister(mob);
		this.ticks.deleteMob(mob);
		WMap.getInstance().removeMob(mob.getuid());
		System.out.println("Removed mob: "+mob.getuid());
	}
	
	public boolean isTemp() {
		return isTemp;
	}
	
	public boolean onlyStars(){
		return onlyStars;
	}
	
	public float expFactor(){
		return expFactor;
	}
	
	public int getSpawnx(){
		return spawnx;
	}
	
	public int getSpawny(){
		return spawny;
	}
	
	public int getSpawnRadius(){
		return spawnRadius;
	}
	
}
