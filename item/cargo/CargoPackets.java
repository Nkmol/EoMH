package item.cargo;

import Player.Character;
import Tools.BitTools;

public class CargoPackets {
	public static byte[] CargoWithdrawDepost(Character cur, int invIndex, int x, int y, int state) {
		
		byte[] chid = BitTools.intToByteArray(cur.getCharID());
		
		byte[] �argo = new byte[24];
		�argo[0] = (byte)0x18;
		�argo[4] = (byte)0x04;
		�argo[6] = (byte)0x2c;
		�argo[8] = (byte)0x01;
		for(int i=0;i<4;i++) {
			�argo[12+i] = chid[i]; 
		}
		�argo[16] = (byte)0x01;
		�argo[18] = (byte)state;
		�argo[19] = (byte)invIndex; 
		//�argo[20] = decrypted[2]; 
		�argo[21] = (byte)y;
		�argo[22] = (byte)x;
		�argo[23] = (byte)0x2e;
		
		return �argo;
	}
}
