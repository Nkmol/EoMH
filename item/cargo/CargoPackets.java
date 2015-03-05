package item.cargo;

import Player.Character;
import Tools.BitTools;

public class CargoPackets {
	public static byte[] CargoDepost(Character cur, int invIndex, int x, int y, int cargoIndex, int state) {
		
		byte[] chid = BitTools.intToByteArray(cur.getCharID());
		
		byte[] cargo = new byte[24];
		
		cargo[0] = (byte)cargo.length;
		cargo[4] = (byte)0x04;
		cargo[6] = (byte)0x2c;
		cargo[8] = (byte)0x01;
		for(int i=0;i<4;i++) {
			cargo[12+i] = chid[i]; 
		}
		cargo[16] = (byte)0x01;
		cargo[18] = (byte)state;
		cargo[19] = (byte)invIndex; 
		cargo[20] = (byte)cargoIndex;
		cargo[21] = (byte)y;
		cargo[22] = (byte)x;
		cargo[23] = (byte)0x2e;
		
		return cargo;
	}
	
	public static byte[] CargoWithdraw(Character cur, int cargoIndex, int x, int y, int invIndex) {
		
		byte[] chid = BitTools.intToByteArray(cur.getCharID());
		
		byte[] cargo = new byte[24];
		
		cargo[0] = (byte)cargo.length;
		cargo[4] = (byte)0x04;
		cargo[6] = (byte)0x2d;
		cargo[8] = (byte)0x01;
		for(int i=0;i<4;i++) {
			cargo[12+i] = chid[i]; 
		}
		cargo[16] = (byte)0x01;

		cargo[18] = (byte)cargoIndex; 
		cargo[19] = (byte)invIndex;
		cargo[20] = (byte)y;
		cargo[21] = (byte)x;
		cargo[22] = (byte)0x0F;
		cargo[23] = (byte)0xBF;
		
		return cargo;
	}
	
	public static byte[] CargoMove(Character cur, int cargoIndex, int preFromX, int y, int x) {
		
		byte[] chid = BitTools.intToByteArray(cur.getCharID());
		
		byte[] cargo = new byte[24];
		
		cargo[0] = (byte)cargo.length;
		cargo[4] = (byte)0x04;
		cargo[6] = (byte)0x2E;
		cargo[8] = (byte)0x01;
		for(int i=0;i<4;i++) {
			cargo[12+i] = chid[i]; 
		}
		cargo[16] = (byte)0x01;

		cargo[18] = (byte)cargoIndex; //?

		cargo[20] = (byte)preFromX;
		cargo[21] = (byte)cargoIndex;
		cargo[22] = (byte)y;
		cargo[23] = (byte)x;
		
		return cargo;
	}
	
public static byte[] CargoSwap(Character cur, int fromIndex, int toIndex, int y, int x) {
		
		byte[] chid = BitTools.intToByteArray(cur.getCharID());
		
		byte[] cargo = new byte[24];
		
		cargo[0] = (byte)cargo.length;
		cargo[4] = (byte)0x04;
		cargo[6] = (byte)0x2E;
		cargo[8] = (byte)0x01;
		for(int i=0;i<4;i++) {
			cargo[12+i] = chid[i]; 
		}
		cargo[16] = (byte)0x01;

		cargo[18] = (byte)fromIndex; //?
		//cargo[19] = (byte)preFromX;
		//cargo[20] = (byte)preFromX;
		cargo[21] = (byte)toIndex; //sets holding item
		cargo[22] = (byte)y;
		cargo[23] = (byte)x;
		
		return cargo;
	}
	
	
}
