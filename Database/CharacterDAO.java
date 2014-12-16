package Database;

import item.ItemInInv;
import item.inventory.Equipments;
import item.inventory.Inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import logging.ServerLogger;
import Buffs.Buff;
import Buffs.ItemBuff;
import Player.Character;
import Player.CharacterMaster;
import Player.Player;
import Skills.CharacterSkillbar;
import Skills.CharacterSkills;

public class CharacterDAO {
	private static final Connection sqlConnection = new SQLconnection().getConnection();
	private static ServerLogger log = ServerLogger.getInstance();
	/*
	 * Method to add new character entry in database. Also return character instance.
	 * Reason for instantiating Character here: database will implicitly ensure unique character ID.
	 * Up until adding new database entry for the character, we have no way of knowing it's ID-to-be.
	 * Add entry -> ID assigned -> retrieve entry with unique ID -> ??? -> profit
	 * Return: Character instance if success, null if failed
	 */
	public static Character addAndReturnNewCharacter(String name, byte[] stats, int chClass, short statpoints, short skillpoints, Player pl, int xCoords, int yCoords, short face) {
		try {

				Queries.newCharacter(new SQLconnection().getConnection(), name, stats, chClass, statpoints, skillpoints, xCoords, yCoords, face, pl.getAccountID()).execute();
				ResultSet rs = Queries.getCharacterByName(sqlConnection, name).executeQuery();
				boolean gotResults = rs.next();
				if(gotResults) {
					Character newCharacter = processCharacterTable(rs);
					createEquipsEntry(newCharacter);
					Equipments eq = getEquipments(newCharacter);
					newCharacter.setEquips(eq);
					createInventoryEntry(newCharacter);
					Inventory inv = getInventories(newCharacter);
					newCharacter.setInventory(inv);
					newCharacter.setPlayer(pl);
					createCharSkillsEntry(newCharacter);
					newCharacter.setCharacterSkills(getCharSkills(newCharacter));
					createCharSkillbarEntry(newCharacter);
					newCharacter.setCharacterSkillbar(getCharSkillbar(newCharacter));
					createCharBuffEntry(newCharacter);
					rs.close();
					return newCharacter;
				} else {
					return null;
				}
		} catch (SQLException e) {
			e.printStackTrace();
			log.severe(CharacterDAO.class, e.getMessage());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			log.severe(CharacterDAO.class, e.getMessage());
			return null;
		}
	}
	
	public static void deleteCharacter(Character ch){
		try {
			PreparedStatement ps=Queries.deleteCharacter(sqlConnection, ch.getCharID());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void createEquipsEntry(Character ch) throws Exception {
		List<Integer> ls = new ArrayList<Integer>();
		for (int i=0; i < 17; i++){
			ls.add(0);
		}
		Queries.createEquips(sqlConnection, ch.getuid(), ls).execute();
		
	}
	
	private static void createInventoryEntry(Character ch) throws Exception {
		
		try{
			PreparedStatement ps=Queries.createInventories(sqlConnection, ch.getuid(), new Inventory(3));
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void createCharSkillsEntry(Character ch) throws Exception {
		
		try{
			PreparedStatement ps=Queries.createCharSkills(sqlConnection, ch.getuid(), new CharacterSkills(ch));
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void createCharSkillbarEntry(Character ch) throws Exception {
		
		try{
			PreparedStatement ps=Queries.createCharSkillbars(sqlConnection, ch.getuid(), new CharacterSkillbar());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void createCharBuffEntry(Character ch) throws Exception {
		
		try{
			PreparedStatement ps=Queries.createCharBuffs(sqlConnection, ch.getuid());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	/*
	 * Load a single character with given name
	 * Return: Character object if found, null if no entry
	 */
	public static Character loadCharacter(String name) {
		ResultSet rs;
		try {
			rs = Queries.getCharacterByName(sqlConnection, name).executeQuery();
		boolean gotResults = rs.next();
		if(gotResults) {
			Character newCharacter = processCharacterTable(rs);
			rs.close();
			return newCharacter;
		} else {
			return null;
		}
		} catch (SQLException e) {
			log.severe(CharacterDAO.class, "Database error: " + e.getMessage());
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			log.severe(CharacterDAO.class, e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * Load a single character with given id
	 * Return: Character object if found, null if no entry
	 */
	public static Character loadCharacter(int id) {
		ResultSet rs;
		try {
			rs = Queries.getCharacterByID(sqlConnection, id).executeQuery();
		boolean gotResults = rs.next();
		if(gotResults) {
			Character newCharacter = processCharacterTable(rs);
			rs.close();
			return newCharacter;
		} else {
			return null;
		}
		} catch (SQLException e) {
			// TODO Auto-generated catch block'
			log.severe(CharacterDAO.class, "Database error: " + e.getMessage());
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.severe(CharacterDAO.class, e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * Load list of characters with given account ID
	 * Return: List containing Character objects, or null if failed
	 */
	public static ArrayList<Character> loadCharacters(int id) {
		ArrayList<Character> chlist = new ArrayList<Character>();
		ResultSet rs;
		try {
			rs = Queries.getCharactersByOwnerID(sqlConnection, id).executeQuery();
				while(rs.next()) {
					chlist.add(processCharacterTable(rs));
				}
			if (rs!=null)
				rs.close();
			if(!chlist.isEmpty()) {
				return chlist;
			} else {
				return null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.severe(CharacterDAO.class, "Database error: " + e.getMessage());
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.severe(CharacterDAO.class, e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	//----------CHARACTER----------
	
	public static boolean doesCharNameExist(String name){
		ResultSet rs;
		try {
			rs = Queries.getCharacterByName(sqlConnection, name).executeQuery();
			boolean b=rs.next();
			rs.close();
			return b;
		} catch (SQLException e) {
			log.severe(CharacterDAO.class, "Database error: " + e.getMessage());
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			log.severe(CharacterDAO.class, e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	public static void saveCharFace(Character ch){
		try {
			PreparedStatement ps=Queries.saveCharFace(sqlConnection, ch);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveCharFaction(Character ch){
		try {
			PreparedStatement ps=Queries.saveCharFaction(sqlConnection, ch);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveCharKao(Character ch){
		try {
			PreparedStatement ps=Queries.saveCharKao(sqlConnection, ch);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveCharSize(Character ch){
		try {
			PreparedStatement ps=Queries.saveCharSize(sqlConnection, ch);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveCharGMrank(Character ch){
		try {
			PreparedStatement ps=Queries.saveCharGMrank(sqlConnection, ch);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveCharAbandoned(Character ch){
		try {
			PreparedStatement ps=Queries.saveCharAbandoned(sqlConnection, ch);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean saveCharacterLocation(Character chara) {
		try {
			PreparedStatement ps=Queries.saveCharacterLocation(sqlConnection, chara);
			boolean b=ps.execute();
			ps.close();
			return b;
		} catch (SQLException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean saveCharacterHpMpSp(Character chara) {
		try {
			PreparedStatement ps=Queries.saveCharacterHpMpSp(sqlConnection, chara);
			boolean b=ps.execute();
			ps.close();
			return b;
		} catch (SQLException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void loadCharacterStuffForRelog(Character chara) {
		ResultSet rs;
		try {
			rs = Queries.getCharacterByID(sqlConnection, chara.getCharID()).executeQuery();
			rs.next();
			chara.addHp(rs.getInt("currentHP"));
			chara.addMana(rs.getInt("currentMana"));
			chara.addStam(rs.getInt("currentStamina"));
			chara.setCommands(rs.getShort("commands")==1);
			chara.setGMrank(rs.getShort("GMrank"));
			chara.setFace(rs.getShort("face"));
			chara.setSize(rs.getShort("size"));
			chara.setKao(rs.getShort("kao"));
			chara.setDead(rs.getShort("isDead")==1);
			chara.setFaction(rs.getShort("faction"));
			chara.setFame(rs.getInt("fame"));
			rs.close();
		} catch (SQLException e) {
			log.severe(CharacterDAO.class, "Database error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.severe(CharacterDAO.class, e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void loadCharacterHpMpSp(Character chara) {
		ResultSet rs;
		try {
			rs = Queries.loadCharacterHpMpSp(sqlConnection, chara).executeQuery();
			rs.next();
			chara.addHp(rs.getInt(1));
			chara.addMana(rs.getInt(2));
			chara.addStam(rs.getInt(3));
			chara.refreshHpMpSp();
			rs.close();
		} catch (SQLException e) {
			log.severe(CharacterDAO.class, "Database error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.severe(CharacterDAO.class, e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static boolean saveCharacterStats(Character chara) {
		try {
			PreparedStatement ps=Queries.saveCharacterStats(sqlConnection, chara);
			boolean b=ps.execute();
			ps.close();
			return b;
		} catch (SQLException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void loadCharacterStats(Character chara) {
		ResultSet rs;
		try {
			rs = Queries.loadCharacterStats(sqlConnection, chara).executeQuery();
			rs.next();
			short[] stats=new short[5];
			for(int i=0;i<5;i++)
				stats[i]=rs.getShort(i+1);
			chara.setCStats(stats);
			chara.calculateCharacterStats();
			rs.close();
		} catch (SQLException e) {
			log.severe(CharacterDAO.class, "Database error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.severe(CharacterDAO.class, e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void loadCharacterLvl(int lvlcap){
		ResultSet rs;
		try {
			rs = Queries.getAllLvls(sqlConnection).executeQuery();
			boolean gotResult=rs.next();
			long[] exp=new long[168];
			int i=0;
			while(gotResult){
				exp[i]=rs.getLong("exp");
				gotResult=rs.next();
				i++;
			}
			CharacterMaster.init(lvlcap,exp);
			rs.close();
		} catch (SQLException e) {
			log.severe(CharacterDAO.class, "Database error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.severe(CharacterDAO.class, e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static boolean saveCharacterLvlexp(Character chara) {
		try {
			PreparedStatement ps=Queries.saveCharacterLvlexp(sqlConnection, chara);
			boolean b=ps.execute();
			ps.close();
			return b;
		} catch (SQLException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean saveCharacterStatpoints(Character chara) {
		try {
			PreparedStatement ps=Queries.saveCharacterStatpoints(sqlConnection, chara);
			boolean b=ps.execute();
			ps.close();
			return b;
		} catch (SQLException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean saveCharacterSkillpoints(Character chara) {
		try {
			PreparedStatement ps=Queries.saveCharacterSkillpoints(sqlConnection, chara);
			boolean b=ps.execute();
			ps.close();
			return b;
		} catch (SQLException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean saveCharacterFame(Character chara) {
		try {
			PreparedStatement ps = Queries.saveCharacterFame(sqlConnection, chara);
			boolean b = ps.execute();
			return b;
		} catch (SQLException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean saveCharacterFameTitle(Character chara) {
		try {
			PreparedStatement ps = Queries.saveCharacterFameTitle(sqlConnection, chara);
			boolean b = ps.execute();
			return b;
		} catch (SQLException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean saveCharacterDead(Character chara) {
		try {
			PreparedStatement ps=Queries.saveCharacterDead(sqlConnection, chara);
			boolean b=ps.execute();
			ps.close();
			return b;
		} catch (SQLException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void loadCharacterCommands(Character chara) {
		ResultSet rs;
		try {
			rs = Queries.getCharacterByID(sqlConnection, chara.getuid()).executeQuery();
			rs.next();
			chara.setCommands(rs.getInt("commands")==1);
			rs.close();
		} catch (SQLException e) {
			log.severe(CharacterDAO.class, "Database error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.severe(CharacterDAO.class, e.getMessage());
			e.printStackTrace();
		}
	}
	
	//--------------------
	
	/*
	 * Save a list of characters to database
	 * Return: true if all succeed, false if one or more fail
	 */
	public static boolean saveCharacters(List<Character> ch) {
		return true;
	}
	
	public static Character processCharacterTable(ResultSet rs) {
		Character newCharacter = new Character();
		try {
			newCharacter.setuid(rs.getInt("CharacterID"));
			newCharacter.setName(rs.getString("charname"));
			newCharacter.setCharacterClass(rs.getInt("charClass"));
			newCharacter.setFace(rs.getShort("face"));
			newCharacter.setSize(rs.getShort("size"));
			newCharacter.setKao(rs.getShort("kao"));
			newCharacter.setAbandoned(rs.getShort("isAbandoned")==1);
			newCharacter.setDead(rs.getInt("isDead")==1);
			newCharacter.setFaction(rs.getInt("faction"));
			newCharacter.setLevel(rs.getInt("level"));
			newCharacter.setExp(rs.getLong("exp"));
			newCharacter.setFame(rs.getInt("fame"));
			newCharacter.setFameTitle(rs.getShort("fametitle"));

			newCharacter.setX(rs.getInt("locationX"));
			newCharacter.setY(rs.getInt("locationY"));
			newCharacter.setCurrentMap(rs.getInt("map"));
		
			short[] dats = new short[5];
		
			dats[3] = rs.getShort("intelligence"); //INT 
			dats[2] = rs.getShort("vitality"); //VIT 
			dats[4] = rs.getShort("agility"); //AGI 
			dats[0] = rs.getShort("strength"); //STR 
			dats[1] = rs.getShort("dexterity"); //DEX 
		
			newCharacter.setStats(new short[5]);
			newCharacter.setCStats(dats);
			
			newCharacter.setStatPoints(rs.getInt("statpoints"));
			newCharacter.setSkillPoints(rs.getShort("skillpoints"));
			
			newCharacter.setCommands(rs.getInt("commands")==1);
			newCharacter.setGMrank(rs.getShort("GMrank"));
			
			newCharacter.setEquips(getEquipments(newCharacter));
			newCharacter.setInventory(getInventories(newCharacter));
			newCharacter.setCharacterSkills(getCharSkills(newCharacter));
			newCharacter.setCharacterSkillbar(getCharSkillbar(newCharacter));
			if(newCharacter.getEquips()!=null)
				newCharacter.createCharacterStats();
			newCharacter.setCharacterBuffs(getBuffs(newCharacter));
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.severe(CharacterDAO.class, "Database error: " + e.getMessage());
			e.printStackTrace();
		}
		return newCharacter;
	}
	// retrieve equipped items from database for character 
	public static Equipments getEquipments(Character ch){
		Equipments eq = null;
		try {
			ResultSet rs = Queries.getEquipment(sqlConnection, ch.getuid()).executeQuery();
			if (rs.next()){
				Map<Integer, ItemInInv> mp = new HashMap<Integer, ItemInInv>();
				
				int id = 0;
				for (int i = 0; i <17; i++){
					id = rs.getInt(i+1);
					if (id != 0){ 
						//System.out.println("Found item at slot:" + i + " ID: " +id);
						ItemInInv it = new ItemInInv(id);
						if(it!=null){
							//it.getEquipable().printData();
							mp.put(i, it);
						}
					}
				}
				eq =  new Equipments(ch);
				eq.setEquipmentsSaved(mp);
				eq.updateEquip();
				
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return eq;
	}
	public static void saveEquipments(int uid, Equipments eq){
		try {
			Map<Integer, ItemInInv> mp = eq.getEquipmentsSaved();
			List<Integer> ls = new ArrayList<Integer>();
			for (int i=0; i <17; i++){
				if  (mp.containsKey(i)){ ls.add(mp.get(i).getItem().getId()); }
				else ls.add(0);
			}
			PreparedStatement ps=Queries.storeEquipments(sqlConnection, uid, ls);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//----------INVENTORY----------
	
	public static Inventory getInventories(Character ch){
		Inventory inv = null;
		try {
			ResultSet rs = Queries.getInventory(sqlConnection, ch.getuid()).executeQuery();
			if (rs.next()){
				inv =  new Inventory(rs.getInt(723));
				inv.setCoins(rs.getInt(721));
				inv.setVendingPoints(rs.getInt(722));
				LinkedList<Integer> seqhash = new LinkedList<Integer>();
				
				int val = 0;
				int amount;
				ItemInInv it;
				for (int i = 0; i <240; i++){
					val = rs.getInt(i*3+1);
					amount=rs.getInt(i*3+3);
					it=null;
					if (val != 0 && amount!=0){ 
						//System.out.println("Found item at slot:" + i + " ID: " +val);
						it = new ItemInInv(val);;
					}
					val = rs.getInt(i*3+2);
					seqhash.add(val);
					if(it!=null){
						inv.putIntoInv(val%100, val/100, it);
						it.setAmount(amount);
					}
				}
				
				inv.saveInv();
				inv.setSeqSaved(seqhash);
				
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return inv;
	}
	
	public static void saveInventories(int uid, Inventory inv){
		try {
			PreparedStatement ps=Queries.storeInventories(sqlConnection, uid,inv);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//----------SKILLS----------
	
	public static CharacterSkills getCharSkills(Character ch){
		CharacterSkills chSkills = null;
		try {
			int skillId;
			ResultSet rs = Queries.getCharSkills(sqlConnection, ch.getuid()).executeQuery();
			if (rs.next()){
				chSkills =  new CharacterSkills(ch);
				
				for(int i=0;i<200;i++){
					skillId=rs.getInt(i+1);
					if(skillId!=0){
						chSkills.learnSkill(skillId,false);
					}
				}
				
				saveCharacterSkillpoints(ch);
				
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return chSkills;
	}
	
	public static void saveCharSkills(int uid, CharacterSkills chSkills){
		try {
			PreparedStatement ps=Queries.storeCharSkills(sqlConnection, uid, chSkills);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//----------SKILLBAR----------
	
	public static CharacterSkillbar getCharSkillbar(Character ch){
		CharacterSkillbar chSkillbar = null;
		try {
			int skillId;
			ResultSet rs = Queries.getCharSkillbars(sqlConnection, ch.getuid()).executeQuery();
			if (rs.next()){
				chSkillbar =  new CharacterSkillbar();
				
				for(int i=0;i<21;i++){
					skillId=rs.getInt(i+1);
					if(skillId!=-1){
						chSkillbar.getSkillBar().put(i, skillId);
					}
				}
				
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return chSkillbar;
	}
	
	public static void saveCharSkillbar(int uid, CharacterSkillbar chSkillbar){
		try {
			PreparedStatement ps=Queries.storeCharSkillbars(sqlConnection, uid, chSkillbar);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//----------BUFFS----------
	
	public static HashMap<Short, Buff> getBuffs(Character ch) {
		HashMap<Short, Buff> buffActive = new HashMap<Short, Buff>();
		try {
			long buffTime;
			short buffId, buffValue;
			ResultSet rs = Queries.getCharBuffs(sqlConnection, ch.getuid()).executeQuery();
			for(int i=0;i<57;i+=3){
				if (rs.next()) {
					buffId=rs.getShort(i+1);
					buffTime=rs.getLong(i+2);
					buffValue=rs.getShort(i+3);
					System.out.println(!(buffId==0 || buffTime==0 || buffValue==0));
					if(!(buffId==0 || buffTime==0 || buffValue==0)){
						buffActive.put(buffId, new ItemBuff(ch, buffId, buffTime, buffValue)); //Doesn't matter if item- or skillbuff?
					}
				}
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return buffActive;
	}
	
	public static void saveCharBuffs(int uid, HashMap<Short, Buff> buffActive){
		try {
			PreparedStatement ps=Queries.storeCharBuffs(sqlConnection, uid, buffActive);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
