package Player;


import item.ItemInInv;
import item.vendor.Vendor;
import item.vendor.VendorPackets;
import item.inventory.Equipments;
import item.inventory.Inventory;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import timer.MoveSyncTimer;
import timer.HealingTimer;
import logging.ServerLogger;
import Database.CharacterDAO;
import Duel.Duel;
import Parties.Party;
import Parties.PartyPackets;
import Player.Dolls.Cleverdoll;
import Player.Dolls.Doll;
import ServerCore.ServerFacade;
import Skills.CharacterSkillbar;
import Skills.CharacterSkills;
import Skills.SkillMaster;
import Tools.BitTools;
import World.Grid;
import World.Location;
import World.OutOfGridException;
import World.WMap;
import World.Area;
import World.Waypoint;

public class Character implements Location, Fightable {
	public static final int factionNeutral = 1;
	public static final int factionLawful = 2;
	public static final int factionEvil = 3;
	private String name = "NOP";
	private int level=1;
	private long exp=0;
	private short[] stats,cStats;
	private int statPoints;
	private int skillPoints;
	private int characterClass;
	private Waypoint location;
	private int faction;
	private int maxhp, hp, maxmana, mana, maxstamina, stamina;
	private short attack, defence,hpreg,manareg,stamreg,minDmg,maxDmg, critdmg;
	private int healingSpeed;
	private int basicAtkSuc, basicDefSuc, basicCritRate, additionalAtkSuc, additionalDefSuc, additionalCritRate;
	private float atkSucMul, defSucMul, critRateMul;
	private int atkSuc, defSuc, critRate;
	private int charID;
	private int currentMap;
	private int [] areaCoords;
	private Area area;
	private Grid grid;
	private byte[] characterDataPacket;
	private Equipments equips;
	private Inventory inventory = new Inventory(3);
	private CharacterSkillbar skillbar = new CharacterSkillbar();
	private CharacterSkills skills = new CharacterSkills(this);
	private List<Integer> iniPackets = Collections.synchronizedList(new ArrayList<Integer>());
	private ServerLogger log = ServerLogger.getInstance();
	private Player pl;
	private MoveSyncTimer moveSyncTimer;
	private Timer timer, healingTimer, respawnTimer;
	private float speed = 24.5f;
	private boolean walking=false;
	private float turboSpeed=0;
	private WMap wmap = WMap.getInstance();
	private boolean isBot=false;
	private Doll doll=null;
	private boolean useableReady=true;
	private Timer useableTimer;
	private boolean skillReady=true;
	private Timer skillTimer;
	private boolean dead=false;
	private LinkedList<Integer> testByteIndex=new LinkedList<Integer>();
	private LinkedList<Byte> testByteValue=new LinkedList<Byte>();
	private LinkedList<Integer> testByteIndexExt=new LinkedList<Integer>();
	private LinkedList<Byte> testByteValueExt=new LinkedList<Byte>();
	private boolean commands;
	private boolean abandoned;
	private short face;
	private short kao;
	private short size;
	private short GMrank;
	private int fame;
	private short fameTitle;
	private Party pt=null;
	private Duel duel=null;
	private boolean reviveSave=false;
	private Vendor vendor = null;
	private int lastHit;
	
	public Character(Doll doll){
		this.doll=doll;
		if(doll!=null){
			isBot=true;
		}else{
			isBot=false;
		}
		this.location = new Waypoint(0,0);
		this.equips = new Equipments(this);
	}
	
	public Character(Player pl) {
		this.pl = pl;
		this.location = new Waypoint(0,0);
		// TODO loading equips from db
		this.equips = new Equipments(this);
	}

	public Character() {
		//TODO: durp durp
		 this.location = new Waypoint(0,0);
		// TODO loading equips from db
		 this.equips = new Equipments(this);
	}
	
	/*
	 * Handle all logic required when character is selected in selection screen
	 * and player enters the game
	 */
	public void joinGameWorld() {
		
		//standard stats
		createCharacterStats();
		
		if(!isBot){
			
			//load other stuff
			CharacterDAO.loadCharacterStuffForRelog(this);
			
			//spawnpacket
			sendSpawnPacket();
			
		}
		
		refreshHpMpSp();
		
		gainExp(exp,false);
		
		lastHit=getuid();
		
		//move sync timer
		timer=new Timer();
		moveSyncTimer=new MoveSyncTimer(this);
		timer.scheduleAtFixedRate(moveSyncTimer,1000,1000);
		
		//regeneration timer
		healingTimer=new Timer();
		healingTimer.scheduleAtFixedRate(new HealingTimer(this),healingSpeed,healingSpeed);
		
		//useable items timer
		useableTimer=new Timer();
		
		//useable items timer
		skillTimer=new Timer();
		
		this.wmap.addCharacter(this);
		try {
			this.grid = this.wmap.getGrid(this.currentMap);
			this.area = this.grid.update(this);
			this.iniPackets.addAll(this.area.addMemberAndGetMembers(this));
			this.sendInitToAll();
		}
		catch(OutOfGridException oe) {
			log.logMessage(Level.SEVERE, this, oe.getMessage() + this.charID +" map:" +this.currentMap + ", disconnecting");
			ServerFacade.getInstance().finalizeConnection(this.GetChannel());
		}
		
		if(dead){
			die();
		}
		
		if(!isBot)
			pl.refreshCharacterOrder();
		
	}
	
	private void sendInitToAll() {
		this.sendInitToList(iniPackets);	
	}

	/*
	 * Quite the opposite of joining the game world
	 */
	public void leaveGameWorld(boolean leaveStuff) {
		if(!isBot){
			CharacterDAO.saveCharacterLocation(this);
			//load other stuff
			CharacterDAO.loadCharacterStuffForRelog(this);
		}
		
		walking=false;
		turboSpeed=0;
		updateSpeed();
		
		if(leaveStuff){
			leavePt();
		}
		if(duel!=null)
			duel.loseDuel(this);
		
		leavePtDuel();
		
		if(timer!=null)
			timer.cancel();
		if(healingTimer!=null)
			healingTimer.cancel();
		if(respawnTimer!=null)
			respawnTimer.cancel();
		if(useableTimer!=null)
			useableTimer.cancel();
		if(skillTimer!=null)
			skillTimer.cancel();
		
		this.area.rmMember(this);
		this.wmap.rmCharacter(charID);
		Iterator<Integer> it = this.iniPackets.iterator();
		Integer player;
		while (it.hasNext()){
			player = it.next();
			Character ch = wmap.getCharacterMap().get(player);
			if(ch!=null && !ch.isBot())
				ServerFacade.getInstance().addWriteByChannel(this.wmap.getCharacter(player).GetChannel(), CharacterPackets.getVanishByID(this.charID));
		}
		this.iniPackets.clear();
		
		if(!isBot){
			pl.refreshCharacterOrder();
		}
	}
	
	public void rejoin(){
		leaveGameWorld(false);
		joinGameWorld();
		if(pt!=null)
			pt.refreshChar(this);
	}
	
	//update the whole area
	public void revive(boolean updateArea){
		
		dead=false;
		if(!isBot)
			CharacterDAO.saveCharacterDead(this);
		if(updateArea){
			rejoin();
		}
		addHpMpSp(maxhp,maxmana,maxstamina);
		
	}
	
	public void calculateCharacterStats() {
		
		float hardness=1;
		if(doll!=null){
			hardness=doll.getHardness();
		}
		short[] eqstats=equips.getStats();
		for(int i=0;i<5;i++){
			stats[i]=(short) (cStats[i]+eqstats[i]);
		}
		maxhp=(int) ((30+equips.getHp()+stats[0]*2.2+stats[1]*2.4+stats[2]*2.5+stats[3]*1.6+stats[4]*1.5)*hardness);
		maxmana=(int) ((30+equips.getMana()+stats[0]*1.4+stats[1]*1.7+stats[2]*1.5+stats[3]*3.5+stats[4]*1.5)*hardness);
		maxstamina=(int) ((30+equips.getStamina()+stats[0]*0.9+stats[1]*1.3+stats[2]*1.5+stats[3]*1.7+stats[4]*1.3)*hardness);
		hpreg=(short)((stats[2]+stats[0]/2)*hardness);
		manareg=(short)((stats[3]+stats[1]/2)*hardness);
		stamreg=(short)((stats[4]*0.1)*hardness);
		healingSpeed=5000;
		attack=(short) ((level/2+equips.getAtk()+stats[0]*0.5+stats[1]*0.46+stats[2]*0.4+stats[3]*0.2+stats[4]*0.2)*hardness);
		defence=(short) ((level/2+equips.getDeff()+stats[0]*0.28+stats[1]*0.3+stats[2]*0.53+stats[3]*0.22+stats[4]*0.42)*hardness);
		minDmg=(short)(equips.getMinDmg()*hardness);
		maxDmg=(short)(equips.getMaxDmg()*hardness);
		basicAtkSuc=(int)((stats[0]*0.5+stats[1]*0.6+stats[2]*0.3+stats[3]*1+stats[4]*0.8+level*6)*hardness);
		basicDefSuc=(int)(stats[0]*0.2+stats[1]*0.2+stats[2]*0.5+stats[3]*0.7+stats[4]*0.6+level*4);
		basicCritRate=(int)((stats[0]*0.1+stats[1]*1+stats[2]*0.1+stats[3]*0.5+stats[4]*0.3+level*2)*hardness)-300;
		additionalAtkSuc=1000;
		additionalDefSuc=500;
		additionalCritRate=2000;
		atkSucMul=equips.getAtkSucMul();
		defSucMul=equips.getDefSucMul();
		critRateMul=equips.getCritRateMul();
		atkSuc=calcAtkSuc();
		defSuc=calcDefSuc();
		critRate=calcCritRate();
		critdmg=(short)((equips.getCritDmg()+stats[1]-10)*hardness);
		if(critdmg<0)
			critdmg=0;
		updateSpeed();
		
		if(hp>maxhp)
			hp=maxhp;
		if(mana>maxmana)
			mana=maxmana;
		if(stamina>maxstamina)
			stamina=maxstamina;
		
		if(!isBot)
			CharacterDAO.saveCharacterStats(this);
		
	}
	
	public void createCharacterStats(){
		
		hp=0;
		mana=0;
		stamina=0;
		calculateCharacterStats();
		
	}
	
	//----------HP MANA AND STAMINA----------
	
	public synchronized void addHpSigned(int hp){
		
		this.hp+=hp;
		if(this.hp>maxhp)
			this.hp=maxhp;
		if(this.hp<0)
			this.hp=0;
		
	}
	
	public synchronized void addManaSigned(int mana){
		
		this.mana+=mana;
		if(this.mana>maxmana)
			this.mana=maxmana;
		if(this.mana<0)
			this.mana=0;
		
	}

	public synchronized void addStaminaSigned(int stamina){
	
		this.stamina+=stamina;
		if(this.stamina>stamina)
			this.stamina=maxstamina;
		if(this.stamina<0)
			this.stamina=0;
	
	}
	
	public synchronized void addHp(int hp){
		
		if(hp>0){
			this.hp+=hp;
			if(this.hp>maxhp)
				this.hp=maxhp;
		}
		
	}
	
	public synchronized void subtractHp(int hp){
		
		if(hp>0){
			this.hp-=hp;
			if(this.hp<0)
				this.hp=0;
			if(!dead && this.hp==0){
				die();
			}
		}
		
	}
	
	public synchronized void addMana(int mana){
		
		if(mana>0){
			this.mana+=mana;
			if(this.mana>maxmana)
				this.mana=maxmana;
		}
		
	}
	
	public synchronized void subtractMana(int mana){
		
		if(mana>0){
			this.mana-=mana;
			if(this.mana<0)
				this.mana=0;
		}
		
	}
	
	public synchronized void addStam(int stamina){
		
		if(stamina>0){
			this.stamina+=stamina;
			if(this.stamina>maxstamina)
				this.stamina=maxstamina;
		}
		
	}
	
	public synchronized void subtractStam(int stamina){
		
		if(stamina>0){
			this.stamina-=stamina;
			if(this.stamina<0)
				this.stamina=0;
		}
		
	}
	
	public void addHpMpSp(int hp,int mana,int stam){
		
		addHp(hp);
		addMana(mana);
		addStam(stam);
		refreshHpMpSp();
		
	}
	
	public void subtractHpMpSp(int hp,int mana,int stam){
		
		subtractHp(hp);
		subtractMana(mana);
		subtractStam(stam);
		refreshHpMpSp();
		
	}
	
	public void autoHeal(){
		
		addHpMpSp(hpreg,manareg,stamreg);
		
	}
	
	public synchronized void recDamage(int uid, int dmg) throws OutOfGridException{
		
		subtractHp(dmg);
		lastHit=uid;
		if(doll!=null && uid!=getuid())
			doll.startAnnoyCharacter(uid);
		stopMovement();
		
	}
	
	public void refreshHpMpSp(){
		
		if(!isBot){
			byte[] healpckt = CharacterPackets.getHealPacket(this);
			ServerFacade.getInstance().getConnectionByChannel(GetChannel()).addWrite(healpckt);
			CharacterDAO.saveCharacterHpMpSp(this);
		}
		if(pt!=null)
			pt.refreshChar(this);
		
	}
	public int getMaxHp() {
		return maxhp;
	}

	public void setMaxHp(short max) {
		this.maxhp = max;
	}
	
	public int getHp() {
		return hp;
	}

	public void setHp(short hp) {
		this.hp = hp;
	}
	
	public int getMaxmana() {
		return maxmana;
	}

	public void setMaxmana(short maxmana) {
		this.maxmana = maxmana;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(short mana) {
		this.mana = mana;
	}
	
	public int getMaxstamina() {
		return maxstamina;
	}

	public void setMaxstamina(short maxstamina) {
		this.maxstamina = maxstamina;
	}

	public int getStamina() {
		return stamina;
	}

	public void setStamina(short stamina) {
		this.stamina = stamina;
	}
	//--------------------
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNewName(String name) {
		String finalname=name;
		int i=0;
		while(CharacterDAO.doesCharNameExist(finalname)){
			i++;
			finalname=name+i;
		}
		this.name = finalname;
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
		if(!isBot)
			CharacterDAO.saveCharacterLvlexp(this);
		if(pt!=null)
			pt.refreshChar(this);
	}

	public short[] getStats() {
		return stats;
	}

	public void setStats(short[] stats) {
		this.stats = stats;
	}
	
	public short[] getCStats() {
		return cStats;
	}

	public void setCStats(short[] cStats) {
		this.cStats = cStats;
	}

	public int getStatPoints() {
		return statPoints;
	}

	public void setStatPoints(int statPoints) {
		this.statPoints = statPoints;
		if(!isBot)
			CharacterDAO.saveCharacterStatpoints(this);
	}

	public int getSkillPoints() {
		return skillPoints;
	}

	public void setSkillPoints(int skillPoints) {
		this.skillPoints = skillPoints;
		if(!isBot)
			CharacterDAO.saveCharacterSkillpoints(this);
	}

	public int getFame() {
		return fame;
	}
	
	public void setFame(int fame) {
		this.fame = fame;
		if(!isBot)
			CharacterDAO.saveCharacterFame(this);
	}
	
	public void addFame(int fame){
		setFame(this.fame+fame);
	}
	
	public int getCharacterClass() {
		return characterClass;
	}

	public void setCharacterClass(int characterClass) {
		this.characterClass = characterClass;
	}

	public int getFaction() {
		return faction;
	}

	public void setFaction(int faction) {
		this.faction = faction;
	}
	
	public void changeFaction(int faction){
		this.faction = faction;
		if(!isBot)
			CharacterDAO.saveCharFaction(this);
		SkillMaster.reSkill(this);
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(short attack) {
		this.attack = attack;
	}

	public int getDefence() {
		return defence;
	}

	public void setDefence(short defence) {
		this.defence = defence;
	}

	public Player getPlayer() {
		return pl;
	}

	public void setPlayer(Player pl) {
		this.pl = pl;
	}

	public Area getArea() {
		return area;
	}

	public Grid getGrid() {
		return grid;
	}

	public int[] getAreaCoords() {
		return areaCoords;
	}

	@Override
	public int getuid() {
		// TODO Auto-generated method stub
		return this.charID;
	}

	@Override
	public void setuid(int uid) {
		// TODO Auto-generated method stub
		this.charID = uid;		
	}

	@Override
	public float getlastknownX() {
		// TODO Auto-generated method stub
		return this.location.getX();
	}

	@Override
	public float getlastknownY() {
		// TODO Auto-generated method stub
		return this.location.getY();
	}
	@Override
	public SocketChannel GetChannel() {
		if(pl!=null)
			return this.pl.getSc();
		else
			return null;
	}

	@Override
	public short getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getCharID() {
		return charID;
	}

	public void setCharID(int charID) {
		this.charID = charID;
	}

	public void setX(float x) {
		this.location.setX(x);
	}

	public void setY(float y) {
		this.location.setY(y);
	}

	public int getCurrentMap() {
		return currentMap;
	}

	public void setCurrentMap(int currentMap) {
		this.currentMap = currentMap;
	}

	public Equipments getEquips() {
		return equips;
	}

	public void setEquips(Equipments equips) {
		this.equips = equips;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	
	public void setCharacterSkillbar(CharacterSkillbar skillbar){
		this.skillbar=skillbar;
	}
	
	public CharacterSkillbar getSkillbar(){
		return skillbar;
	}
	
	public CharacterSkills getSkills(){
		return skills;
	}
	
	public void setCharacterSkills(CharacterSkills skills){
		if(skills!=null){
			this.skills=skills;
			for(int i=0;i<21;i++){
				
				if(skillbar!=null && skillbar.getSkillBar().containsKey(i)){
					
					if(skillbar.getSkillBar().get(i)<256){
						skillbar.getSkillBar().remove(i);
					}
					
				}
			
			}
			if(!isBot)
				CharacterDAO.saveCharSkills(getuid(), skills);
		}
	}

	public int getMaxhp() {
		return maxhp;
	}

	public byte[] getCharacterDataPacket() {
		return characterDataPacket;
	}

	public void setCharacterDataPacket(byte[] characterDataPacket) {
		this.characterDataPacket = characterDataPacket;
	}
	
	public boolean isBot(){
		return isBot;
	}
	
	public short getMinDmg() {
		return minDmg;
	}

	public void setMinDmg(short minDmg) {
		this.minDmg = minDmg;
	}
	
	public short getMaxDmg() {
		return maxDmg;
	}

	public void setMaxDmg(short maxDmg) {
		this.maxDmg = maxDmg;
	}
	
	public boolean getUseableReady() {
		return useableReady;
	}

	public void setUseableReadyFalse() {
		useableReady=false;
		useableTimer.schedule(new TimerTask() {
			  @Override
			  public void run() {
				Character.this.useableReady=true;
				cancel();
			  }
		}, 500);
	}
	
	public boolean getSkillReady() {
		return skillReady;
	}

	public void setSkillReadyFalse() {
		skillReady=false;
		skillTimer.schedule(new TimerTask() {
			  @Override
			  public void run() {
				Character.this.skillReady=true;
				cancel();
			  }
		}, 500);
	}
	
	public void setExp(long exp){
		this.exp=exp;
		if(!isBot)
			CharacterDAO.saveCharacterLvlexp(this);
	}
	
	public long getExp(){
		return exp;
	}
	
	public boolean isDead(){
		return dead;
	}
	
	public void setDead(boolean dead){
		this.dead=dead;
	}
	
	public boolean hasCommands() {
		return commands;
	}

	public void setCommands(boolean commands) {
		this.commands = commands;
	}
	
	public boolean isAbandoned() {
		return abandoned;
	}

	public void setAbandoned(boolean abandoned) {
		this.abandoned = abandoned;
		CharacterDAO.saveCharAbandoned(this);
	}
	
	public short getFace() {
		return face;
	}

	public void setFace(short face) {
		this.face = face;
		if(!isBot)
			CharacterDAO.saveCharFace(this);
	}
	
	public Timer getRespawnTimer(){
		return respawnTimer;
	}
	
	public LinkedList<Integer> getTestByteIndex(){
		return testByteIndex;
	}
	
	public void setTestByteIndex(LinkedList<Integer> testByteIndex){
		this.testByteIndex=testByteIndex;
	}
	
	public LinkedList<Byte> getTestByteValue(){
		return testByteValue;
	}
	
	public void setTestByteValue(LinkedList<Byte> testByteValue){
		this.testByteValue=testByteValue;
	}
	
	public LinkedList<Integer> getTestByteIndexExt(){
		return testByteIndexExt;
	}
	
	public void setTestByteIndexExt(LinkedList<Integer> testByteIndexExt){
		this.testByteIndexExt=testByteIndexExt;
	}
	
	public LinkedList<Byte> getTestByteValueExt(){
		return testByteValueExt;
	}
	
	public void setTestByteValueExt(LinkedList<Byte> testByteValueExt){
		this.testByteValueExt=testByteValueExt;
	}
	
	public short getFameTitle() {
		return fameTitle;
	}
	
	public void setFameTitle(short fameTitle) {
		this.fameTitle = fameTitle;
	}
	
	public short getKao() {
		return kao;
	}

	public void setKao(short kao) {
		this.kao = kao;
		if(!isBot)
			CharacterDAO.saveCharKao(this);
	}

	public short getSize() {
		return size;
	}

	public void setSize(short size) {
		this.size = size;
		if(!isBot)
			CharacterDAO.saveCharSize(this);
	}

	public short getGMrank() {
		return GMrank;
	}

	public void setGMrank(short GMrank) {
		this.GMrank = GMrank;
		if(!isBot)
			CharacterDAO.saveCharGMrank(this);
	}
	
	public int getBasicDefSuc() {
		return basicDefSuc;
	}

	public void setBasicDefSuc(int basicDefSuc) {
		this.basicDefSuc = basicDefSuc;
	}

	public int getBasicAtkSuc() {
		return basicAtkSuc;
	}

	public void setBasicAtkSuc(int basicAtkSuc) {
		this.basicAtkSuc = basicAtkSuc;
	}

	public int getAdditionalAtkSuc() {
		return additionalAtkSuc;
	}

	public void setAdditionalAtkSuc(int additionalAtkSuc) {
		this.additionalAtkSuc = additionalAtkSuc;
	}

	public int getAdditionalDefSuc() {
		return additionalDefSuc;
	}

	public void setAdditionalDefSuc(int additionalDefSuc) {
		this.additionalDefSuc = additionalDefSuc;
	}

	public float getDefSucMul() {
		return defSucMul;
	}

	public void setDefSucMul(float defSucMul) {
		this.defSucMul = defSucMul;
	}

	public float getAtkSucMul() {
		return atkSucMul;
	}

	public void setAtkSucMul(float atkSucMul) {
		this.atkSucMul = atkSucMul;
	}

	public float getCritRateMul() {
		return critRateMul;
	}

	public void setCritRateMul(float critRateMul) {
		this.critRateMul = critRateMul;
	}
	
	public int getAdditionalCritRate() {
		return additionalCritRate;
	}

	public void setAdditionalCritRate(int additionalCritRate) {
		this.additionalCritRate = additionalCritRate;
	}

	public int getBasicCritRate() {
		return basicCritRate;
	}

	public void setBasicCritRate(int basicCritRate) {
		this.basicCritRate = basicCritRate;
	}
	
	public int calcAtkSuc(){
		return (int)(basicAtkSuc+additionalAtkSuc*atkSucMul);
	}
	
	public int calcDefSuc(){
		return (int)(basicDefSuc+additionalDefSuc*defSucMul);
	}
	
	public int calcCritRate(){
		return (int)(basicCritRate+additionalCritRate*critRateMul);
	}
	
	public int getAtkSuc(){
		return atkSuc;
	}
	
	public int getDefSuc(){
		return defSuc;
	}
	
	public int getCritRate(){
		return critRate;
	}
	
	public short getCritdmg() {
		return critdmg;
	}

	public void setCritdmg(short critdmg) {
		this.critdmg = critdmg;
	}
	
	public void refreshStatPoints(){
		setStatPoints(level*3+(level/12)*2+52-(cStats[0]+cStats[1]+cStats[2]+cStats[3]+cStats[4]));
	}
	
	public void refreshSkillPoints(){
		setSkillPoints(level*2-2-skills.getUsedSkillpoints());
	}
	
	public Party getPt() {
		return pt;
	}

	public void setPt(Party pt) {
		this.pt = pt;
	}
	
	public Duel getDuel() {
		return duel;
	}

	public void setDuel(Duel duel) {
		this.duel = duel;
	}
	
	public boolean isReviveSave() {
		return reviveSave;
	}

	public void setReviveSave(boolean reviveSave) {
		this.reviveSave = reviveSave;
	}
	
	public Vendor getVendor() {
		return vendor;
	}
	
	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}
	
	public int getLastHit(){
		return lastHit;
	}
	
	public Doll getDoll() {
		return doll;
	}

	public void setDoll(Doll doll) {
		this.doll = doll;
	}
	
	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public void updateSpeed(){
		if(moveSyncTimer!=null){
			float tmp;
			if(equips.getSpeed()!=0)
				tmp=equips.getSpeed();
			else
				tmp=getSpeedWithoutItems();
			moveSyncTimer.newSpeed(tmp);
		}
	}
	
	public void setWalking(boolean walking){
		this.walking=walking;
		updateSpeed();
	}
	
	public void setTurboSpeed(float turboSpeed){
		this.turboSpeed=turboSpeed;
		updateSpeed();
	}
	
	public void updateLocation(float x, float y){
		/*
		if (this.timer != null){ if (!this.timer.isCompleted()) this.timer.cancel(); }
		if (WMap.distance(x, y, this.getX(), this.getY()) > this.syncDistance){
			this.activesyncTest(this.getX(), this.getY(), x, y);
		}*/
		
		synchronized(location){
			this.setX(x);
			this.setY(y);
			//System.out.println("X: "+x+"  Y: "+y);
			//System.out.println(CharacterDAO.saveCharacterLocation(this));
			try {
				Area t = this.grid.update(this);
				if (t != this.area){
					this.area.moveTo(this, t);
					this.area = t;
					List<Integer> ls;
					ls = this.area.addMemberAndGetMembers(this);
					synchronized(iniPackets){
			    	Iterator<Integer> it = this.iniPackets.iterator();
			    	while (it.hasNext()){
			    		Integer i = it.next();
			    		if (!ls.contains(i)){
			    			it.remove();
			    			System.out.println(this.charID + " removed player: " + i);
			    			if(this.wmap.getCharacter(i)!=null && !isBot)
			    				ServerFacade.getInstance().addWriteByChannel(this.wmap.getCharacter(i).GetChannel(), CharacterPackets.getVanishByID(this.charID));
			    		}
			    	}}
					ls.removeAll(iniPackets);
					this.sendInitToList(ls);
					this.iniPackets.addAll(ls);
				}
			} catch(OutOfGridException oe) {
				log.logMessage(Level.SEVERE, this, oe.getMessage() + " Illegal state for player: " + this.charID + " (moved outside grid) - disconnecting");
				if(!isBot)
					ServerFacade.getInstance().finalizeConnection(this.GetChannel());
			}
		}
	}
	
	public void updateFame(Character cur) {
		cur.addWritePacketWithId(CharacterPackets.getFameVendingPacket(cur));
	}
		
	private void sendInitToList(List<Integer> ls) {
		Iterator<Integer> it = ls.iterator();
		Integer t = null;
		while(it.hasNext()){
			t = it.next();
			if (this.wmap.CharacterExists(t) && t != this.getuid() && !wmap.getCharacterMap().get(t).isBot()){
				Character tmp = this.wmap.getCharacter(t);
				SocketChannel sc = tmp.GetChannel();
				ServerFacade.getInstance().getConnectionByChannel(sc).addWrite(CharacterPackets.getExtCharPacket(this,tmp));
				if(tmp.vendor != null) {
					ServerFacade.getInstance().getConnectionByChannel(this.GetChannel()).addWrite(CharacterPackets.getExtVending(tmp));
				}
			}
			else {
				it.remove();
			}
		}
	}


	// this method should only be called by active sync timer
	public void syncLocation(float x, float y){
		this.setX(x);
		this.setY(y);
		try {
			Area t = this.grid.update(this);
			if (t != this.area){
				this.area.moveTo(this, t);
				this.area = t;
				this.area.addMember(this);
			}
		} catch(OutOfGridException oe) {
			log.logMessage(Level.SEVERE, this, oe.getMessage() + " Illegal state for player: " + this.charID + " (moved outside grid) - disconnecting");
			ServerFacade.getInstance().finalizeConnection(this.GetChannel());
		}
	}

	/*
	 * Holy mother of a monster packet.
	 * Format a character data packet for this character based on it's attributes.
	 */
	public byte[] initCharPacket() {
        byte[] cdata = CharacterPackets.getCharPacket(this);
        this.setCharacterDataPacket(cdata);
        return cdata;
	}
	// send packet buf to all nearby players
	public void sendToMap(byte[] buf) {
		synchronized(this.iniPackets) {
			Iterator<Integer> iter = this.iniPackets.iterator();
				while(iter.hasNext()) {
					Integer plUid = iter.next();               
					if (plUid != this.charID){
						Character ch = this.wmap.getCharacter(plUid.intValue());
						if(ch != null && !ch.isBot()) {
							ServerFacade.getInstance().addWriteByChannel(ch.GetChannel(), buf);
						}
					}
				}
		}
		//this.area.sendToMembers(this.getuid(), buf);
	}
	
	// receive updated list for nearby objects
	public void updateEnvironment(Integer player, boolean add) {
		//System.out.println("Character: " + this.charID + " has got player list of size: " + players.size());
		if (this.iniPackets.contains(player) && !add && !wmap.getCharacter(player).isBot()){
			this.iniPackets.remove(player);
			ServerFacade.getInstance().addWriteByChannel(this.wmap.getCharacter(player).GetChannel(), CharacterPackets.getVanishByID(this.charID));
		}
		if (add && !this.iniPackets.contains(player)){
			this.iniPackets.add(player);
			this.sendInit(player);
		}
		
		// this.sendInit(player);
		//this.sendVanish();
	}
	// send initial packets to players who don't already have ours
	private void sendInit(Integer tmp) {
		if (this.wmap.CharacterExists(tmp) && tmp != this.getuid() && !wmap.getCharacterMap().get(tmp).isBot()){
			Character t = this.wmap.getCharacter(tmp);
			SocketChannel sc = t.GetChannel();
			if(sc!=null){
				ServerFacade.getInstance().getConnectionByChannel(sc).addWrite(CharacterPackets.getExtCharPacket(this,t));
				if(dead)
					ServerFacade.getInstance().getConnectionByChannel(sc).addWrite(CharacterPackets.getDeathPacket(this));
			}
			// if (this.vanish.containsKey(tmp)) this.vanish.remove(tmp);
		}
	}

	public Waypoint getLocation() {
		return this.location;
	}
	
	public void sendSpawnPacket(){
		
		ServerFacade.getInstance().getConnectionByChannel(GetChannel()).addWrite(CharacterPackets.getSpawnPacket(this));
		ServerFacade.getInstance().getConnectionByChannel(GetChannel()).addWrite(VendorPackets.getVendorListPacket(this));
		//TO DO (these packets just dont fix the 30sec cd problem)
		/*ServerFacade.getInstance().getConnectionByChannel(GetChannel()).addWrite(CharacterPackets.getJoinGameWorldStuffPacket1(this));
		ServerFacade.getInstance().getConnectionByChannel(GetChannel()).addWrite(CharacterPackets.getJoinGameWorldStuffPacket2(this));
		ServerFacade.getInstance().getConnectionByChannel(GetChannel()).addWrite(CharacterPackets.getJoinGameWorldStuffPacket3(this));
		ServerFacade.getInstance().getConnectionByChannel(GetChannel()).addWrite(CharacterPackets.getJoinGameWorldStuffPacket4(this));
		ServerFacade.getInstance().getConnectionByChannel(GetChannel()).addWrite(CharacterPackets.getJoinGameWorldStuffPacket5(this));
		ServerFacade.getInstance().getConnectionByChannel(GetChannel()).addWrite(CharacterPackets.getJoinGameWorldStuffPacket6(this));
		ServerFacade.getInstance().getConnectionByChannel(GetChannel()).addWrite(CharacterPackets.getJoinGameWorldStuffPacket7(this));
		ServerFacade.getInstance().getConnectionByChannel(GetChannel()).addWrite(CharacterPackets.getJoinGameWorldStuffPacket8(this));*/
		
	}
	
	public synchronized void gainExp(long expL, boolean updateExp){
		
		if (expL>0){
			if(updateExp)
				this.exp+=expL;
			if(CharacterMaster.getLvlCap()>level && CharacterMaster.getLvlExp(level)<=this.exp){
				lvlUp();
			}else{
				if(!isBot)
					ServerFacade.getInstance().getConnectionByChannel(GetChannel()).addWrite(CharacterPackets.getExpPacket(this, expL));
				setExp(this.exp);
			}
		}
		
	}
	
	public synchronized void decreaseExp(float percent){
		
		exp-=(long)(CharacterMaster.getLvlExp(level)*percent);
		if(exp<0)
			exp=0;
		setExp(exp);
		
	}
	
	public void lvlUp(){
		
		exp-=CharacterMaster.getLvlExp(level);
		level++;
		refreshStatPoints();
		refreshSkillPoints();
		calculateCharacterStats();
		
		if(CharacterMaster.getLvlCap()>level && CharacterMaster.getLvlExp(level)<exp){
			lvlUp();
		}else{
		
			byte[] lvlpckt = CharacterPackets.getLvlUpPacket(this);
			
			if(!isBot)
				ServerFacade.getInstance().getConnectionByChannel(GetChannel()).addWrite(lvlpckt);
			sendToMap(lvlpckt);
			
			setLevel(level);
			gainExp(exp,false);
			
			CharacterMaster.announceHighestLevel(this);
			
		}	
	}
	
	public void setCStatsInCharwindow(short[] stats) throws CharacterException{
		
		int totalStatpoints=52+level*3+(level/12)*2;
		int amount=0;
		for(int i=0;i<5;i++){
			amount+=stats[i];
		}
		
		if(amount>totalStatpoints){
			throw new CharacterException("Cannot add c points [too many spent]");
		}
		
		setCStats(stats);
		setStatPoints(totalStatpoints-amount);
		calculateCharacterStats();
		
	}
	
	public void die(){
		
		dead=true;
		
		if(isLastHitCharacter())
			setReviveSave(true);
		
		if(!isBot && duel==null && !isInPtDuel())
			CharacterDAO.saveCharacterDead(this);
		if(doll!=null){
			if(respawnTimer!=null)
				respawnTimer.cancel();
			respawnTimer=new Timer();
			respawnTimer.schedule(new TimerTask() {
				  @Override
				  public void run() {
					  synchronized(Character.this){Character.this.revive(true);}
				  }
			}, 10000);
			doll.stopActions();
		}
		healingTimer.cancel();
		
		stopMovement();
		
		byte[] deathpckt=CharacterPackets.getDeathPacket(this);
		
		if(!isBot)
			ServerFacade.getInstance().getConnectionByChannel(GetChannel()).addWrite(deathpckt);
		
		if(duel!=null){
			duel.loseDuel(this);
		}
		
		leavePtDuel();
		
		sendToMap(deathpckt);
		
	}
	
	public void equipStandardEquipment(){
		
		Map<Integer, ItemInInv> equipments = getEquips().getEquipments();
		switch(characterClass){
			case 1:{
				equipments.put(0, new ItemInInv(210110101));
				equipments.put(1, new ItemInInv(207114101));
				equipments.put(3, new ItemInInv(202110103));
				equipments.put(4, new ItemInInv(203110102));
				equipments.put(6, new ItemInInv(209114101));
				equipments.put(7, new ItemInInv(201011002));
				equipments.put(9, new ItemInInv(208114113));
				equipments.put(10, new ItemInInv(208114113));
				equipments.put(11, new ItemInInv(206110102));
				break;
			}
			case 2:{
				equipments.put(0, new ItemInInv(210220101));
				equipments.put(1, new ItemInInv(207224101));
				equipments.put(3, new ItemInInv(202220103));
				equipments.put(4, new ItemInInv(203220102));
				equipments.put(6, new ItemInInv(209225101));
				equipments.put(7, new ItemInInv(201011008));
				equipments.put(9, new ItemInInv(208224113));
				equipments.put(10, new ItemInInv(208224113));
				equipments.put(11, new ItemInInv(206220102));
				break;
			}
			case 3:{
				equipments.put(0, new ItemInInv(210130101));
				equipments.put(1, new ItemInInv(207134101));
				equipments.put(3, new ItemInInv(202130103));
				equipments.put(4, new ItemInInv(203130102));
				equipments.put(6, new ItemInInv(209130001));
				equipments.put(7, new ItemInInv(201011014));
				equipments.put(9, new ItemInInv(208134113));
				equipments.put(10, new ItemInInv(208134113));
				equipments.put(11, new ItemInInv(206130102));
				break;
			}
			case 4:{
				equipments.put(0, new ItemInInv(210140101));
				equipments.put(1, new ItemInInv(207144101));
				equipments.put(3, new ItemInInv(202140103));
				equipments.put(4, new ItemInInv(203140102));
				equipments.put(6, new ItemInInv(209140101));
				equipments.put(7, new ItemInInv(201011020));
				equipments.put(9, new ItemInInv(208144113));
				equipments.put(10, new ItemInInv(208144113));
				equipments.put(11, new ItemInInv(206140102));
				break;
			}
			default:{
				
				break;
			}
		}
		getEquips().saveEquip();
		getEquips().calculateEquipStats();
		calculateCharacterStats();
		if(!isBot)
			CharacterDAO.saveEquipments(getCharID(), getEquips());
		
	}
	
	public void addWritePacketWithId(byte[] buf){
		
		byte[] b=buf.clone();
		byte[] cid=BitTools.intToByteArray(getCharID());
		for(int i=0;i<4;i++){
			b[12+i]=cid[i];
		}
		ServerFacade.getInstance().addWriteByChannel(GetChannel(), b);
		
	}
	
	public void leavePt(){
		if(pt!=null){
			Party tmp=pt;
			tmp.leaveParty(this);
			addWritePacketWithId(PartyPackets.getLeavePacket(this, tmp));
			tmp.sendToMembers(PartyPackets.getLeavePacket(this, tmp),null);
		}
	}
	
	public boolean isInPtDuel(){
		if(pt==null)
			return false;
		if(pt.getPartyDuel()==null)
			return false;
		if(!pt.getPartyDuel().isStillInDuel(this))
			return false;
		return true;
	}
	
	public void leavePtDuel(){
		if(pt!=null && pt.getPartyDuel()!=null && pt.getPartyDuel().isStillInDuel(this)){
			pt.getPartyDuel().leaveDuel(this);
		}
	}
	
	private boolean isLastHitCharacter(){
		return WMap.getInstance().CharacterExists(lastHit);
	}
	
	//let the dolls talk!
	public void sendChatToDolls(String text) {
		Cleverdoll cd=Cleverdoll.getInstance();
		if(cd!=null){
			int i=1;
			boolean found=false;
			while(found==false && i<text.length()){
				if(text.charAt(i)==':')
					found=true;
				i++;
			}
			String name="";
			if(found){
				name=text.substring(0, i-1);
				if(text.length()>i)
					text=text.substring(i);
				else
					return;
			}else{
				return;
			}
			Character ch=(Character)area.getEnemyNear(name);
			if(ch!=null){
				cd.thinkAbout(ch.getDoll(), text);
			}
		}
	}
	
	//move to new location
	public void startMoveTo(float x, float y){
		moveSyncTimer.newTarget(new Waypoint(x,y));
	}
	
	public float getSpeedWithoutItems(){
		if(turboSpeed!=0)
			return turboSpeed;
		if(walking==true)
			return CharacterMaster.getWalkingspeed();
		return CharacterMaster.getRunningspeed();
	}
	
	public void stopMovement(){
		moveSyncTimer.stopMovement();
	}

	
}
