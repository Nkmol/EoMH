package Mob;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import Configuration.ConfigurationManager;
import Database.MobDAO;
import World.WMap;
import World.Waypoint;

public class MobMaster {

	private static int poolId=ConfigurationManager.getConf("world").getIntVar("mobUIDPool");
	private static List<Mob> tempMobs=Collections.synchronizedList(new LinkedList<Mob>());
	private static List<Mobpuzzle> puzzles;
	private static List<String> names;
	private static List<String> sentences;
	
	public static void initPuzzles(LinkedList<Mobpuzzle> puzzles){
		MobMaster.puzzles=puzzles;
	}
	
	public static void initNames(LinkedList<String> names){
		MobMaster.names=names;
	}
	
	public static void initSentences(LinkedList<String> sentences){
		MobMaster.sentences=sentences;
	}

	public static int getPoolId() {
		return poolId;
	}

	public static void setPoolId(int poolId) {
		MobMaster.poolId = poolId;
	}
	
	public static boolean doesMobExist(int mobID){
		return MobDAO.doesMobExist(mobID);
	}
	
	public static void spawnMob(int mobid, int amount, int map, Waypoint w, int radius, boolean isTemp, boolean onlyStars, float expFactor){
		
		int []data = new int[]{map,(int)w.getX()-radius/2,(int)w.getY()-radius/2,radius,radius,10,0,0};
		System.out.println("Creating controller with x: " + data[1] + " y: " + data[2]);
		MobController run = new MobController(mobid, amount, poolId, data, isTemp, onlyStars, expFactor);
		try{
			WMap.getInstance().getGrid(data[0]).getThreadPool().executeProcess(run);
		}catch (Exception e){
			e.printStackTrace();
		}
		poolId += amount;
		
	}
	
	public static void addTempMob(Mob mob){
		synchronized(tempMobs){
			tempMobs.add(mob);
		}
	}
	
	public static void deleteTempMob(Mob mob){
		synchronized(tempMobs){
			tempMobs.remove(mob);
		}
	}
	
	public static void killAllTempMobs(int uid){
		synchronized(tempMobs){
			for(Iterator<Mob>it=tempMobs.iterator();it.hasNext();){
				try{
					it.next().recDamage(uid, 999999999);
				}catch(Exception e){e.printStackTrace();}
			}
		}
	}
	
	public static void deleteAllTempMobs(){
		synchronized(tempMobs){
			for(Iterator<Mob>it=tempMobs.iterator();it.hasNext();){
				try{
					it.next().dieByDespawn();
				}catch(Exception e){e.printStackTrace();}
			}
		}
	}
	
	public static Mobpuzzle getRandomMobpuzzle(){
		return puzzles.get((int)(Math.random()*puzzles.size()));
	}
	
	public static String getRandomName(int uid){
		return names.get(uid%names.size());
	}
	
	public static String getRandomSentence(){
		return sentences.get((int)(Math.random()*sentences.size()));
	}
	
	
}
