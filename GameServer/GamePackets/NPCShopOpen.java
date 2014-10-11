package GameServer.GamePackets;

import java.nio.ByteBuffer;

import Connections.Connection;
import Encryption.Decryptor;
import Player.Character;
import Player.PlayerConnection;
import Tools.BitTools;

public class NPCShopOpen implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling NPC shop open");
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		
		byte[] chid=BitTools.intToByteArray(cur.getCharID());
		
		byte[] npcshop = new byte[56];
		npcshop[0] = (byte)0x38;
		npcshop[4] = (byte)0x04;
		npcshop[6] = (byte)0x13;
		npcshop[8] = (byte)0x01;
		for(int i=0;i<4;i++) {
			npcshop[12+i] = chid[i];
		}
		npcshop[16] = (byte)0x47;
		npcshop[17] = (byte)0x76;
		npcshop[18] = decrypted[0];
		
		npcshop[24] = (byte)0x01;
		
		npcshop[29] = (byte)0x47;
		
		npcshop[32] = (byte)0x4b;
		npcshop[33] = (byte)0x6b;
		
		npcshop[38] = (byte)0x80;
		npcshop[39] = (byte)0x3f;
		
		npcshop[42] = (byte)0x80;
		npcshop[43] = (byte)0x3f;
		
		return npcshop;
	}
	
}
