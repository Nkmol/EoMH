package GameServer.GamePackets;

import java.nio.ByteBuffer;

import Player.Character;
import Player.PlayerConnection;
import Tools.BitTools;
import Connections.Connection;
import Database.CharacterDAO;
import Encryption.Decryptor;

public class SkillIntoBar implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling Skillbar");
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		byte[] cid = BitTools.intToByteArray(cur.getCharID());
		byte[] skillbar = new byte[24];
		
		skillbar[0] = (byte)skillbar.length;
		skillbar[4] = (byte)0x04;
		skillbar[6] = (byte)0x11;
		
		skillbar[8] = (byte)0x01;
		skillbar[9] = (byte)0x06;
		skillbar[10] = (byte)0x15;
		skillbar[11] = (byte)0x08;
		
		for(int i=0;i<4;i++) {
			skillbar[12+i] = cid[i];
			//skill number
			skillbar[20+i] = decrypted[i+4];
		}
		
		skillbar[16]=(byte)0x01;
		//slot
		skillbar[18]=decrypted[0];
		//action id
		skillbar[19]=decrypted[1];
		
		if(decrypted[1]==2 || decrypted[1]==1 || decrypted[1]==3 || decrypted[1]==4){
			byte[] skillNumber={decrypted[4],decrypted[5],decrypted[6],decrypted[7]};
			cur.getSkillbar().getSkillBar().put((int)decrypted[0], BitTools.byteArrayToInt(skillNumber));
		}else if (decrypted[1]==6){
			byte[] skillNumber={decrypted[4],decrypted[5],decrypted[6],decrypted[7]};
			//+256 is hash
			cur.getSkillbar().getSkillBar().put((int)decrypted[0], BitTools.byteArrayToInt(skillNumber)+256);
		}else if (decrypted[1]==0){
			cur.getSkillbar().getSkillBar().remove((int)decrypted[0]);
		}
		CharacterDAO.saveCharSkillbar(cur.getuid(), cur.getSkillbar());
		
		
		return skillbar;
	}
	
}
