package Mob;

import Configuration.ConfigurationManager;
import Database.MobDAO;
import World.Waypoint;

public class MobMaster {

	private static int poolId=ConfigurationManager.getConf("world").getIntVar("mobUIDPool");

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
		
		int []data = new int[]{map,(int)w.getX(),(int)w.getY(),radius,10,0,0};
		System.out.println("Creating controller with x: " + data[1] + " y: " + data[2]);
		new MobController(mobid, amount, poolId, data, isTemp, onlyStars, expFactor);
		/*MobController run = new MobController(mobid, amount, poolId, data, isTemp, onlyStars, expFactor);
		try{
		WMap.getInstance().getGrid(data[0]).getThreadPool().executeProcess(run);
		}catch (Exception e){
			e.printStackTrace();
		}*/
		poolId += amount;
		
	}
	
	
}
