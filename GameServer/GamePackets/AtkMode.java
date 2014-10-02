package GameServer.GamePackets;

import java.nio.ByteBuffer;

import Player.Character;
import Player.PlayerConnection;
import Tools.BitTools;
import Connections.Connection;
import Encryption.Decryptor;

public class AtkMode implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling Atkmode");
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		byte[] cid = BitTools.intToByteArray(cur.getCharID());
		byte[] atkmode = new byte[24];
		
		atkmode[0] = (byte)atkmode.length;
		atkmode[4] = (byte)0x05;
		atkmode[6] = (byte)0x06;
		
		atkmode[8] = (byte)0x01;
		atkmode[9] = (byte)0x33;
		atkmode[10] = (byte)0x15;
		atkmode[11] = (byte)0x08;
		
		for(int i=0;i<4;i++) {
			atkmode[12+i] = cid[i];
		}
		
		atkmode[16]=decrypted[0];
		
		atkmode[18] = (byte)0x10;
		atkmode[19] = (byte)0x29;
		
		cur.sendToMap(atkmode);
		
		cur.stopMovement();
		
		return null;
	}
	
}
