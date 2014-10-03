package Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import logging.ServerLogger;
import Configuration.ConfigurationManager;
import Mob.MobController;
import Mob.MobData;
import Mob.MobMaster;
import Skills.CastableSkill;
import Skills.SkillMaster;
import World.WMap;

/*
 * MobDAO.class
 * Access the database and parses the data to the needed form for mobs
 */

public class MobDAO {
	private static final Connection sqlConnection = new SQLconnection(false).getConnection();
	private static ServerLogger log = ServerLogger.getInstance();
	
	public static MobData getMobData(int mobID){
		MobData data = null;
		try{
			ResultSet rs = Queries.getMobData(sqlConnection, mobID).executeQuery();
			if (rs.next()){
				data = new MobData();
				data.setMobID(rs.getInt("mobID"));
				data.setLvl(rs.getInt("lvl"));
				int amount=0;
				for(int i=0;i<3;i++){
					if(rs.getInt("skill"+(i+1))!=0)
						amount++;
				}
				CastableSkill skills[]=new CastableSkill[amount];
				for(int i=0;i<amount;i++){
					skills[i]=(CastableSkill)SkillMaster.getSkill(rs.getInt("skill"+(i+1)));
				}
				data.setSkills(skills);
				data.setMinatk((int)(rs.getInt("minatk")*1.4+30));
				data.setMaxatk((int)(rs.getInt("maxatk")*1.4+30));
				data.setDefence((int)((rs.getInt("defence")+10)*3));
				data.setMaxhp(rs.getInt("maxhp")+10);
				data.setAtksuc(rs.getInt("atksuc"));
				data.setDefsuc(rs.getInt("defsuc"));
				data.setCritsuc((data.getAtksuc()-500)/2);
				data.setBasexp(rs.getLong("basexp"));
				data.setCoins(rs.getInt("coins"));
				data.setBasefame(rs.getInt("basefame"));
				data.setAggroRange(rs.getInt("aggroRange"));
				data.setAttackRange(rs.getInt("attackRange"));
				data.setFollowRange(rs.getInt("followRange"));
				data.setMoveRange(rs.getInt("moveRange"));
				amount=0;
				for(int i=0;i<80;i++){
					if(rs.getInt("drop"+i)!=0){
						amount++;
					}
				}
				int[] drops=new int[amount];
				float[] dropchances= new float[amount];
				for(int i=0;i<amount;i++){
					drops[i]=rs.getInt("drop"+i);
					dropchances[i]=rs.getFloat("dropchance"+i);
				}
				data.setDrops(drops);
				data.setDropchances(dropchances);
				
			}
			else {
				log.warning(MobDAO.class, "Database error: Unable to find mobID " + mobID);
				// System.out.println("ERROR: Unable to find data for mobID " + mobID);
			}
			if (rs!=null)
				rs.close();
			
			
		}catch (SQLException e) {
			log.logMessage(Level.SEVERE, MobDAO.class, e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e) {
			log.logMessage(Level.SEVERE, MobDAO.class, e.getMessage());
			e.printStackTrace();
		}
		return data;
	}
	
	public static boolean doesMobExist(int mobID){
		
		try{
			ResultSet rs = Queries.getMobData(sqlConnection, mobID).executeQuery();
			return rs.next();
		}catch(Exception e){
			return false;
		}
		
	}
	
	public static void initMobs(){
		int mobid, count, pool;
		int []data = new int[]{0,0,0,0,0,0,0};
		pool = ConfigurationManager.getConf("world").getIntVar("mobUIDPool");
		try{
			ResultSet rs = Queries.getMobs(sqlConnection).executeQuery();
			while(rs.next()){
				mobid = rs.getInt("mobType");
				count = rs.getInt("spawnCount");
				data[0] = rs.getInt("map");
				data[1] = rs.getInt("spawnX");
				data[2] = rs.getInt("spawnY");
				data[3] = rs.getInt("spawnRadius");
				data[4] = rs.getInt("waypointCount");
				data[5] = rs.getInt("waypointHop");
				data[6] = rs.getInt("respawnTime");
				System.out.println("Creating controller with x: " + data[1] + " y: " + data[2]);
				new MobController(mobid, count, pool, data, false, false, 1f);
				//MobController run = new MobController(mobid, count, pool, data, false, false, 1f);
				//WMap.getInstance().getGrid(data[0]).getThreadPool().executeProcess(run);
				pool += count;
			}
			MobMaster.setPoolId(pool);
			if (rs!=null)
				rs.close();
		} catch (SQLException e){
			log.logMessage(Level.SEVERE, MobDAO.class, e.getMessage());
		} catch (Exception e){
			log.logMessage(Level.SEVERE, MobDAO.class, e.getMessage());
		}
	}

}
