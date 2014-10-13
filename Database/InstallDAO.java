package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import logging.ServerLogger;

public class InstallDAO {
	private ServerLogger log = ServerLogger.getInstance();
	private static InstallDAO instance;
	
	
	private InstallDAO(){
		this.log = ServerLogger.getInstance();
	}
	public static InstallDAO getInstance(){
		if (instance == null){
			instance = new InstallDAO();
		}
		return instance;
	}
	
	public boolean isEmpty(){
		boolean b = true;
		try{
			ResultSet rs=Queries.showGrants(new SQLconnection(false).getConnection()).executeQuery();
			if (rs!=null)
				rs.close();
			// b = rs.next();
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	public boolean tableExists(String table){
		boolean b = false;
		try{
			ResultSet rs = Queries.showTables(new SQLconnection(false).getConnection()).executeQuery();
			while( rs.next()){
				if (table.contentEquals(rs.getString(1))) b = true;
			}
			if (rs!=null)
				rs.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	public boolean dropTable(int n){
		boolean b = false;
		try{
			PreparedStatement ps;
			if (n == 0){
				ps=Queries.dropAccountTable(new SQLconnection().getConnection());
				b = ps.execute();
				ps.close();
			}
			else if (n == 1){
				ps = Queries.dropCharacterTable(new SQLconnection().getConnection());
				b = ps.execute();
				ps.close();
			}
			else if (n == 2){
				ps = Queries.dropItemsTable(new SQLconnection().getConnection());
				b = ps.execute();
				ps.close();
			}
			else if (n == 3){
				ps = Queries.dropMapTable(new SQLconnection().getConnection());
				b = ps.execute();
				ps.close();
			}
			else if (n == 4){
				ps = Queries.dropMobsTable(new SQLconnection().getConnection());
				b = ps.execute();
				ps.close();
			}
			else if (n == 5){
				ps = Queries.dropMobDataTable(new SQLconnection().getConnection());
				b = ps.execute();
				ps.close();
			}
			else if (n == 6){
				ps = Queries.dropEquipmentTable(new SQLconnection().getConnection());
				b = ps.execute();
				ps.close();
			}
			else if (n == 7){
				ps = Queries.dropInventoryTable(new SQLconnection().getConnection());
				b = ps.execute();
				ps.close();
			}
			else if (n == 8){
				ps = Queries.dropSkillsTable(new SQLconnection().getConnection());
				b = ps.execute();
				ps.close();
			}
			else if (n == 9){
				ps = Queries.dropCharSkillsTable(new SQLconnection().getConnection());
				b = ps.execute();
				ps.close();
			}
			else if (n == 10){
				ps = Queries.dropCharSkillbarsTable(new SQLconnection().getConnection());
				b = ps.execute();
				ps.close();
			}
			else if (n == 11){
				ps = Queries.dropLvlsTable(new SQLconnection().getConnection());
				b = ps.execute();
				ps.close();
			}
			else if (n == 12){
				ps = Queries.dropGamemasterTable(new SQLconnection().getConnection());
				b = ps.execute();
				ps.close();
			}
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	public boolean createAccountTable() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.createAccountTable(new SQLconnection().getConnection());
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());

			b = false;
		}
		return b;
		
	}
	public boolean createCharacterTable() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.createCharactersTable(new SQLconnection().getConnection());
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
		
	}
	public boolean createMapTable() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.createMapTable(new SQLconnection().getConnection());
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			log.severe(this, "Database error: " +e.getMessage());
			// e.printStackTrace();
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	public boolean createMobDataTable() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.createMobDataTable(new SQLconnection().getConnection());
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	public boolean createMobsTable() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.createMobsTable(new SQLconnection().getConnection());
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	
	public boolean createNpcSpawnsTable() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.createNpcSpawnsTable(new SQLconnection().getConnection());
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	
	public boolean createItemsTable() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.createItemsTable(new SQLconnection().getConnection());
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	public boolean createSkillsTable() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.createSkillsTable(new SQLconnection().getConnection());
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	
	public boolean addMap(Connection sql, int id, String name, int gridsize, int areasize, int x, int y, int pool) {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.addMap(sql, id, name, gridsize, areasize,x,y,pool);
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	
	public boolean addItem(Connection sql,int itemid,int baseid,int category,int againsttype,int bonustype,int typedmg,int bonustypedmg,float atkrange,
			int price,int isconsumable,int ispermanent,int equipslot,int width,int height,int minlvl,int maxlvl,int reqstr,int reqdex,int reqvit,int reqint,int reqagi,
			int warusable,int sinusable,int mageusable,int monkusable,int faction,int upgradelvl,int str,int bonusstr,int dex,int bonusdex,int vit,int bonusvit,
			int intl,int bonusintl,int agi,int bonusagi,int healhp,int life,int bonuslife,int healmana,int mana,int bonusmana,int stam,int bonusstam,float atkscs,float bonusatkscs,float defscs,
			float bonusdefscs,float critchance,float bonuscritchance,int critdmg,int bonuscritdmg,int mindmg,int maxdmg,int offpower,int bonusoffpower,
			int defpower,int bonusdefpower,int pvpdmginc,int timetoexpire,int seteffectid,int amountsetpieces,int movespeed,int buffid1, int bufftime1, int buffvalue1, int buffid2, int bufftime2, int buffvalue2) {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.addItem(sql,
					itemid,baseid,category,againsttype,bonustype,typedmg,bonustypedmg,atkrange,
					price,isconsumable,ispermanent,equipslot,width,height,minlvl,maxlvl,reqstr,reqdex,reqvit,reqint,reqagi,
					warusable,sinusable,mageusable,monkusable,faction,upgradelvl,str,bonusstr,dex,bonusdex,vit,bonusvit,
					intl,bonusintl,agi,bonusagi,healhp,life,bonuslife,healmana,mana,bonusmana,stam,bonusstam,atkscs,bonusatkscs,defscs,
					bonusdefscs,critchance,bonuscritchance,critdmg,bonuscritdmg,mindmg,maxdmg,offpower,bonusoffpower,
					defpower,bonusdefpower,pvpdmginc,timetoexpire,seteffectid,amountsetpieces,movespeed,buffid1,bufftime1,buffvalue1,buffid2,bufftime2,buffvalue2);
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	
	public boolean addSkill(Connection sql,int skillid,int skillgroup,int chclass,int stage,int effectOnWep,int reqSkill1,int reqSkill2,int reqSkill3,int skillpoints,int nextSkillLawful,
			int nextSkillEvil,int lvl,int specificType,int moreSpecificType,int normalDmgFont,int mobDmgFont,int targets,int generalType,int faction,
			int needsWepToCast,int ultiSetId,int isCastable,int isSpecialCast,int healCost,int manaCost,int staminaCost,int dmg,float speed,int effAmount,
			int effId1,int effDuration1,int effValue1,int effId2,int effDuration2,int effValue2,int effId3,int effDuration3,int effValue3) {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.addSkill(sql,
					skillid,skillgroup,chclass,stage,effectOnWep,reqSkill1,reqSkill2,reqSkill3,skillpoints,nextSkillLawful,
					nextSkillEvil,lvl,specificType,moreSpecificType,normalDmgFont,mobDmgFont,targets,generalType,faction,
					needsWepToCast,ultiSetId,isCastable,isSpecialCast,healCost,manaCost,staminaCost,dmg,speed,effAmount,
					effId1,effDuration1,effValue1,effId2,effDuration2,effValue2,effId3,effDuration3,effValue3);
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	
	public boolean addLvl(Connection sql,int lvl,long exp) {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.addLvl(sql,lvl,exp);
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	
	public boolean addGamemasterRank(Connection sql,int rank,String prename,boolean gotGMname,int commandpower,
			int allocateGMrank,boolean isPlayer) {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.addGamemasterRank(sql,rank,prename,gotGMname,commandpower,allocateGMrank,isPlayer);
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	
	public boolean CreateAccount(int accountID, String ip, String username, String password, int flags) {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.CreateUserAccount(new SQLconnection().getConnection(), ip, username, password, flags);
			ps.execute();
			ps.close();
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	public boolean createMobDataEntry(Connection sql, int id, int lvl, int skill1, int skill2, int skill3, int minatk, int maxatk, int deff, int hp, int atksuc, int defsuc, long basexp, int coins, int basefame, int aggro, int attrange, int follow,int move,int[] drops,float[] dropchances) {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.createMobDataEntry(sql, id, lvl, minatk, maxatk, deff, skill1, skill2, skill3, hp, atksuc, defsuc, basexp, coins, basefame, aggro, follow, move, attrange, drops, dropchances);
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}

		return b;
		
	}
	
	public boolean createMobSpawnEntry(Connection sql, int map, int id, int amount, float rx, float ry, float radius) {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.createMobSpawnEntry(sql, map, id, amount, rx, ry, radius);
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}

		return b;
		
	}
	
	public boolean createNpcSpawnEntry(Connection sql, int map, int id, float x, float y) {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.createNpcSpawnEntry(sql, map, id, x, y);
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}

		return b;
		
	}
	
	public boolean createEquipmentTable() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.createEquipmentTable(new SQLconnection().getConnection());
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	
	public boolean createInventoryTable() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.createInventoryTable(new SQLconnection().getConnection());
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	
	public boolean createCharSkillsTable() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.createCharSkillsTable(new SQLconnection().getConnection());
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	
	public boolean createCharSkillbarsTable() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.createCharSkillbarsTable(new SQLconnection().getConnection());
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	
	public boolean createLvlsTable() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.createLvlsTable(new SQLconnection().getConnection());
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}
	
	public boolean createGamemasterTable() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.createGamemasterTable(new SQLconnection().getConnection());
			ps.execute();
			ps.close();
			
		}catch (SQLException e) {
			// e.printStackTrace();
			log.severe(this, "Database error: " +e.getMessage());
			b = false;
		}
		catch (Exception e) {
			// e.printStackTrace();
			log.severe(this, "Unspecified error:" +e.getMessage());
			b = false;
		}
		return b;
	}

}
