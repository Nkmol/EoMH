package Player.Dolls;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import timer.PotsAndDmgTimer;
import item.EquipableItem;
import item.EquipableSetItem;
import item.ItemInInv;
import item.inventory.Equipments;
import Skills.DollSkills;
import Skills.SkillException;
import Skills.SkillMaster;
import Tools.BitTools;
import World.Location;
import World.WMap;
import Player.Character;
import Player.CharacterMaster;
import Player.CharacterPackets;
import Player.Fightable;

public class Doll {

	private String name;
	public Character doll;
	private Timer timer;
	private int uid;
	private float targetX=0;
	private float targetY=0;
	private int skillindex=4;
	private Timer potsTimer;
	private Location aggroTarget;
	private boolean fighting=false;
	private boolean stopCd=false;
	private Timer stopTimer;
	private DollSkills dskills=new DollSkills(this);
	private float hardness=1;
	
	public Doll(int uid, String name){
		this.name=name;
		this.uid=uid;
	}
	
	private void startPotsTimer(){
		potsTimer=new Timer();
		potsTimer.scheduleAtFixedRate(new PotsAndDmgTimer(this,(short)0,(short)0),500,500);
	}
	
	public void despawn(){
		doll.leaveGameWorld(true,true);
		stopActions();
		if(potsTimer!=null)
			potsTimer.cancel();
	}
	
	public Character getDoll(){
		return doll;
	}
	
	public void spawn(int map, float x, float y, String name, int characterClass, Equipments eq, LinkedList<Integer> testByteIndexExt, 
			LinkedList<Byte> testByteValueExt, boolean equipStandardEquipment){
		
		this.name=name;
		doll = new Character(this);
		doll.setCurrentMap(map);
		doll.setX(x);
		doll.setY(y);
		doll.setNewName(name);
		doll.setCharacterClass(characterClass);
		doll.setCharID(uid);
		if(eq!=null){
			for(int i=0;i<17;i++){
				if(eq.getEquipmentsSaved().containsKey(i))
					doll.getEquips().getEquipmentsSaved().put(i, eq.getEquipmentsSaved().get(i));
			}
		}
		
		short[] dats = new short[5];
		
		dats[0] = 10; //INT 
		dats[2] = 10; //VIT 
		dats[1] = 10; //AGI 
		dats[4] = 10; //STR 
		dats[3] = 10; //DEX 
	
		doll.setStats(new short[5]);
		doll.setCStats(dats);
		
		if(equipStandardEquipment)
			doll.equipStandardEquipment();
		
		if(doll.getEquips()!=null){
			doll.createCharacterStats();
			doll.getEquips().calculateEquipStats();
		}
		
		doll.setTestByteIndexExt(testByteIndexExt);
		doll.setTestByteValueExt(testByteValueExt);
		
		doll.joinGameWorld(true,true);
		
		doll.setHp((short)doll.getMaxhp());
		doll.setMana((short)doll.getMaxmana());
		doll.setStamina((short)doll.getMaxstamina());
		
		startPotsTimer();
		
	}
	
	private void spawnClone(int map, float x, float y, String name, Character toCopy){
		
		this.name=name;
		doll = new Character(this);
		doll.setCurrentMap(map);
		doll.setX(x);
		doll.setY(y);
		doll.setNewName(name);
		doll.setCharacterClass(toCopy.getCharacterClass());
		doll.setCharID(uid);
		doll.setLevel(toCopy.getLevel());
		doll.setFace(toCopy.getFace());
		doll.setSize(toCopy.getSize());
		doll.setKao(toCopy.getKao());
		doll.setFaction(toCopy.getFaction());
		Equipments eq=toCopy.getEquips();
		if(eq!=null){
			for(int i=0;i<17;i++){
				if(eq.getEquipmentsSaved().containsKey(i))
					doll.getEquips().getEquipmentsSaved().put(i, eq.getEquipmentsSaved().get(i));
			}
		}
	
		doll.setStats(toCopy.getStats());
		doll.setCStats(toCopy.getCStats());
		
		if(doll.getEquips()!=null){
			doll.createCharacterStats();
			doll.getEquips().calculateEquipStats();
		}
		
		doll.joinGameWorld(true,true);
		
		doll.setHp((short)doll.getMaxhp());
		doll.setMana((short)doll.getMaxmana());
		doll.setStamina((short)doll.getMaxstamina());
		
		dskills=new DollSkills(this,toCopy.getSkills().getCastableSkills(),toCopy.getSkills().getBasicSkill());
		
		startPotsTimer();
		
	}
	
	public void goToLocationApprox(Location loc){
		
		if(WMap.distance(targetX, targetY, loc.getLocation().getX(), loc.getLocation().getY())>15){//targetX!=loc.getLocation().getX() || targetY!=loc.getLocation().getY()){
			targetX=loc.getLocation().getX();
			targetY=loc.getLocation().getY();
			float tmpx, tmpy;
			if(doll.getlastknownX()>targetX)
				tmpx=targetX+5;
			else
				tmpx=targetX-5;
			if(doll.getlastknownY()>targetY)
				tmpy=targetY+5;
			else
				tmpy=targetY-5;
			
			tmpx+=Math.random()*10-5;
			tmpy+=Math.random()*10-5;
			targetX=tmpx;
			targetY=tmpy;
			moveToPoint(tmpx,tmpy);
		}
		
	}
	
	public void moveToPoint(float px, float py){
		
		byte[] x = BitTools.floatToByteArray(px);
		byte[] y = BitTools.floatToByteArray(py);
		
		doll.startMoveTo(px, py);
		
		byte[] id = BitTools.intToByteArray(doll.getCharID());
		byte externmove[] = new byte[48]; 
		
		externmove[0] = (byte)externmove.length;
		externmove[4] = (byte)0x05;
		externmove[6] = (byte)0x0D;
		
		externmove[8]  = (byte)0x01;
		
		for(int i=0;i<4;i++) {
			
			externmove[i+20] = x[i];
			externmove[i+24] = y[i];			   
			externmove[i+28] = x[i];
			externmove[i+32] = y[i];			
			externmove[i+12] = id[i];
		}
		
		externmove[36]=(byte)0x01;
		
		doll.sendToMap(externmove);
		
	}
	
	public void equipItem(int itemID){
		
		ItemInInv it = new ItemInInv(itemID,1);
		if(it!=null && (it.getItem() instanceof EquipableItem || it.getItem() instanceof EquipableSetItem)){
			doll.getEquips().getEquipmentsSaved().put(new Integer(((EquipableItem)(it.getItem())).getEquipSlot()), it);
			doll.getArea().sendToMembers(doll.getuid(), CharacterPackets.getExtEquipPacket(doll, (byte)((EquipableItem)(it.getItem())).getEquipSlot(),it.getItem().getId()));
			doll.getEquips().calculateEquipStats();
			doll.calculateCharacterStats();
		}
		
	}
	
	public void duplicateCharacter(Character cur){
		
		despawn();
		spawnClone(doll.getCurrentMap(),doll.getlastknownX(),doll.getlastknownY(),cur.getName()+"X",cur);
		
	}
	
	public void startRandomMoves(){
		
		stopActions();
		timer=new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			  @Override
			  public void run() {
				  Doll.this.moveToPoint(doll.getlastknownX()+(float)(Math.random()*200-100),doll.getlastknownY()+(float)(Math.random()*200-100));
			  }
		},5000,5000);
		
	}
	
	public void startFollowCharacter(Character cur){
		
		aggroTarget=cur;
		stopActions();
		timer=new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			  @Override
			  public void run() {
				  Doll.this.goToLocationApprox(aggroTarget);
			  }
		},1000,1000);
		
	}
	
	public void startAnnoyCharacter(String name){
		
		setAggroTarget(name);
		if(aggroTarget!=null){
			startAnnoyCharacter();
		}
		
	}
	
	public void startAnnoyCharacter(int uid){
		
		setAggroTarget(uid);
		if(aggroTarget!=null){
			startAnnoyCharacter();
		}
		
	}
	
	public void startAnnoyCharacter(){
		
		if(fighting==false && aggroTarget!=null && stopCd==false){
			stopActions();
			fighting=true;
			timer=new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					byte mobtargets=0;
					byte chartargets=0;
					if(aggroTarget instanceof Character)
						chartargets=1;
					else
						mobtargets=1;
					try{
						if(((Fightable) aggroTarget).getHp()>0 && !doll.isDead()){
							Doll.this.goToLocationApprox(aggroTarget);
							if(WMap.distance(aggroTarget.getlastknownX(), aggroTarget.getlastknownY(), doll.getlastknownX(), doll.getlastknownY())<=25)
								SkillMaster.castSimpleSkill(null, doll, getNextSkill(), (byte)0, chartargets, mobtargets, new int[]{aggroTarget.getuid()},false);
						}else{
							if(doll.isInPtDuel()){
								setAggroTarget(doll.getPt().getPartyDuel().getRandomTarget(doll.getPt()));
							}else{
								stopActions();
							}
						}
					}catch(SkillException e){
						try{
							SkillMaster.castSimpleSkill(null, doll, dskills.getBasicSkill(), (byte)0, chartargets, mobtargets, new int[]{aggroTarget.getuid()},false);
						}catch(SkillException e2){
							try{
								SkillMaster.castSimpleSkill(null, doll, SkillMaster.getWoodenSkillId(doll.getCharacterClass()), (byte)0, chartargets, mobtargets, new int[]{aggroTarget.getuid()},false);
							}catch(Exception e3){
								stopActions();
								e3.printStackTrace();
							}
						}catch(Exception e2){
							stopActions();
							e.printStackTrace();
						}
					}catch(Exception e){
						stopActions();
						e.printStackTrace();
					}
				}
			},1000,1000);
		}
		
	}
	
	public void stop(){
		stopActions();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}
	
	public int getNextSkill(){
		if(skillindex==4)
			skillindex=0;
		else
			skillindex++;
		if(dskills.getHighestSkills()[skillindex]==0){
			return dskills.getBasicSkill();
		}
		return dskills.getHighestSkills()[skillindex];
	}
	
	public boolean healHp(short hp){
		if(doll.getHp()<doll.getMaxhp() && doll.getHp()<CharacterMaster.getHpcap()){
			doll.addHp(hp);
			sendHealPacketToPlayers(273000242);
			return true;
		}
		return false;
	}
	
	public boolean healMana(short mana){
		if(doll.getMana()<doll.getMaxmana() && doll.getMana()<CharacterMaster.getManacap()){
			doll.addMana(mana);
			sendHealPacketToPlayers(273000243);
			return true;
		}
		return false;
	}
	
	public void sendHealPacketToPlayers(int item){
		doll.sendToMap(CharacterPackets.getExtUseItemPacket(doll, item));
	}
	
	public short getHpManaPotValueByLvl(){
		int lvl=doll.getLevel();
		if(lvl>84)
			return 300;
		if(lvl>60)
			return 220;
		if(lvl>32)
			return 140;
		if(lvl>24)
			return 80;
		return 40;
	}
	
	public void setAggroTarget(int uid){
		aggroTarget=(Location)doll.getArea().getFightableNear(uid);
	}
	
	public void setAggroTarget(String name){
		aggroTarget=(Location)doll.getArea().getEnemyNear(name);
	}
	
	public synchronized void stopActions(){
		if(timer!=null)
			timer.cancel();
		fighting=false;
		stopCd=true;
		stopTimer=new Timer();
		stopTimer.schedule(new TimerTask() {
			  @Override
			  public void run() {
				  stopCd=false;
				  cancel();
			  }
		}, 5000);
	}
	
	public void learnSkill(int skillid, boolean checkIfAllowed){
		if(checkIfAllowed){
			try{
			SkillMaster.canLearnSkill(doll, skillid);
			}catch(SkillException e){
				return;
			}
		}
		dskills.learnSkill(skillid);
	}

	public float getHardness() {
		return hardness;
	}

	public void setHardness(float hardness) {
		this.hardness = hardness;
		doll.calculateCharacterStats();
	}
	
	public void writeInPublicChat(String s){
		char[] ctext = s.toCharArray();
		char[] ntext = doll.getName().toCharArray();
		byte[] btext = new byte[ctext.length];
		byte[] bntext = new byte[ntext.length];
		
		for(int i=0;i<ctext.length;i++){
			btext[i]=(byte)ctext[i];
		}
		
		for(int i=0;i<ntext.length;i++){
			bntext[i]=(byte)ntext[i];
		}
		
		byte[] msg = new byte[45+btext.length];
		
		msg[0] = (byte)(45+btext.length);
		msg[4] = (byte)0x05;
		msg[6] = (byte)0x07;
		msg[8] = (byte)0x01;
		
		msg[17] = (byte)0x01;
		
		//shout chat color
		msg[18] = (byte)0x06;
		
		//name
		for(int i=0;i<ntext.length;i++){
			msg[20+i]=bntext[i];
		}
		
		msg[40] = (byte)btext.length;
		
		//text
		for(int i=0;i<btext.length;i++)
			msg[44+i]=btext[i];
		
		msg[btext.length+44] = (byte)0x00;
		
		doll.sendToMap(msg);
	}
	
}
