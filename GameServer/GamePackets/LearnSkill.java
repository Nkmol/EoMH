package GameServer.GamePackets;

import java.nio.ByteBuffer;

import Player.Character;
import Player.PlayerConnection;
import Skills.SkillException;
import Skills.SkillMaster;
import Skills.SkillPackets;
import Tools.BitTools;
import Connections.Connection;
import Database.CharacterDAO;
import Encryption.Decryptor;
import GameServer.ServerPackets.ServerMessage;

public class LearnSkill implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling Learning Skill");
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		byte[] skillNumber={decrypted[0],decrypted[1],decrypted[2],decrypted[3]};
		byte[] skillId={decrypted[4],decrypted[5],decrypted[6],decrypted[7]};
		int skillNumberInt=BitTools.byteArrayToInt(skillNumber);
		int skillIdInt=BitTools.byteArrayToInt(skillId);
		byte[] learnskill = SkillPackets.getLearnSkillPacket(cur, skillIdInt, skillNumberInt);
		
		try{
			
			SkillMaster.canLearnSkill(cur, skillIdInt);
			cur.getSkills().learnSkill(skillIdInt,true);
			CharacterDAO.saveCharSkills(cur.getuid(), cur.getSkills());
			
		}catch(SkillException e){
			
			new ServerMessage().execute(e.getMessage(),con);
			throw new PaketException();
			
		}
		
		return learnskill;
	}
	
}
