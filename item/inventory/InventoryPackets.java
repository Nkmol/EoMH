package item.inventory;

import Player.Character;
import Tools.BitTools;

public class InventoryPackets {

	public static byte[] getInventoryDeletePacket(Character ch, int invIndex, int amount){
		byte[] chid = BitTools.intToByteArray(ch.getCharID());
		
		byte[] delete=new byte[20];
		
		delete[0] = (byte)delete.length;
		delete[4] = (byte)0x04;
		delete[6] = (byte)0x15;
		
		for(int i=0;i<4;i++) {
			delete[12+i] = chid[i];
		}
		
		delete[16] = (byte)0x01;
		delete[18] = (byte)amount;
		delete[19] = (byte)invIndex;
		
		return delete;
	}
	
}
