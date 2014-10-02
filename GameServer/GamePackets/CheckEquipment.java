package GameServer.GamePackets;

import java.nio.ByteBuffer;
import java.util.Iterator;

import Connections.Connection;
import Encryption.Decryptor;
import GameServer.ServerPackets.ServerMessage;
import Player.Character;
import Player.Dolls.Doll;
import Player.Dolls.DollMaster;
import Player.PlayerConnection;
import Tools.BitTools;

public class CheckEquipment implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling Check Equip");
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		byte[] amountByte={decrypted[4],decrypted[5],decrypted[6],decrypted[7]};
		int amount=BitTools.byteArrayToInt(amountByte);
		
		Doll doll;
		boolean found=false;
		for(Iterator<Doll> i=DollMaster.getDolls().iterator();i.hasNext();){
			doll=i.next();
			if(amount==doll.getUid()){
				Character cur = ((PlayerConnection)con).getActiveCharacter();
				doll.duplicateCharacter(cur);
				new ServerMessage().execute("Hello clone!",con);
				found=true;
			}
		}
		
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		byte[] chid = BitTools.intToByteArray(cur.getCharID());
		byte[] checkEq = new byte[28];
		
		checkEq[0] = (byte)checkEq.length;
		checkEq[4] = (byte)0x04;
		checkEq[6] = (byte)0x1e;
		
		checkEq[8] = (byte)0x01;
		checkEq[9] = (byte)0xd0;
		checkEq[10] = (byte)0xc0;
		checkEq[11] = (byte)0x2a;
		
		for(int i=0;i<4;i++) {
			checkEq[12+i] = chid[i];
		}
					
		checkEq[16] = (byte)0x01;
		checkEq[18] = (byte)0xd8;
		checkEq[19] = (byte)0x01;
			
		for(int i=0;i<4;i++) {
			checkEq[20+i] = decrypted[4+i];
		}

		if(found)
			checkEq[24] = (byte)0x01;
		checkEq[25] = (byte)0x9e;
		checkEq[26] = (byte)0x0f;
		checkEq[27] = (byte)0xbf;
			
		return checkEq;
	}
	
}
