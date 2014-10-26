package Player;

import item.ItemFrame;
import item.ItemInInv;

import java.nio.ByteBuffer;

import Buffs.Buff;
import Gamemaster.GameMaster;
import Skills.SkillFrame;
import Skills.SkillMaster;
import Tools.BitTools;

public class CharacterPackets {
	
	public static byte[] getHealPacket(Character ch){
		
		byte[] healpckt = new byte[32];
		healpckt[0] = (byte)healpckt.length;
		healpckt[4] = (byte)0x05;
		healpckt[6] = (byte)0x35;
		healpckt[8] = (byte)0x08; 
		healpckt[9] = (byte)0x60; 
		healpckt[10] = (byte)0x22;
		healpckt[11] = (byte)0x45;
		
		byte[] cid = BitTools.intToByteArray(ch.getCharID());
		
		byte[] bhp = BitTools.intToByteArray(ch.getHp());
		byte[] bmana = BitTools.intToByteArray(ch.getMana());
		byte[] bstam = BitTools.intToByteArray(ch.getStamina());
		
		healpckt[24] = bhp[0];
		healpckt[25] = bhp[1];
		
		healpckt[28] = bmana[0];
		healpckt[29] = bmana[1];
		
		healpckt[30] = bstam[0];
		healpckt[31] = bstam[1];	
		
		healpckt[16] = (byte)0x03;
		healpckt[18] = (byte)0x02;
		
		for(int i=0;i<4;i++) {
			healpckt[12+i] = cid[i];
		}
			
		return healpckt;
	
	}
	
	public static byte[] getFameVendingPacket(Character cur) {
    	int points = cur.getInventory().getVendingPoints();
    	byte[] fame = BitTools.intToByteArray(cur.getFame());
		byte[] cid = BitTools.intToByteArray(cur.getCharID());
    	byte[] fameVen = new byte[32];
    	
    	fameVen[0] = (byte)fameVen.length;
    	fameVen[4] = (byte)0x05;
    	fameVen[6] = (byte)0x43;
    	
    	fameVen[8] = (byte)0x01;
    	fameVen[10] = (byte)0x33;
    	fameVen[11] = (byte)0x08;

		for(int i=0;i<4;i++) {
			fameVen[12+i] = cid[i];
			fameVen[28+i] = fame[i];
		}
		
		fameVen[20] = (byte)cur.getFameTitle(); //fame title
		fameVen[21] = (byte)points;
		fameVen[22] = (byte)cur.getGMrank();
		fameVen[26] = (byte)0x02;
		fameVen[27] = (byte)0x03;
		
		return fameVen;
	}
	
	
	public static byte[] getCharPacket(Character ch){
		
		byte[] cdata = new byte[653];
        byte[] charname = (GameMaster.getGMprename(ch)+ch.getName()).getBytes();
        
        for(int i=0;i<charname.length;i++) {
                cdata[i] = charname[i];
        }
        
		byte[] fame = BitTools.intToByteArray(ch.getFame());
        
        for(int i=0;i<fame.length;i++) {
        	cdata[36+i] = fame[i];
        }
        
        if(ch.getCharacterClass() == 2) {
                cdata[40] = 0x02;       //gender byte(2 = female(yeah.. don't ask me why CRS add gender byte in their packet))
                cdata[48] = 0x02;       //sin class is 2
        } else {
            	cdata[40] = 0x01; //male(obviously duh)
                cdata[48] = (byte)ch.getCharacterClass(); //class byte
        }
        
        cdata[34]=(byte)ch.getFaction();	//faction
        if(ch.getFace()>0)
        	cdata[42]=(byte)ch.getFace();	//face
        else
        	cdata[42]=(byte)0x01;
        
        cdata[54] = (byte)ch.getLevel();
        
        cdata[460]=(byte)ch.getKao();		//kao
        cdata[462]=(byte)ch.getSize();		//size
        cdata[464]=(byte)ch.getFameTitle();	//fame title
        if(GameMaster.hasGMname(ch))		//GM name
        	cdata[466]=(byte)0x01;
        
		//0 int, 1 agi, 2 vit, 3 dex, 4 str | int, vit, agi, str, dex
        for(int i=0;i<ch.getCStats().length;i++) {
        	byte[] tmp = BitTools.shortToByteArray(ch.getCStats()[i]);
        	cdata[576+(i*2)] = tmp[0];
        	cdata[576+(i*2)+1] = tmp[1];
        }
        
        byte[] statp=BitTools.intToByteArray(ch.getStatPoints());
        byte[] skillp=BitTools.intToByteArray(ch.getSkillPoints());
        
        for(int i=0;i<2;i++){
        	cdata[604+i]=statp[i];
        	cdata[606+i]=skillp[i];
        }
                
        for(int i=0;i<16;i++) { cdata[i+17] = (byte)0x30; }
        
        cdata[44] = (byte)0x01; //standard its 01 but 00 gives no errors, no explanation yet
        cdata[52] = (byte)0x02; //standard its 02 but 00 goes as well nothing changes though, no explanation yet
                
        byte[] stuff = new byte[] { (byte)0x00, (byte)0xa0, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x7c, (byte)0x01, (byte)0x00,                               
                (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, 
                (byte)0xf3, (byte)0xd4, (byte)0x13, (byte)0xc5, (byte)0x9a, 
                (byte)0xb9, (byte)0xbd, (byte)0xc5, (byte)0x00, (byte)0x00, 
                (byte)0x02, (byte)0x00 }; 
        
     
        for(int i=0;i<stuff.length;i++) {
                cdata[i+66-8-3] = stuff[i];
        }
        
        //equip
        byte[] bytes;
        byte[] amount;
        for(int i=0;i<17;i++){
        	if (ch.getEquips().getEquipmentsSaved().containsKey(i)){
        		bytes = BitTools.intToByteArray(ch.getEquips().getEquipmentsSaved().get(i).getItem().getId());
        		amount =  BitTools.intToByteArray(ch.getEquips().getEquipmentsSaved().get(i).getAmount());
        		for(int j=0;j<4;j++){
        			cdata[80+i*12+j]=bytes[j];
        			cdata[84+i*12+j]=amount[j];
        		}
        	}
        }
        
        cdata[465] = (byte)ch.getInventory().getVendingPoints();
        
        //abandoned in char selection
        if(ch.isAbandoned())
        	cdata[648]=(byte)0x01;
        
        //TEST
        for(int i=0;i<ch.getTestByteIndex().size();i++)
        	cdata[ch.getTestByteIndex().get(i)]=ch.getTestByteValue().get(i);

        return cdata;
		
	}
	
	public static byte[] getExtCharPacket(Character ch, Character receiver){
		
		System.out.println("EXT CHAR PACKET");
		byte[] cedata = new byte[612];
		short length = (short)cedata.length;
		byte[] lengthbytes = BitTools.shortToByteArray(length);
		byte[] chID = BitTools.intToByteArray(ch.getCharID());
		byte[] chName = (GameMaster.getGMprename(ch)+ch.getName()).getBytes();
		byte[] xCoords = BitTools.floatToByteArray(ch.getlastknownX());
		byte[] yCoords = BitTools.floatToByteArray(ch.getlastknownY());
		
		cedata[0] = lengthbytes[0];
		cedata[1] = lengthbytes[1];
		cedata[4] = (byte)0x05;
		cedata[6] = (byte)0x01;
		cedata[8] = (byte)0x01;
		
		for(int i=0;i<4;i++) {
			cedata[i+12] = chID[i]; //character ID
			cedata[i+88] = xCoords[i]; //location x
			cedata[i+92] = yCoords[i]; //location y
		}
		
		for(int i=0;i<chName.length;i++) {
			cedata[i+20] = chName[i]; //characters Name
		}
		
		for(int i=0;i<16;i++) {
			cedata[37+i] = (byte)0x30; //character packets have 16 times 30(0 in ASCII) in row. Mysteries of CRS.
		}
		
		if(ch.getCharacterClass() == 2) {
			cedata[60] = (byte)0x02; //gender byte
			cedata[68] = (byte)0x02; //class byte
		} else {
			cedata[60] = (byte)0x01; //gender byte
			cedata[68] = (byte)ch.getCharacterClass(); //class byte
		}
		
		cedata[54] = (byte)ch.getFaction(); //faction
		
		if(ch.getFace()>0)
        	cedata[62]=(byte)ch.getFace();	//face
        else
        	cedata[62]=(byte)0x01;
		
		cedata[74]=(byte)ch.getLevel();		//level
		
		//equip
        byte[] bytes;
        for(int i=0;i<17;i++){
        	if (ch.getEquips().getEquipmentsSaved().containsKey(i)){
        		bytes = ByteBuffer.allocate(4).putInt(ch.getEquips().getEquipmentsSaved().get(i).getItem().getId()).array();
        		for(int j=0;j<4;j++)
        			cedata[100+i*12+j]=bytes[3-j];
        	}
        }
        
        if(ch.getPt()!=null && receiver.getPt()!=null && ch.getPt().getPartyDuel()!=null && 
        	ch.getPt().getPartyDuel()==receiver.getPt().getPartyDuel() && ch.isInPtDuel() && receiver.isInPtDuel())
        	cedata[480]=(byte)2;					//fakekao
        else
        	cedata[480]=(byte)ch.getKao();			//kao
        
        cedata[482]=(byte)ch.getSize();		//size
        
        cedata[484] =(byte)ch.getFameTitle();//Fame title
        
        if(GameMaster.hasGMname(ch))		//GM name
        	cedata[486]=(byte)0x01;
        
        //TEST
        for(int i=0;i<ch.getTestByteIndexExt().size();i++)
        	cedata[ch.getTestByteIndexExt().get(i)]=ch.getTestByteValueExt().get(i);
        
        return cedata;
		
	}
	
	public static byte[] getSpawnPacket(Character ch){
		
		byte[] chID = BitTools.intToByteArray(ch.getCharID());
		byte[] chX = BitTools.floatToByteArray(ch.getlastknownX());
		byte[] chY = BitTools.floatToByteArray(ch.getlastknownY());
		byte[] coins = BitTools.intToByteArray(ch.getInventory().getCoins());
		byte[] cdata = new byte[5824];
		byte[] cdatalength = BitTools.intToByteArray(cdata.length);
		
		cdata[4] = (byte)0x04;
		cdata[6] = (byte)0x01;
		cdata[8] = (byte)0x01;
		
		for(int i=0;i<4;i++){
			cdata[i]=cdatalength[i];
			cdata[12+i]=chID[i];
		}
		
		cdata[20] = (byte)ch.getCurrentMap();
		//TIME
		cdata[24] = (byte)0x08;
		cdata[28] = (byte)0x05;
		cdata[29] = (byte)0x05;
		cdata[30] = (byte)0x11;

		//quest active id. Just one byte?
		//cdata[2166] = (byte)0x00;

		byte[] itID;
		byte[] itAmount;
		ItemInInv item;
		for(int i=0;i<240;i++){
			
			cdata[2252+i*12]=0;
			if(ch.getInventory().getSeqSaved().get(i)!=-1){
				item=ch.getInventory().getInvSaved().get(ch.getInventory().getSeqSaved().get(i));
				if(item!=null){
					itID=BitTools.intToByteArray(item.getItem().getId());
					itAmount=BitTools.intToByteArray(item.getAmount());
					for(int j=0;j<4;j++){
						cdata[2252+i*12+j]=itID[j];
						cdata[2252+i*12+j+4]=itAmount[j];
					}
					
					//row
					cdata[2252+i*12-2]=(byte)(ch.getInventory().getSeqSaved().get(i)/100);
					//line
					cdata[2252+i*12-1]=(byte)(ch.getInventory().getSeqSaved().get(i)%100);
				}
			}
			
		}
		
		//skillbar
		int barIdInt;
		byte[] barId;
		SkillFrame skill;
		for(int i=0;i<21;i++){
			
			if(ch.getSkillbar().getSkillBar().containsKey(i)){
				barIdInt=ch.getSkillbar().getSkillBar().get(i);
				barId=BitTools.intToByteArray(barIdInt);
				
				if(barIdInt>200000000){
					cdata[5140+i*8]=(byte)0x01;
				}else if(barIdInt>255){
					cdata[5140+i*8]=(byte)0x06;
					//hash
					barId=BitTools.intToByteArray(barIdInt-256);
				}else if(ch.getSkills().getLoadedSkills().containsKey(barIdInt)){
					skill=SkillMaster.getSkill(ch.getSkills().getLoadedSkills().get(barIdInt));
					if(skill.getTypeSpecific()==6){
						cdata[5140+i*8]=(byte)0x03;
					}else if(skill.getTypeSpecific()==7){
						cdata[5140+i*8]=(byte)0x04;
					}else{
						cdata[5140+i*8]=(byte)0x02;
					}
				}
				
				for(int j=0;j<4;j++){
					cdata[5144+i*8+j]=barId[j];
				}
				
			}
			
		}
		
		//skills
		byte[] skillID;
		int skillIDint;
		byte[] skillPoints;
		for(int i=0;i<ch.getSkills().getLoadedSkills().size();i++){
			
			skillIDint=ch.getSkills().getLoadedSkills().get(i);
			skillID=BitTools.intToByteArray(skillIDint);
			skillPoints=BitTools.intToByteArray((SkillMaster.getSkill(skillIDint)).getSkillpoints());
			for(int j=0;j<4;j++){
				cdata[5320+i*8+j]=skillID[j];
				cdata[5324+i*8+j]=skillPoints[j];
			}
			
		}
		
		for(int i=0;i<4;i++){
			cdata[5128+i]=coins[i];
			cdata[5800+i]=chX[i];
			cdata[5804+i]=chY[i];
		}
				
		//0c is safe zone
		//08 is nonsafe zone
		cdata[5808]=(byte)0x08;
		
		cdata[5822]=(byte)0x50;
		cdata[5823]=(byte)0x2e;
		
		return cdata;
		
	}
	
	public static byte[] getExtEquipPacket(Character ch, byte slot, int id){
		
		byte[] chID = BitTools.intToByteArray(ch.getCharID());
		byte[] itID = BitTools.intToByteArray(id);
		byte[] cedata = new byte[24];
		
		cedata[0] = (byte)cedata.length;
		cedata[4] = (byte)0x05;
		cedata[6] = (byte)0x0C;
		cedata[8] = (byte)0x01;
		
		for(int i=0;i<4;i++){
			cedata[12+i]=chID[i];
			cedata[16+i]=itID[i];
		}
		
		cedata[20]=slot;
		cedata[21]=(byte)0x9E;
		cedata[22]=slot;
		cedata[23]=(byte)0xBF;
		
		return cedata;
		
	}
	
	public static byte[] getExpPacket(Character ch, long expL){
		
		byte[] cid=BitTools.intToByteArray(ch.getCharID());
		byte[] exp=BitTools.longToByteArray(expL);
		byte[] exppckt=new byte[24];
		
		exppckt[0]=(byte)exppckt.length;
		exppckt[4]=(byte)0x05;
		exppckt[6]=(byte)0x0b;
		exppckt[8]=(byte)0x01;
		
		for(int i=0;i<4;i++){
			exppckt[12+i]=cid[i];
		}
		
		for(int i=0;i<8;i++){
			exppckt[16+i]=exp[i];
		}
		
		return exppckt;
		
	}
	
	public static byte[] getLvlUpPacket(Character ch){
		
		byte[] cid = BitTools.intToByteArray(ch.getCharID());
		byte[] hp = BitTools.intToByteArray(ch.getMaxhp());
	    byte[] mana = BitTools.intToByteArray(ch.getMaxmana());
	    byte[] lvl = BitTools.intToByteArray(ch.getLevel());
	    //byte[] expFirst = BitTools.intToByteArray(expFirstInt);
	    //byte[] expLast = BitTools.intToByteArray(expLastInt);
	    byte[] cpleft = BitTools.intToByteArray(ch.getStatPoints());
	    byte[] sp = BitTools.intToByteArray(ch.getSkillPoints());
	
		byte[] lvlpckt = new byte[44];
		
		lvlpckt[0] = (byte)lvlpckt.length;
		lvlpckt[4] = (byte)0x05;
		lvlpckt[6] = (byte)0x20;
		lvlpckt[8] = (byte)0x01;
		lvlpckt[9] = (byte)0x39;
		lvlpckt[10] = (byte)0x07;
		lvlpckt[11] = (byte)0x08;
		
		for(int i=0;i<2;i++){
			lvlpckt[16+i] = lvl[i];
			lvlpckt[18+i] = cpleft[i];
			lvlpckt[20+i] = sp[i];
		}
		
		//exp in lvlup packet is pretty fcked up lol, needs different base values
		for(int i=0;i<4;i++){
			lvlpckt[12+i] = cid[i];
			lvlpckt[24+i] = hp[i];
			lvlpckt[28+i] = mana[i];
			//lvlpckt[32+i] = expLast[i];
			//lvlpckt[36+i] = expFirst[i];
		}
		
		return lvlpckt;
		
	}
	
	public static byte[] getDeathPacket(Character ch){
		
		byte[] cid=BitTools.intToByteArray(ch.getCharID());
		
		byte[] deathpckt = new byte[20];
		
		deathpckt[0] = 0x14;
		deathpckt[4] = 0x05;
		deathpckt[6] = 0x0a;
		
		byte[] bid = new byte[] {0x01, 0x45, 0x65, 0x28}; //This might be some kind of death animation ID. Fixed value seems to work(must not be null)
		
		for(int i=0;i<4;i++) {
			deathpckt[i+12] = cid[i];
			deathpckt[i+8] = bid[i];
		}
		
		return deathpckt;
		
	}
	
	public static byte[] getVanishByID(int uid) {
		
		System.out.println("VANISH CHAR PACKET");
		byte[] vanish = new byte[20];
		byte[] bUid = BitTools.intToByteArray(uid);
		byte[] stuffz = new byte[] {(byte)0x01, (byte)0x10, (byte)0xa0, (byte)0x36, (byte)0x00, (byte)0xee, (byte)0x5f, (byte)0xbf};
		//8 -> (byte)0x01, (byte)0x10, (byte)0xa0, (byte)0x36, 	 16->	(byte)0x00, (byte)0xee, (byte)0x5f, (byte)0xbf
		
		
		vanish[0] = (byte)vanish.length;
		vanish[4] = (byte)0x05;
		
		
		for(int i=0;i<4;i++) { 
			vanish[12+i] = bUid[i];
			vanish[i+8] = stuffz[i];
			vanish[i+16] = stuffz[i+4];
		}
		
		return vanish;
		
	}
	
	public static byte[] getPickItemPacket(Character ch, ItemFrame item, int amount, int uidInt, byte line, byte row, byte stuff){
		
		byte[] chid=BitTools.intToByteArray(ch.getCharID());
		byte[] itid=BitTools.intToByteArray(item.getId());
		byte[] uid=BitTools.intToByteArray(uidInt);
		byte[] am=BitTools.intToByteArray(amount);
		
		byte[] pick = new byte[40];
		pick[0] = (byte)pick.length;
		pick[4] = (byte)0x04;
		pick[6] = (byte)0x0F;
			
		for(int i=0;i<4;i++) {
			pick[12+i] = chid[i];
			pick[28+i] = uid[i];
			pick[32+i] = itid[i];
		}
			
		pick[24] = line;
		pick[25] = row; 
		pick[26] = stuff;
		pick[30] = row;
		pick[31] = stuff;
		//amount
		pick[36] = am[0];
		pick[37] = am[1];
		pick[38] = am[2];
		pick[39] = am[3];
		//do not allow amount 0
		if(pick[36]+pick[37]+pick[38]+pick[39]==0)
			pick[36]=1;
		pick[8]  = (byte)0x01;
		pick[9]  = (byte)0x9C;
		pick[10] = (byte)0x04;
		pick[11] = (byte)0x08;
		pick[16] = (byte)0x01;
		pick[18] = (byte)0xCF;
		pick[19] = (byte)0x2D;
		pick[20] = (byte)0x03;
		return pick;
		
	}
	
	public static byte[] getJoinGameWorldStuffPacket1(Character ch){
		
		byte[] cid=BitTools.intToByteArray(ch.getCharID());
		byte[] stuff=new byte[16];
		stuff[0]=(byte)stuff.length;
		stuff[4]=(byte)0x05;
		stuff[6]=(byte)0x79;
		for(int i=0;i<4;i++){
			stuff[8+i]=cid[i];
		}
		return stuff;
		
	}
	
	public static byte[] getBuffPacket(Character ch, short buffid, short slot, Buff buff) {
    	byte[] chid = BitTools.intToByteArray(ch.getCharID());
		byte[] buffIcon = new byte[44];
		buffIcon[0] = (byte)0x2c; 
		buffIcon[4] = (byte)0x05;
		buffIcon[6] = (byte)0x1f;
		buffIcon[8] = (byte)0x01;// 1 = player | 2 = mob
		
		for(int i=0;i<4;i++){
			buffIcon[12+i] = chid[i]; 				
		}	
		
		
		byte[] buffId = BitTools.shortToByteArray(buffid);
		byte[] buffSlot = BitTools.shortToByteArray(slot);
		byte[] buffTime = BitTools.shortToByteArray(buff.getBuffTime());
		byte[] buffValue = BitTools.shortToByteArray(buff.getBuffValue());
		for(int i=0;i<2;i++) {
			buffIcon[i+16] = buffSlot[i];
			buffIcon[i+20] = buffId[i];
			buffIcon[i+22] = buffTime[i]; // time (Time in mh = EXAMPLE: 192 / 4 = 48 -> 48 is deci  = 30 Hex)
			buffIcon[i+24] = buffValue[i]; // value
		}
		
		buffIcon[26] = (byte)0x01; 
		buffIcon[28] = (byte)0x89; 
		buffIcon[32] = (byte)0x89; 
		buffIcon[36] = (byte)0x7e; 
		buffIcon[38] = (byte)0x7e; 
		buffIcon[40] = (byte)0x60; 
		buffIcon[42] = (byte)0x60;	 

		 return buffIcon;
	}
	
	public static byte[] getJoinGameWorldStuffPacket2(Character ch){
		
		byte[] cid=BitTools.intToByteArray(ch.getCharID());
		byte[] stuff=new byte[24];
		stuff[0]=(byte)stuff.length;
		stuff[4]=(byte)0x04;
		stuff[6]=(byte)0x2f;
		stuff[8]=(byte)0x01;
		for(int i=0;i<4;i++){
			stuff[12+i]=cid[i];
		}
		stuff[16]=(byte)0x01;
		return stuff;
		
	}
	
	public static byte[] getJoinGameWorldStuffPacket3(Character ch){
		
		byte[] cid=BitTools.intToByteArray(ch.getCharID());
		byte[] stuff=new byte[20];
		stuff[0]=(byte)stuff.length;
		stuff[4]=(byte)0x04;
		stuff[6]=(byte)0x4e;
		stuff[8]=(byte)0x01;
		stuff[9]=(byte)0x2e;
		stuff[10]=(byte)0xd3;
		stuff[11]=(byte)0x2a;
		for(int i=0;i<4;i++){
			stuff[12+i]=cid[i];
		}
		stuff[16]=(byte)0x22;
		stuff[17]=(byte)0x4e;
		stuff[18]=(byte)0x17;
		stuff[19]=(byte)0x54;
		return stuff;
		
	}
	
	public static byte[] getJoinGameWorldStuffPacket4(Character ch){
		
		byte[] cid=BitTools.intToByteArray(ch.getCharID());
		byte[] stuff=new byte[460];
		byte[] length=BitTools.intToByteArray(stuff.length);
		stuff[4]=(byte)0x05;
		stuff[6]=(byte)0x44;
		stuff[8]=(byte)0x01;
		for(int i=0;i<4;i++){
			stuff[12+i]=cid[i];
			stuff[i]=length[i];
		}
		stuff[16]=(byte)0x02;
		
		stuff[19]=(byte)0x01;
		stuff[20]=(byte)0x01;
		stuff[21]=(byte)0x01;
		stuff[22]=(byte)0x01;
		stuff[23]=(byte)0x01;
		stuff[24]=(byte)0x01;
		stuff[25]=(byte)0x01;
		stuff[26]=(byte)0x01;
		stuff[27]=(byte)0x01;
		stuff[28]=(byte)0x01;
		
		stuff[32]=(byte)0x01;
		stuff[40]=(byte)0x0e;
		stuff[44]=(byte)0x38;
		stuff[48]=(byte)0x25;
		stuff[52]=(byte)0x0e;
		stuff[56]=(byte)0x25;
		stuff[60]=(byte)0xd5;
		
		stuff[120]=(byte)0xc7;
		stuff[121]=(byte)0xaa;
		stuff[122]=(byte)0xb8;
		stuff[123]=(byte)0xa5;
		stuff[124]=(byte)0xbc;
		stuff[125]=(byte)0xd2;
		stuff[126]=(byte)0xb3;
		stuff[127]=(byte)0xaa;
		stuff[128]=(byte)0xb9;
		stuff[129]=(byte)0xab;
		
		stuff[137]=(byte)0x6c;
		stuff[138]=(byte)0x6c;
		stuff[139]=(byte)0xbf;
		stuff[140]=(byte)0xf9;
		stuff[141]=(byte)0xb1;
		stuff[142]=(byte)0xa4;
		stuff[143]=(byte)0x6c;
		stuff[144]=(byte)0x6c;
		
		stuff[154]=(byte)0xc3;
		stuff[155]=(byte)0xb5;
		stuff[156]=(byte)0xb7;
		stuff[157]=(byte)0xe6;
		stuff[158]=(byte)0xbd;
		stuff[159]=(byte)0xc2;
		stuff[160]=(byte)0xc3;
		stuff[161]=(byte)0xb5;
		
		stuff[171]=(byte)0xc7;
		stuff[172]=(byte)0xaa;
		stuff[173]=(byte)0xb8;
		stuff[174]=(byte)0xa5;
		stuff[175]=(byte)0xbc;
		stuff[176]=(byte)0xd2;
		stuff[177]=(byte)0xb3;
		stuff[178]=(byte)0xaa;
		stuff[179]=(byte)0xb9;
		stuff[180]=(byte)0xab;
		
		stuff[188]=(byte)0xc3;
		stuff[189]=(byte)0xb5;
		stuff[190]=(byte)0xb7;
		stuff[191]=(byte)0xe6;
		stuff[192]=(byte)0xbd;
		stuff[193]=(byte)0xc2;
		stuff[194]=(byte)0xc3;
		stuff[195]=(byte)0xb5;
		
		stuff[205]=(byte)0xb3;
		stuff[206]=(byte)0xaa;
		stuff[207]=(byte)0xbb;
		stuff[208]=(byte)0xdb;
		stuff[209]=(byte)0xb3;
		stuff[210]=(byte)0xe0;
		stuff[211]=(byte)0xbc;
		stuff[212]=(byte)0xae;
		stuff[213]=(byte)0xb5;
		stuff[214]=(byte)0xe9;
		
		return stuff;
		
	}
	
	public static byte[] getJoinGameWorldStuffPacket5(Character ch){
		
		byte[] cid=BitTools.intToByteArray(ch.getCharID());
		byte[] stuff=new byte[32];
		stuff[0]=(byte)stuff.length;
		stuff[4]=(byte)0x04;
		stuff[6]=(byte)0x61;
		stuff[8]=(byte)0x01;
		for(int i=0;i<4;i++){
			stuff[12+i]=cid[i];
		}
		return stuff;
		
	}

	public static byte[] getJoinGameWorldStuffPacket6(Character ch){
	
		byte[] cid=BitTools.intToByteArray(ch.getCharID());
		byte[] stuff=new byte[1612];
		byte[] length=BitTools.intToByteArray(stuff.length);
		stuff[4]=(byte)0x04;
		stuff[6]=(byte)0x41;
		stuff[8]=(byte)0x01;
		for(int i=0;i<4;i++){
			stuff[12+i]=cid[i];
			stuff[i]=length[i];
		}
		return stuff;
	
	}
	
	public static byte[] getJoinGameWorldStuffPacket7(Character ch){
		
		byte[] cid=BitTools.intToByteArray(ch.getCharID());
		byte[] stuff=new byte[52];
		stuff[0]=(byte)stuff.length;
		stuff[4]=(byte)0x04;
		stuff[6]=(byte)0x36;
		stuff[8]=(byte)0x01;
		stuff[9]=(byte)0xf5;
		stuff[10]=(byte)0x10;
		stuff[11]=(byte)0x29;
		for(int i=0;i<4;i++){
			stuff[12+i]=cid[i];
		}
		stuff[16]=(byte)0x01;
		return stuff;
		
	}
	
	public static byte[] getJoinGameWorldStuffPacket8(Character ch){
		
		byte[] cid=BitTools.intToByteArray(ch.getCharID());
		byte[] stuff=new byte[32];
		stuff[0]=(byte)stuff.length;
		stuff[4]=(byte)0x05;
		stuff[6]=(byte)0x35;
		stuff[8]=(byte)0x08;
		stuff[9]=(byte)0x80;
		stuff[10]=(byte)0x99;
		stuff[11]=(byte)0x43;
		for(int i=0;i<4;i++){
			stuff[12+i]=cid[i];
		}
		stuff[16]=(byte)0x01;
		
		stuff[24]=(byte)0x33;
		stuff[25]=(byte)0x01;
		
		stuff[28]=(byte)0x08;
		stuff[29]=(byte)0x01;
		stuff[30]=(byte)0xc4;
		
		return stuff;
		
	}
	
	public static byte[] getExtUseItemPacket(Character ch, int item){
		
		byte[] cid=BitTools.intToByteArray(ch.getCharID());
		byte[] itemid = BitTools.intToByteArray(item);
		
		byte[] extuseitem=new byte[40];
		extuseitem[0] = (byte)extuseitem.length;
    	extuseitem[4] = (byte)0x05;
    	extuseitem[6] = (byte)0x05;
    	
    	extuseitem[8] = (byte)0x01;
    	extuseitem[9] = (byte)0x20;
    	extuseitem[10] = (byte)0xcd;
    	extuseitem[11] = (byte)0x2a;
    	
    	for(int i=0;i<4;i++){
    		extuseitem[12+i] = cid[i];
    		extuseitem[16+i] = itemid[i];
    	}
    	
    	extuseitem[37] = (byte)0x9e;
    	extuseitem[38] = (byte)0x0f;
    	extuseitem[39] = (byte)0xbf;
    	
    	return extuseitem;
    	
	}
	
	public static byte[] getExtVending(Character cur) {
		byte[] chid = BitTools.intToByteArray(cur.getCharID());
		
		byte[] extvendor = new byte[48];
		extvendor[0] = (byte)extvendor.length;
		extvendor[4] = (byte)0x05; // 0x05 = to show other players
		extvendor[6] = (byte)0x37;
		extvendor[8] = (byte)0x01;
		
		for(int i=0;i<4;i++) {
			extvendor[12+i] = chid[i];
		}
		
		extvendor[16] = cur.getVendor() == null ? (byte)0x00 : (byte)0x01;
		
		if(cur.getVendor() != null) {
			byte[] shopname = BitTools.stringToByteArray(cur.getVendor().getShopname());
			for(int i=0;i<30;i++) {
				extvendor[17+i] = shopname[i];
			}
		}
		return extvendor;
	}
	
	public static byte[] getMovementPacket(Character ch, float targetX, float targetY, byte run){
		
		byte[] chid = BitTools.intToByteArray(ch.getCharID());
		byte[] tx = BitTools.floatToByteArray(targetX);
		byte[] ty = BitTools.floatToByteArray(targetY);
		byte[] chx = BitTools.floatToByteArray(ch.getlastknownX());
		byte[] chy = BitTools.floatToByteArray(ch.getlastknownY());
		
		byte[] locSync = new byte[56]; 
		
		locSync[0] = (byte)locSync.length;
		locSync[4] = (byte)0x04;
		locSync[6] = (byte)0x0D;
		
		for(int i=0;i<4;i++) {
			//1st set
			locSync[16+i] = chx[i];   
			locSync[20+i] = chy[i]; 
			//2nd set 
			locSync[24+i] = tx[i];
			locSync[28+i] = ty[i];
			//character id
			locSync[i+12] = chid[i];
		}
		
		//run/walk
		locSync[40]=run;
		
		return locSync;
		
	}
	
	public static byte[] getExtMovementPacket(Character ch, float targetX, float targetY, byte run){
		
		byte[] chid = BitTools.intToByteArray(ch.getCharID());
		byte[] tx = BitTools.floatToByteArray(targetX);
		byte[] ty = BitTools.floatToByteArray(targetY);
		byte[] chx = BitTools.floatToByteArray(ch.getlastknownX());
		byte[] chy = BitTools.floatToByteArray(ch.getlastknownY());
		
		byte externmove[] = new byte[48]; 
		
		externmove[0] = (byte)externmove.length;
		externmove[4] = (byte)0x05;
		externmove[6] = (byte)0x0D;
		
		externmove[8]  = (byte)0x01;
		
		for(int i=0;i<4;i++) {
			externmove[i+12] = chid[i];
			//character actual coords
			externmove[i+20] = chx[i];
			externmove[i+24] = chy[i];
			//character target coords
			externmove[i+28] = tx[i];
			externmove[i+32] = ty[i];	
		}
		
		//run/walk
		externmove[36]=run;
		
		return externmove;
		
	}
	
}