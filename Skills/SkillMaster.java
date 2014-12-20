package Skills;

import java.util.HashMap;
import java.util.Map;

import Mob.Mob;
import Player.Character;
import Player.Fightable;
import Tools.BitTools;
import World.Location;
import World.OutOfGridException;
import World.WMap;
import Buffs.BuffMaster;
import Buffs.BuffsException;
import Buffs.SkillBuff;
import Connections.Connection;
import Database.SkillDAO;
import GameServer.GamePackets.PaketException;

public class SkillMaster {

	private static Map<Integer, SkillFrame> skills;
	private static Map<Integer, Integer> knockSkills;
	private static Map<Integer, Integer> woodenSkills;
	private static Map<Integer, Integer> standardBasicSkills;
	
	public static void loadAllSkills(){
		
		skills=SkillDAO.getInstance().getAllSkills();
		System.out.println(skills.size()+" skills are loaded!");
		
		knockSkills=new HashMap<Integer, Integer>();
		knockSkills.put(1, 121100050);
		knockSkills.put(2, 122200050);
		knockSkills.put(3, 121300050);
		knockSkills.put(4, 121400050);
		woodenSkills=new HashMap<Integer, Integer>();
		woodenSkills.put(1, 121103060);
		woodenSkills.put(2, 122206060);
		woodenSkills.put(3, 121309060);
		woodenSkills.put(4, 121413050);
		System.out.println("knock and wooden skills are loaded!");
		standardBasicSkills=new HashMap<Integer, Integer>();
		standardBasicSkills.put(1, 131101011);
		standardBasicSkills.put(2, 131102011);
		standardBasicSkills.put(3, 131103011);
		standardBasicSkills.put(4, 132204011);
		standardBasicSkills.put(5, 132205011);
		standardBasicSkills.put(6, 132206011);
		standardBasicSkills.put(7, 131307011);
		standardBasicSkills.put(8, 131308011);
		standardBasicSkills.put(9, 131309011);
		standardBasicSkills.put(10, 131411011);
		standardBasicSkills.put(11, 131411011);
		standardBasicSkills.put(12, 131411011);
		System.out.println("standard basic skills are loaded!");
		
	}
	
	public static SkillFrame getSkill(int id){
		if(!skills.containsKey(id))
			return null;
		return skills.get(id);
	}
	
	public static int getKnockSkillId(int chclass){
		return knockSkills.get(chclass);
	}
	
	public static int getWoodenSkillId(int chclass){
		return woodenSkills.get(chclass);
	}
	
	public static int getStandardBasicSkillId(int wep){
		return standardBasicSkills.get(wep);
	}
	
	public static void canLearnSkill(Character ch, int id) throws SkillException{
		
		SkillFrame skill=skills.get(id);
		
		if(skill.getChclass()!=ch.getCharacterClass() && skill.getChclass()!=0){
			throw new SkillException("Cannot learn skill [wrong character class]");
		}
		if(skill.getLvl()>ch.getLevel()){
			throw new SkillException("Cannot learn skill [lvl too low]");
		}
		if(skill.getFaction()!=0 && skill.getFaction()!=ch.getFaction()){
			throw new SkillException("Cannot learn skill [wrong faction]");
		}
		if(skill.getSkillpoints()>ch.getSkillPoints()){
			throw new SkillException("Cannot learn skill [not enough skillpoints]");
		}
		if((!ch.getSkills().getLearnedSkills().containsValue(skill.getReqSkill1()) && skill.getReqSkill1()!=0)
		|| (!ch.getSkills().getLearnedSkills().containsValue(skill.getReqSkill2()) && skill.getReqSkill2()!=0)
		|| (!ch.getSkills().getLearnedSkills().containsValue(skill.getReqSkill3()) && skill.getReqSkill3()!=0)){
			throw new SkillException("Cannot learn skill [you do not have the req skills]");
		}
		
	}
	
	public static void canCastSkill(Character ch, int id) throws SkillException{
		
		if(!(skills.get(id) instanceof CastableSkill)){
			throw new SkillException("Cannot cast skill [not castable]");
		}
		
		CastableSkill skill=(CastableSkill)skills.get(id);
		
		if(skill.getLvl()>ch.getLevel()){
			throw new SkillException("Cannot cast skill [lvl too low]");
		}
		if(skill.getFaction()!=0 && skill.getFaction()!=ch.getFaction()){
			throw new SkillException("Cannot cast skill [wrong faction]");
		}
		if(skill.getNeedsWepToCast()!=46 && skill.getNeedsWepToCast()!=0 && 
		(!ch.getEquips().getEquipments().containsKey(7) || 
		(skill.getNeedsWepToCast()!=55 && skill.getNeedsWepToCast()!=45 && ch.getEquips().getEquipments().get(7).getItem().getCategory()!=skill.getNeedsWepToCast()))){
			throw new SkillException("Cannot cast skill [you do not have the req wep]");
		}
		if(skill.getManaCost()>ch.getMana()){
			throw new SkillException("Cannot cast skill [not enough mana]");
		}
		if(skill.getStaminaCost()>ch.getStamina()){
			throw new SkillException("Cannot cast skill [not enough stamina]");
		}
		if(skill.getUltiSetId()!=0 && !ch.getEquips().getFullSets().containsKey(skill.getUltiSetId())){
			throw new SkillException("Cannot cast skill [you do not have the req set]");
		}
		
	}
	
	public static void canCastSkillStats(Character ch, int id) throws SkillException{
		
		if(!(skills.get(id) instanceof CastableSkill)){
			throw new SkillException("Cannot cast skill [not castable]");
		}
		
		CastableSkill skill=(CastableSkill)skills.get(id);
		
		if(skill.getManaCost()>ch.getMana()){
			throw new SkillException("Cannot cast skill [not enough mana]");
		}
		if(skill.getStaminaCost()>ch.getStamina()){
			throw new SkillException("Cannot cast skill [not enough stamina]");
		}
		
	}
	
	public static void canCastToTarget(Location ch, Location target) throws SkillException{
		
		if(target==null)
			throw new SkillException("Cannot cast skill [target doesnt exist]");
		if(WMap.distance(ch.getlastknownX(), ch.getlastknownY(), target.getlastknownX(), target.getlastknownY())>150)
			throw new SkillException("Cannot cast skill [target is too far away]");
		
	}
	
	public static int getSkillIdFromCast(Character ch, byte decrypted) throws SkillException{
		
		int skillidInt;
        int key=(int) decrypted;
        if(ch.getSkills().getCastableSkills().containsKey(key)){
        	skillidInt=ch.getSkills().getCastableSkills().get(key).getId();
        }else{
        	if(decrypted==(byte)0xFF){
        		if(ch.getEquips().getEquipmentsSaved().containsKey(7))
        			skillidInt=SkillMaster.getWoodenSkillId(ch.getCharacterClass());
        		else
        			skillidInt=SkillMaster.getKnockSkillId(ch.getCharacterClass());
        	}else{
        		throw new SkillException("Cannot cast skill [skill not learned]");
        	}
        }
        
        return skillidInt;
		
	}
	
	public static boolean isSkillReady(Character ch, byte decrypted){
		
        int key=(int) decrypted;
        if(ch.getSkills().getCastableSkills().containsKey(key)){
        	return ch.getSkills().getCastableSkills().get(key).isReady();
        }else{
        	return true;
        }
		
	}
	
	public static void setSkillReadyFalse(Character ch, byte decrypted){
		
		int key=(int) decrypted;
        if(ch.getSkills().getCastableSkills().containsKey(key)){
        	SkillFrame skill=getSkill(ch.getSkills().getCastableSkills().get(key).getId());
        	if(skill.getTypeSpecific()!=5 && skill.getTypeSpecific()!=6 && skill.getTypeSpecific()!=7)
        		ch.getSkills().getCastableSkills().get(key).setReadyFalse();
        }
		
	}
	
	public static int skillCastDmgCalculations(Character ch, int skillId){
		
		int dmgInt=((CastableSkill)SkillMaster.getSkill(skillId)).getDmg()+ch.getAttack()+(ch.getMinDmg()+(int)(Math.random()*(ch.getMaxDmg()-ch.getMinDmg()+1)));
		
    	return dmgInt;
		
	}
	
	public static byte skillCastDmgTypeCalculations(Fightable cur, Fightable target, boolean canCrit){
		
		byte dmgType;

		if(cur.getAtkSuc()>=target.getDefSuc() || (int)(Math.random()/(Math.pow(2,(float)-(target.getDefSuc()-cur.getAtkSuc())/400f)))==0){
			
			if(cur.getCritRate()>=target.getDefSuc() || (int)(Math.random()/(Math.pow(2,(float)-(target.getDefSuc()-cur.getCritRate())/200f)))==0){
				
				if(canCrit && cur instanceof Character && target instanceof Mob && (int)(Math.random()*50)==0){
					dmgType=5;
				}else{
					dmgType=2;
				}
				
			}else{
				dmgType=1;
			}
			
		}else{
			dmgType=0;
		}
		
		/* old and outdated system based on lvls
		int lvldif=targetlvl-chlvl+1;
		if(lvldif<1){
			lvldif=1;
		}else if(lvldif>50){
			lvldif=50;
		}
		byte dmgType=1;
		
    	if(((int)(Math.random()*100))==0){
    		dmgType=5;
    	}else if(chlvl>targetlvl && ((int)(Math.random()*5))==0){
    		dmgType=2;
    	}else if(chlvl<targetlvl && ((int)(Math.random()*(60/lvldif)))==0){
    		dmgType=0;
    	}*/
    	
    	return dmgType;
		
	}
	
	public static float getDmgFactorByType(Fightable cur, int dmgType){
		
		//GREEN CRIT evil
		if(dmgType==5)
    		return 66;
		//WHITE CRIT
    	if(dmgType==2)
    		return 1.5f;
    	//MISS
    	if(dmgType==0)
    		return 0;
    	
    	return 1;
		
	}
	
	public static float getDmgFactorByClass(Character cur){
		
		switch(cur.getCharacterClass()){
			case 1:{
				return 1.2f;
			}
			case 2:{
				return 1.1f;
			}
			case 3:{
				return 1;
			}
			case 4:{
				return 1;
			}
			default:{
				return 1;
			}
		}
		
	}
	
	public static void castSimpleSkill(Connection con, Character cur, int skillidInt, byte skillActivationType,
			byte chartargets, byte mobtargets, int[] targetIds, boolean ignoreStats) throws PaketException,OutOfGridException,SkillException{
		
		//skillpckt1 is a packet of skilleffects e.g. buffs
        byte[] skillpckt1 = SkillPackets.getSkillEffectOnCharPacket(cur);
        //skillpckt2 is a packet of skill activation, different IDs and DMG
    	byte[] skillpckt2 = new byte[52];
    	//just for medi and turbo
    	byte[] skillpckt3 = new byte[28];
		
		//SkillID
    	int skillidNoFake=skillidInt;
    	
    	SkillFrame skill=SkillMaster.getSkill(skillidInt);
    	
    	if(ignoreStats==false)
    		canCastSkillStats(cur, skillidInt);
    	
    	//TURBO AND MEDI
    	if(skill.getTypeSpecific()==6 || skill.getTypeSpecific()==7){
    		
        	if(skill.getTypeSpecific()==6){
        		skillpckt3=SkillPackets.getMediPacket(cur, skillidInt, skillActivationType);
        	}else{
        		skillpckt3=SkillPackets.getTurboPacket(cur, skillidInt, skillActivationType==(byte)0xc8);
        		if(skillActivationType==(byte)0xc8)
        			cur.setTurboSpeed(((CastableSkill)skill).getSpeed());
        		else
        			cur.setTurboSpeed(0);
        	}
        	
        	if(con!=null)
        		con.addWrite(skillpckt3);
        	cur.sendToMap(skillpckt3);
        	return;
    		
    	}
    	
    	//TARGETS
    	int targets=chartargets+mobtargets;
    	
    	//COSTS
    	cur.addHpSigned(-((CastableSkill)SkillMaster.getSkill(skillidInt)).getHealCost());
    	cur.addManaSigned(-((CastableSkill)SkillMaster.getSkill(skillidInt)).getManaCost());
    	cur.addStaminaSigned(-((CastableSkill)SkillMaster.getSkill(skillidInt)).getStaminaCost());
    	
    	Fightable target;
    	
    	//GET MAXIMUM CASTERS DMG
    	int dmgInt=SkillMaster.skillCastDmgCalculations(cur, skillidInt);
    	int totalDmg;
    	int dmgType;
    	
    	//GET MAIN SKILL PACKET
    	skillpckt2 = SkillPackets.getCastSkillPacket(cur, targets, skillidNoFake, skillActivationType);
    	
    	//ADD TARGET STUFF TO THE PACKET
    	for(int aoe=0;aoe<targets;aoe++){
    		
    		//GET TARGET
    		byte[] targetByte=BitTools.intToByteArray(targetIds[aoe]);
    		if(cur.getArea().isNear(BitTools.byteArrayToInt(targetByte)))
    			target=cur.getArea().getFightableNear(BitTools.byteArrayToInt(targetByte));
    		else
    			throw new PaketException();
    		
        	totalDmg=dmgInt;
        	
        	//DECREASE DMG BY DEF
        	dmgType=SkillMaster.skillCastDmgTypeCalculations(cur, target, skill.getTypeSpecific()==2);
        	totalDmg-=target.getDefence();
        	
        	//CRIT
        	if(dmgType==2 || dmgType==5)
        		totalDmg+=cur.getCritdmg();
        	
        	//DMG TYPE FACTOR
        	totalDmg*=SkillMaster.getDmgFactorByType(cur,dmgType)*SkillMaster.getDmgFactorByClass(cur);
        	
        	if(totalDmg<0)
    			totalDmg=0;
        	
        	//ATK THE TARGET FINALLY
        	target.recDamage(cur.getuid(),totalDmg);
        	
        	int targetId=BitTools.byteArrayToInt(targetByte);
        	
        	//COMPLETE THE TARGET IN THE PACKAGE
        	skillpckt2 = SkillPackets.completeCastSkillPacket(skillpckt2, aoe, targetId, target.getHp(), target.getMana(), -totalDmg, chartargets, dmgType);
			
    		if(chartargets>0)
        		chartargets--;
    	}
    	
    	//send skill packet to other players
    	cur.sendToMap(skillpckt2);
    	
    	if(con!=null){
    		//send skill packet to client
    		con.addWrite(skillpckt2);
    		
    		//effects on char
    		con.addWrite(skillpckt1);
    	}
    	
    	//update clients hp mana stam
    	cur.refreshHpMpSp();
    	
    	cur.stopMovement();
		
	}
	
	public static byte[] castSkill(Connection con, Character cur, byte skillBarNumber, byte skillActivationType,
			byte chartargets, byte mobtargets, int[] targetIds) throws PaketException,SkillException,OutOfGridException{

		//skillpckt1 is a packet of skilleffects e.g. buffs
        byte[] skillpckt1 = SkillPackets.getSkillEffectOnCharPacket(cur);
        //skillpckt2 is a packet of skill activation, different IDs and DMG
    	byte[] skillpckt2 = new byte[52];
    	//just for medi and turbo
    	byte[] skillpckt3 = new byte[28];
		
		//SkillID
    	byte[] skillid;
    	int skillidInt=SkillMaster.getSkillIdFromCast(cur, skillBarNumber);
    	int skillidNoFake=skillidInt;
    	skillid=BitTools.intToByteArray(skillidInt);
    	
    	SkillMaster.canCastSkill(cur, skillidInt);
    	
    	SkillFrame skill=SkillMaster.getSkill(skillidInt);
    	
    	//TURBO AND MEDI
    	if(skill.getTypeSpecific()==6 || skill.getTypeSpecific()==7){
    		
        	if(skill.getTypeSpecific()==6){
        		skillpckt3=SkillPackets.getMediPacket(cur, skillidInt, skillActivationType);
        	}else{
        		skillpckt3=SkillPackets.getTurboPacket(cur, skillidInt, skillActivationType==(byte)0xc8);
        		if(skillActivationType==(byte)0xc8)
        			cur.setTurboSpeed(((CastableSkill)skill).getSpeed());
        		else
        			cur.setTurboSpeed(0);
        	}
        	
        	if(con!=null)
        		con.addWrite(skillpckt3);
        	cur.sendToMap(skillpckt3);
        	return null;
    		
    	}
    	
    	//COOLDOWN CAST
    	if(!cur.getSkillReady()){
    		throw new PaketException();
    	}else{
    		cur.setSkillReadyFalse();
    	}
    	
    	//COOLDOWN SKILL: STATS OF A BASIC SKILL + ANIMATION OF THE ACTUAL SKILL
    	boolean skillfake=false;
    	if(!SkillMaster.isSkillReady(cur, skillBarNumber)){
    		//basic skill if not ready
    		skillidInt=cur.getSkills().getBasicSkill();
    		//skillid=BitTools.intToByteArray(skillidInt);
    		if(chartargets+mobtargets>1){
    			if(chartargets>0){
    				chartargets=1;
    				mobtargets=0;
    			}else{
    				chartargets=0;
    				mobtargets=1;
    			}
    		}
    		skillfake=true;
    	}else{
    		SkillMaster.setSkillReadyFalse(cur, skillBarNumber);
    	}
    	
    	//TARGETS
    	int targets=chartargets+mobtargets;
    	
    	//COSTS
    	System.out.println("stamina costs: " + ((CastableSkill)SkillMaster.getSkill(skillidInt)).getStaminaCost());
    	cur.addHpSigned(-((CastableSkill)SkillMaster.getSkill(skillidInt)).getHealCost());
    	cur.addManaSigned(-((CastableSkill)SkillMaster.getSkill(skillidInt)).getManaCost());
    	cur.addStaminaSigned(-((CastableSkill)SkillMaster.getSkill(skillidInt)).getStaminaCost());
    	
    	Fightable target;
    	
    	//GET MAXIMUM CASTERS DMG
    	int dmgInt=SkillMaster.skillCastDmgCalculations(cur, skillidInt);
    	int totalDmg;
    	int dmgType;
    	
    	//GET MAIN SKILL PACKET
    	skillpckt2 = SkillPackets.getCastSkillPacket(cur, targets, skillidNoFake, skillActivationType);
    	
    	//ADD TARGET STUFF TO THE PACKET
    	for(int aoe=0;aoe<targets;aoe++){
    		
    		//GET TARGET
    		byte[] targetByte=BitTools.intToByteArray(targetIds[aoe]);
    		
    		if(cur.getArea().isNear(BitTools.byteArrayToInt(targetByte)))
    			target=cur.getArea().getFightableNear(BitTools.byteArrayToInt(targetByte));
    		else
    			throw new PaketException();
    		
    		//check for distance
    		canCastToTarget((Location)cur, (Location)target);
    		
        	totalDmg=dmgInt;
        	
        	if(skill.getTypeSpecific() != 3 && skill.getTypeSpecific() != 6 && skill.getTypeSpecific() != 7)
        		dmgType=SkillMaster.skillCastDmgTypeCalculations(cur, target, skill.getTypeSpecific()==2);
        	else
        		dmgType = (short)3;
        	
        	//DECREASE DMG BY DEF
        	totalDmg-=target.getDefence();
        	
        	//CRIT
        	if(dmgType==2 || dmgType==5)
        		totalDmg+=cur.getCritdmg();
        	
        	//DMG TYPE FACTOR
        	totalDmg*=SkillMaster.getDmgFactorByType(cur,dmgType)*SkillMaster.getDmgFactorByClass(cur);
        	
        	if(totalDmg<0)
    			totalDmg=0;
        	
        	//ATK THE TARGET FINALLY
        	if(skill.getTypeSpecific() != 3 && skill.getTypeSpecific() != 6 && skill.getTypeSpecific() != 7)
        		target.recDamage(cur.getuid(),totalDmg);
        	
        	int targetId=BitTools.byteArrayToInt(targetByte);
        	
        	//COMPLETE THE TARGET IN THE PACKAGE
        	skillpckt2 = SkillPackets.completeCastSkillPacket(skillpckt2, aoe, targetId, target.getHp(), target.getMana(), -totalDmg, chartargets, dmgType);
			
    		if(chartargets>0)
        		chartargets--;
    		
    		//new aggro target for pt dolls
    		if(aoe==0 && target!=cur){
    			if(cur.getPt()!=null){
    	    		cur.getPt().dollsGetNewAggro(cur, targetId);
    	    	}
    		}
    		//When not targeting self
    		if(cur.getCharID() != target.getuid())
    			cur.onAttack();
    		
    		if(target.isAlive()) {
	    		//BUFF
	            if(skill.getEffectsId()[0] > 0) {
			        //ItemBuff[] itembuff = new ItemBuff[conitem.getBuffId().length];
			        for(int i=0; i<skill.getEffectsId().length;i++) {
			        	if(skill.getEffectsId()[i] > 0) {
			        		long time = BuffMaster.timeClientToServer(skill.getEffectsDuration()[i]); // MH time = int * 4. Also converting to miliseconds
				            System.out.println("buffId: " + skill.getEffectsId()[i] + " buffValue: " + skill.getEffectsValue()[i] + " skillType: " + skill.getTypeSpecific() + "dmgType: " + dmgType);
				            SkillBuff buff = new SkillBuff(target, skill.getEffectsId()[i], time, skill.getEffectsValue()[i], cur.getCharID());
				            if(buff.getAction() == null) {
				            	System.out.println("Buffaction not created for buffid " + skill.getEffectsId()[i]);
				            	return null;
				            }
				        	//Run buff 
				            try {
								buff.activate();
							} catch (BuffsException e) {
								System.out.print(e.getMessage());
							}
			        	}
			       }
	            }
    		}
    	}
    	
    	//send basic atk packet to other players
    	if(skillfake){
    		skillid=BitTools.intToByteArray(skillidInt);
    		byte[] skillpckt4=new byte[skillpckt2.length];
    		for(int i=0;i<skillpckt2.length;i++){
    			skillpckt4[i]=skillpckt2[i];
    		}
    		for(int i=0;i<4;i++){
    			skillpckt4[20+i] = skillid[i];
    		}
    		cur.sendToMap(skillpckt4);
    	}else{
    		//send skill packet to other players
    		cur.sendToMap(skillpckt2);
    	}
    	
    	if(con!=null){
    		//send skill packet to client
    		con.addWrite(skillpckt2);
    		
    		//effects on char
    		con.addWrite(skillpckt1);
    	}
    	
    	//update clients hp mana stam
    	cur.refreshHpMpSp();
    	
    	cur.stopMovement();
    	
    	return null;
		
	}
	
	public static void reSkill(Character ch){
		
		ch.setCharacterSkills(new CharacterSkills(ch));
		
	}
	
	public static boolean canUpgrade(Character ch, int upgradeSkill){
		CharacterSkills skills=ch.getSkills();
		if(skills.getUpgradeSkillStage()<upgradeSkill)
			return false;
		return true;
	}
	
}
