package Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import logging.ServerLogger;
import Configuration.ConfigurationManager;
import Mob.MobMaster;
import NPCs.Npc;
import NPCs.NpcData;
//import World.WMap;
import World.Waypoint;

/*
 * MobDAO.class
 * Access the database and parses the data to the needed form for mobs
 */

public class NpcDAO {
	private static final Connection sqlConnection = new SQLconnection(false).getConnection();
	private static ServerLogger log = ServerLogger.getInstance();
	
	public static NpcData getNpcData(int npcID){
		NpcData data = null;
		try{
			//ResultSet rs = Queries.getNpcData(sqlConnection, npcID).executeQuery();
			//if (rs.next()){
				//data = new NpcData();
				//data.setId((rs.getInt("npcID"));
				
			//}
			//else {
				//log.warning(NpcDAO.class, "Database error: Unable to find mobID " + mobID);
				// System.out.println("ERROR: Unable to find data for mobID " + mobID);
			//}
			//if (rs!=null)
				//rs.close();
			
			
		//}catch (SQLException e) {
		//	log.logMessage(Level.SEVERE, NpcDAO.class, e.getMessage());
		//	e.printStackTrace();
		}
		catch (Exception e) {
			log.logMessage(Level.SEVERE, NpcDAO.class, e.getMessage());
			e.printStackTrace();
		}
		return data;
	}
	
	public static boolean doesNpcExist(int npcID){
		
		try{
			//ResultSet rs = Queries.getNpcData(sqlConnection, npcID).executeQuery();
			//return rs.next();
			return false;
		}catch(Exception e){
			return false;
		}
		
	}
	
	public static void initNpcs(){
		int npcid,map,pool;
		float x,y;
		pool = ConfigurationManager.getConf("world").getIntVar("mobUIDPool");
		try{
			ResultSet rs = Queries.getNpcSpawns(sqlConnection).executeQuery();
			while(rs.next()){
				npcid = rs.getInt("npcType");
				map = rs.getInt("map");
				x = rs.getInt("spawnX");
				y = rs.getInt("spawnY");
				new Npc(new NpcData(npcid),map,pool,new Waypoint(x,y));
				System.out.println("Creating NPC with id: "+npcid);
				pool++;
			}
			MobMaster.setPoolId(pool);
			if (rs!=null)
				rs.close();
		} catch (SQLException e){
			log.logMessage(Level.SEVERE, NpcDAO.class, e.getMessage());
		} catch (Exception e){
			log.logMessage(Level.SEVERE, NpcDAO.class, e.getMessage());
		}
	}

}
