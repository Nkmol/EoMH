package GameServer.GamePackets;

import java.nio.ByteBuffer;

import Player.Character;
import Player.PlayerConnection;
import Skills.SkillException;
import Skills.SkillMaster;
import Tools.BitTools;
import World.OutOfGridException;
import Connections.Connection;
import Encryption.Decryptor;
import GameServer.ServerPackets.ServerMessage;

public class CastSkill implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling Skillcast");
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		Character cur = ((PlayerConnection)con).getActiveCharacter();

        try{
        	//give decrypted stuff other names
        	byte skillBarNumber=decrypted[0];
        	byte skillActivationType=decrypted[1];
        	byte chartargets=decrypted[16];
        	byte mobtargets=decrypted[18];
        	int[] targetIds=new int[chartargets+mobtargets];
        	byte[] targetByte=new byte[4];
        	for(int i=0;i<targetIds.length;i++){
        		for(int j=0;j<4;j++){
        			targetByte[j]=decrypted[20+j+i*4];
        		}
        		targetIds[i]=BitTools.byteArrayToInt(targetByte);
        	}
        	
        	return SkillMaster.castSkill(con, cur, skillBarNumber, skillActivationType, chartargets, mobtargets, targetIds);
        	
        }catch(SkillException e){
        	
        	new ServerMessage().execute(e.getMessage(),con);
			throw new PaketException();
        	
        }catch(OutOfGridException e){
        	e.printStackTrace();
        }
		return null;
	}
	
}
