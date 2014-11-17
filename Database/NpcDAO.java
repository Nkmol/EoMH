package Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import logging.ServerLogger;
import Configuration.ConfigurationManager;
import NPCs.NPCMaster;
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
			ResultSet rs = Queries.getNpcData(sqlConnection, npcID).executeQuery();
			if (rs.next()){
				int module=rs.getInt("module");
				int items[]=new int[60];
				for(int i=0;i<60;i++){
					items[i]=rs.getInt("item"+(i+1));
				}
				data = new NpcData(npcID,module,items);
			}
			else {
				log.warning(NpcDAO.class, "Database error: Unable to find mobID " + npcID);
				System.out.println("ERROR: Unable to find data for mobID " + npcID);
			}
			if (rs!=null)
				rs.close();
			
			
		}catch (SQLException e) {
			log.logMessage(Level.SEVERE, NpcDAO.class, e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e) {
			log.logMessage(Level.SEVERE, NpcDAO.class, e.getMessage());
			e.printStackTrace();
		}
		return data;
	}
	
	public static boolean doesNpcExist(int npcID){
		
		try{
			ResultSet rs = Queries.getNpcData(sqlConnection, npcID).executeQuery();
			return rs.next();
		}catch(Exception e){
			return false;
		}
		
	}
	
	public static void initNpcs(){
		System.out.println("Loading NPCs");
		int npcid,map,pool;
		float x,y;
		pool = ConfigurationManager.getConf("world").getIntVar("npcUIDPool");
		try{
			ResultSet rs = Queries.getNpcSpawns(sqlConnection).executeQuery();
			while(rs.next()){
				npcid = rs.getInt("npcType");
				map = rs.getInt("map");
				x = rs.getInt("spawnX");
				y = rs.getInt("spawnY");
				Npc npc=new Npc(getNpcData(npcid),map,pool,new Waypoint(x,y));
				NPCMaster.loadNPC(pool, npc);
				pool++;
			}
			//TEST ALL NPCS IN GM ISLAND1
			rs=Queries.getAllNpcData(sqlConnection).executeQuery();
			map=206;
			int i=0;
			while(rs.next()){
				npcid = rs.getInt("npcID");
				x=35222+(i%20)*10;
				y=49723+(i/20)*10;
				Npc npc=new Npc(getNpcData(npcid),map,pool,new Waypoint(x,y));
				NPCMaster.loadNPC(pool, npc);
				pool++;
				i++;
			}
			if (rs!=null)
				rs.close();
			System.out.println("Loading NPCs done");
		} catch (SQLException e){
			log.logMessage(Level.SEVERE, NpcDAO.class, e.getMessage());
			e.printStackTrace();
		} catch (Exception e){
			log.logMessage(Level.SEVERE, NpcDAO.class, e.getMessage());
			e.printStackTrace();
		}
	}

}
