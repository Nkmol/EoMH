package item.cargo;

import Player.Character;
import Tools.BitTools;

public class CargoPackets {
	public static byte[] CargoWithdrawDepost(Character cur, int invIndex, int x, int y, int state) {
		
		byte[] chid = BitTools.intToByteArray(cur.getCharID());
		
		byte[] çargo = new byte[24];
		çargo[0] = (byte)0x18;
		çargo[4] = (byte)0x04;
		çargo[6] = (byte)0x2c;
		çargo[8] = (byte)0x01;
		for(int i=0;i<4;i++) {
			çargo[12+i] = chid[i]; 
		}
		çargo[16] = (byte)0x01;
		çargo[18] = (byte)state;
		çargo[19] = (byte)invIndex; 
		//çargo[20] = decrypted[2]; 
		çargo[21] = (byte)y;
		çargo[22] = (byte)x;
		çargo[23] = (byte)0x2e;
		
		return çargo;
	}
}
