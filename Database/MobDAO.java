package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;

import logging.ServerLogger;
import Configuration.ConfigurationManager;
import Mob.MobController;
import Mob.MobData;
import Mob.MobMaster;
import Mob.Mobpuzzle;
import Skills.CastableSkill;
import Skills.SkillMaster;
//import World.WMap;

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
				data.setMinatk((int)(rs.getInt("minatk")*1+20));
				data.setMaxatk((int)(rs.getInt("maxatk")*1+20));
				data.setDefence((int)((rs.getInt("defence")+10)*2));
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
		int []data = new int[]{0,0,0,0,0,0,0,0};
		pool = ConfigurationManager.getConf("world").getIntVar("mobUIDPool");
		try{
			ResultSet rs = Queries.getMobs(sqlConnection).executeQuery();
			while(rs.next()){
				mobid = rs.getInt("mobType");
				count = rs.getInt("spawnCount");
				data[0] = rs.getInt("map");
				data[1] = rs.getInt("spawnX");
				data[2] = rs.getInt("spawnY");
				data[3] = rs.getInt("spawnWidth");
				data[4] = rs.getInt("spawnHeight");
				//old: spawnRadius:
				//skyzone has creepy spawns
				//if(data[0]==9)
				//	data[3]+=150;
				data[5] = rs.getInt("waypointCount");
				data[6] = rs.getInt("waypointHop");
				data[7] = rs.getInt("respawnTime");
				System.out.println("Creating controller with x: " + data[1] + " y: " + data[2]);
				//MobController run = 
				new MobController(mobid, count, pool, data, false, false, 1f);
				//WMap.getInstance().getGrid(data[0]).getThreadPool().executeProcess(run);
				pool += count;
			}
			MobMaster.setPoolId(pool);
			if (rs!=null)
				rs.close();
		} catch (SQLException e){
			log.logMessage(Level.SEVERE, MobDAO.class, e.getMessage());
		} catch (Exception e){
			e.printStackTrace();
			log.logMessage(Level.SEVERE, MobDAO.class, e.getMessage());
		}
	}
	
	public static void loadMobpuzzles() {
		LinkedList<Mobpuzzle> puzzles=null;
		try{
			ResultSet rs=Queries.getAllPuzzlemobs(sqlConnection).executeQuery();
			puzzles=new LinkedList<>();
			while(rs.next()){
				Mobpuzzle puzzle=new Mobpuzzle(rs.getString("question"), rs.getString("answer"), rs.getInt("type"),
						rs.getFloat("coinrate"), rs.getFloat("exprate"), rs.getFloat("droprate"), rs.getInt("bonusdrop"));
				puzzles.add(puzzle);
			}
			rs.close();
			MobMaster.initPuzzles(puzzles);
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean deleteAllMobpuzzles() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.deleteAllPuzzlemobs(sqlConnection);
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			e.printStackTrace();
			b = false;
		}
		catch (Exception e) {
			e.printStackTrace();
			b = false;
		}
		return b;
	}
	
	public static void loadRandomnames() {
		LinkedList<String> names=null;
		try{
			ResultSet rs=Queries.getAllRandomnames(sqlConnection).executeQuery();
			names=new LinkedList<>();
			while(rs.next()){
				String name=(rs.getString("name"));
				names.add(name);
			}
			rs.close();
			MobMaster.initNames(names);
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean deleteAllRandomnames() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.deleteAllRandomnames(sqlConnection);
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			e.printStackTrace();
			b = false;
		}
		catch (Exception e) {
			e.printStackTrace();
			b = false;
		}
		return b;
	}
	
	public static void loadRandomsentences() {
		LinkedList<String> sentences=null;
		try{
			ResultSet rs=Queries.getAllRandomsentences(sqlConnection).executeQuery();
			sentences=new LinkedList<>();
			while(rs.next()){
				String sentence=(rs.getString("sentence"));
				sentences.add(sentence);
			}
			rs.close();
			MobMaster.initSentences(sentences);
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean deleteAllRandomsentences() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.deleteAllRandomsentences(sqlConnection);
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			e.printStackTrace();
			b = false;
		}
		catch (Exception e) {
			e.printStackTrace();
			b = false;
		}
		return b;
	}

}
