package Mob;


import Tools.BitTools;
import World.Waypoint;

/*
 * MobPackets.class
 * Generates the necessary packets for the mobs
 */

public class MobPackets {
	
	public static byte[] getMovePacket(int uid, float x, float y){
		byte[] moveBucket = new byte[48];
		byte[] uniqueID = BitTools.intToByteArray(uid);
		byte[] moveX = BitTools.floatToByteArray(x);
		byte[] moveY = BitTools.floatToByteArray(y);
		
		moveBucket[0] = (byte)moveBucket.length;
		moveBucket[4] = (byte)0x05;
		moveBucket[6] = (byte)0x0D;
		moveBucket[8] =  (byte)0x02;
		moveBucket[9] =  (byte)0x10;
		moveBucket[10] = (byte)0xa0; 
		moveBucket[11] = (byte)0x36;

		
		for(int i=0;i<4;i++) {
			moveBucket[i+12] = uniqueID[i];
			moveBucket[i+20] = moveX[i];
			moveBucket[i+24] = moveY[i];
			moveBucket[i+28] = moveX[i];
			moveBucket[i+32] = moveY[i];
		}
		
		return moveBucket;
	}
	public static byte[] getDeathPacket(int uid, Mob mob, boolean star) {
		
		byte[] rval = new byte[20];
		
		rval[0] = 0x14;
		rval[4] = 0x05;
		rval[6] = 0x0a;
		
				
		byte[] bytes = BitTools.intToByteArray(uid);
		byte[] bid = new byte[] {0x02, 0x61, 0x21, 0x35}; //This might be some kind of death animation ID. Fixed value seems to work(must not be null)
		
		for(int i=0;i<4;i++) {
			rval[i+12] = bytes[i];
			rval[i+8] = bid[i];
		}
		
		if(star)
			rval[16]=(byte)0x01;
		
		return rval;
		
	}
	//public static ByteBuffer getInitialPacket(int mobID, int uid, float x, float y) {
	public static byte[] getInitialPacket(int mobID, int uid, Waypoint wp, int curhp) {
        byte[] mobBucket = new byte[608];
        byte[] size = BitTools.shortToByteArray((short)mobBucket.length);
       
        byte[] mobid = BitTools.shortToByteArray((short)mobID);
        byte[] mobUid = BitTools.intToByteArray(uid);
        byte[] xCoords = BitTools.floatToByteArray(wp.getX());
        byte[] yCoords = BitTools.floatToByteArray(wp.getY());
        byte[] hp = BitTools.intToByteArray(curhp);
        
        for(int i=0;i<2;i++) {
                mobBucket[i] = size[i];
                mobBucket[i+64] = mobid[i];
        }
        for(int i=0;i<4;i++) {
                mobBucket[i+12] = mobUid[i];
                mobBucket[i+84] = xCoords[i];
                mobBucket[i+88] = yCoords[i];
                mobBucket[i+72] = hp[i];
        }
       
        mobBucket[4] = (byte)0x05;
        mobBucket[6] = (byte)0x03;
        mobBucket[8] = (byte)0x02;
        
        if(MobMaster.start > 0) {
        	for(int i=0;i<MobMaster.length;i++) {
        		mobBucket[MobMaster.start+i] = (byte)MobMaster.value;
        	}
        }

      return mobBucket;
	}
	
	public static byte[] getSkillPacket(int mobID, int skillID, byte dmgType, int totalDmg, int[] targets, int targethp, int targetmana){
		
		byte[] mid=BitTools.intToByteArray(mobID);
		byte[] skillid=BitTools.intToByteArray(skillID);
		byte[] dmg;
		byte[] targetid;
		byte[] targetNewHp=BitTools.intToByteArray(targethp);
		byte[] targetNewMana=BitTools.intToByteArray(targetmana);
		
		byte[] skillpckt = new byte[28+targets.length*24];
		
		skillpckt[0] = (byte)skillpckt.length;
    	skillpckt[4] = (byte)0x05;
    	skillpckt[6] = (byte)0x34;
    	skillpckt[8] = (byte)0x02;
    	
    	//CharID
    	for(int i=0;i<4;i++){
    		skillpckt[12+i] = mid[i];
    		skillpckt[20+i] = skillid[i];
    	} 
    	
    	skillpckt[16] = (byte)0x01;
    	
    	skillpckt[27] = (byte)targets.length;
    	
    	for(int aoe=0;aoe<targets.length;aoe++){
    		
    		targetid=BitTools.intToByteArray(targets[aoe]);
        	dmg=BitTools.intToByteArray(-totalDmg);
    		
        	skillpckt[28+aoe*24] = (byte)0x01;
    		
    		//CharID
    		for(int i=0;i<4;i++){
    			//target
    			skillpckt[32+i+aoe*24] = targetid[i];
    			
    			skillpckt[40+i+aoe*24] = targetNewHp[i];
    			skillpckt[44+i+aoe*24] = dmg[i];
    			skillpckt[48+i+aoe*24] = targetNewMana[i];
    		}
    		
    		//0=miss,1=normal,2=whitecrit,3/4=buff/nothing,5=greencrit
    		skillpckt[36+aoe*24] = dmgType;
    		skillpckt[37+aoe*24] = (byte)0x00;
    		skillpckt[38+aoe*24] = (byte)0x00;
    		skillpckt[39+aoe*24] = (byte)0x00;
    	}
    	
    	return skillpckt;
    	
	}
	
	public static byte[] famepacket(int uid, int iChid, int fame) {
        byte[] famepacket = new byte[28];
    	byte[] Uid = BitTools.intToByteArray(uid);
    	byte[] chid = BitTools.intToByteArray(iChid);
		byte[] Fame = BitTools.intToByteArray(fame);
        famepacket[0] = (byte)0x1c;
        famepacket[4] = (byte)0x05;
        famepacket[6] = (byte)0x09;
        famepacket[8] = (byte)0x01;
		 for(int i=0;i<4;i++) {
			 famepacket[i+12] = chid[i];	
			 famepacket[i+20] = Uid[i]; 
			 famepacket[i+24] = Fame[i];
		 }
	       famepacket[16] = (byte)0x02;
	       famepacket[17] = (byte)0x29;
	       famepacket[18] = (byte)0x37;
	       famepacket[19] = (byte)0x08;
       
        return famepacket;
	}

}
