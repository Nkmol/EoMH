package Skills;

import Player.Character;
import Tools.BitTools;

public class SkillPackets {

	public static byte[] getLearnSkillPacket(Character ch, int skillIdInt, int skillNumberInt){
		
		byte[] cid = BitTools.intToByteArray(ch.getCharID());
		byte[] skillId=BitTools.intToByteArray(skillIdInt);
		byte[] skillNumber=BitTools.intToByteArray(skillNumberInt);
		byte[] skillpoints = BitTools.intToByteArray(ch.getSkillPoints());
		
		byte[] learnskill = new byte[32];
		
		learnskill[0] = (byte)learnskill.length;
		learnskill[4] = (byte)0x04;
		learnskill[6] = (byte)0x29;
		learnskill[8] = (byte)0x01;
		
		for(int i=0;i<4;i++) {
			learnskill[12+i] = cid[i];
			//skill number
			learnskill[20+i] = skillNumber[i];
			//skill id
			learnskill[24+i] = skillId[i];
		}
		
		learnskill[16]=(byte)0x01;
		learnskill[18]=(byte)0x06;
		learnskill[19]=(byte)0x08;
		
		for(int i=0;i<2;i++){
			learnskill[28+i]=skillpoints[i];
		}
		
		learnskill[30]=(byte)0x39;
		learnskill[31]=(byte)0x08;
		
		return learnskill;
		
	}
	
	public static byte[] getSkillEffectOnCharPacket(Character ch){
		
		byte[] cid = BitTools.intToByteArray(ch.getCharID());
		
		byte[] skillpckt = new byte[44];
		
		skillpckt[0] = (byte)skillpckt.length;
    	skillpckt[4] = (byte)0x05;
    	skillpckt[6] = (byte)0x1F;
    	skillpckt[8] = (byte)0x01;
    	skillpckt[9] = (byte)0x99;
    	skillpckt[10] = (byte)0x0F;
    	skillpckt[11] = (byte)0xBF;
    	
    	//CharID
    	for(int i=0;i<4;i++){
    		skillpckt[12+i] = cid[i];
    	}
    	
    	skillpckt[16] = (byte)0x0E;
    	
    	//Effid
    	skillpckt[20] = (byte)0x00;  //e.g.32
    	skillpckt[21] = (byte)0x00;
    	
    	//Effduration
    	skillpckt[22] = (byte)0x00; //e.g.6e
    	skillpckt[23] = (byte)0x00;
    	
    	//Effvalue
    	skillpckt[24] = (byte)0x00; //e.g.21
    	skillpckt[25] = (byte)0x00;
    	
    	skillpckt[26] = (byte)0x01;
    	skillpckt[28] = (byte)0x87;
    	skillpckt[29] = (byte)0x01;
    	skillpckt[32] = (byte)0x87;
    	skillpckt[33] = (byte)0x01;
    	skillpckt[36] = (byte)0x45;
    	skillpckt[37] = (byte)0x01;
    	skillpckt[38] = (byte)0x45;
    	skillpckt[39] = (byte)0x01;
    	skillpckt[40] = (byte)0xF2;
    	skillpckt[42] = (byte)0xF2;  
    	
    	return skillpckt;
		
	}
	
	public static byte[] getSummPacket(Character ch, int skillIdInt, byte activationId) {
		byte[] cid = BitTools.intToByteArray(ch.getCharID());
		byte[] skillid = BitTools.intToByteArray(skillIdInt);
		
		byte[] skillpckt = new byte[52];
    	
    	skillpckt[0] = (byte)skillpckt.length;
    	skillpckt[4] = (byte)0x05;
    	skillpckt[6] = (byte)0x34;
    	skillpckt[8] = (byte)0x01;
    	
    	//CharID
    	for(int i=0;i<4;i++){
    		skillpckt[12+i] = cid[i];
    		skillpckt[20+i] = skillid[i];
    	} 
    	
    	skillpckt[16] = (byte)0x01;
    	
    	//skilltype
    	skillpckt[24]= activationId;
    	skillpckt[25] = (byte)0x07;
    	skillpckt[27] = (byte)0x01;
    	
    	return skillpckt;
	}
	
	public static byte[] getMediPacket(Character ch, int skillIdInt, byte activationId){
		
		byte[] cid = BitTools.intToByteArray(ch.getCharID());
		byte[] skillid = BitTools.intToByteArray(skillIdInt);
		
		byte[] skillpckt = new byte[28];
		
		skillpckt[0] = (byte)skillpckt.length;
    	skillpckt[4] = (byte)0x05;
    	skillpckt[6] = (byte)0x34;
    	
    	skillpckt[8] = (byte)0x01;
    	skillpckt[9] = (byte)0xa2;
    	skillpckt[10] = (byte)0x04;
    	skillpckt[11]= (byte)0x08;
    	
    	for(int i=0;i<4;i++){
    		skillpckt[12+i] = cid[i];
    		skillpckt[20+i] = skillid[i];
    	}
    	
    	skillpckt[16]=(byte)0x01;
    		
    	skillpckt[18] = (byte)0x14;
    	skillpckt[19] = (byte)0x08;
    		
    	skillpckt[24] = activationId;
    	skillpckt[25] = (byte)0xee;
    	skillpckt[26] = (byte)0x0d;
    	
    	return skillpckt;
		
	}
	
	public static byte[] getTurboPacket(Character ch, int skillIdInt, boolean activate){
		
		byte[] cid = BitTools.intToByteArray(ch.getCharID());
		byte[] skillid = BitTools.intToByteArray(skillIdInt);
		
		byte[] skillpckt = new byte[28];
		
		skillpckt[0] = (byte)skillpckt.length;
    	skillpckt[4] = (byte)0x05;
    	skillpckt[6] = (byte)0x34;
    	
    	skillpckt[8] = (byte)0x01;
    	
		if(activate==true){
			skillpckt[9] = (byte)0x96;
			skillpckt[10] = (byte)0x15;
			
			skillpckt[18] = (byte)0x0f;
    		skillpckt[19] = (byte)0xbf;
    		
    		skillpckt[24] = (byte)0xca;
    		skillpckt[25] = (byte)0xa2;
    		skillpckt[26] = (byte)0x04;
		}else{
			skillpckt[9] = (byte)0xa2;
			skillpckt[10] = (byte)0x04;
			
			skillpckt[18] = (byte)0x14;
    		skillpckt[19] = (byte)0x08;
    		
    		skillpckt[24] = (byte)0xcb;
    		skillpckt[25] = (byte)0xee;
    		skillpckt[26] = (byte)0x0d;
		}
	
		skillpckt[11]= (byte)0x08;
	
		for(int i=0;i<4;i++){
			skillpckt[12+i] = cid[i];
			skillpckt[20+i] = skillid[i];
		}
	
		skillpckt[16]=(byte)0x01;
    	
    	return skillpckt;
		
	}
	
	public static byte[] getCastSkillPacket(Character ch, int targets, int skillIdInt, byte activationId, boolean isSpecial){
		
		byte[] cid = BitTools.intToByteArray(ch.getCharID());
		byte[] skillid = BitTools.intToByteArray(skillIdInt);
		
		byte[] skillpckt = new byte[28+targets*24];
    	
    	skillpckt[0] = (byte)skillpckt.length;
    	skillpckt[4] = (byte)0x05;
    	skillpckt[6] = (byte)0x34;
    	skillpckt[8] = (byte)0x01;
    	
    	//CharID
    	for(int i=0;i<4;i++){
    		skillpckt[12+i] = cid[i];
    		skillpckt[20+i] = skillid[i];
    	} 
    	
    	skillpckt[16] = (byte)0x01;
    	
    	//skilltype
    	skillpckt[24]= activationId;
    	
    	if(isSpecial)
    		skillpckt[25] = (byte)0x02;
    	else
    		skillpckt[25] = (byte)0x07;
    	
    	skillpckt[27] = (byte)(targets);
    	
    	return skillpckt;
		
	}
	
	public static byte[] completeCastSkillPacket(byte[] skillpckt, int aoe, int targetId, int hpInt, int manaInt, int dmgInt, int chartargets, int dmgType){
		
		byte[] hp=BitTools.intToByteArray(hpInt);
		byte[] mana=BitTools.intToByteArray(manaInt);
		byte[] dmg=BitTools.intToByteArray(dmgInt);
		byte[] targetByte=BitTools.intToByteArray(targetId);
		
		if(chartargets>0)
			skillpckt[28+aoe*24] = (byte)0x01;
		else
			skillpckt[28+aoe*24] = (byte)0x02;
		
		//CharID
		for(int i=0;i<4;i++){
			//target
			skillpckt[32+i+aoe*24] = targetByte[i];
			skillpckt[40+i+aoe*24] = hp[i];
			skillpckt[44+i+aoe*24] = dmg[i];
			skillpckt[48+i+aoe*24] = mana[i];
		}
		
		//0=miss,1=normal,2=whitecrit,3/4=buff/nothing,5=greencrit
		skillpckt[36+aoe*24] = (byte)dmgType;
		skillpckt[37+aoe*24] = (byte)0x00;
		skillpckt[38+aoe*24] = (byte)0x00;
		skillpckt[39+aoe*24] = (byte)0x00;
		
		return skillpckt;
		
	}
	
}
