package GameServer.GamePackets;

import java.nio.ByteBuffer;

import Connections.Connection;
import Database.CharacterDAO;
import Encryption.Decryptor;
import Player.Character;
import Player.Player;
import Player.PlayerConnection;

public class CharacterDelete implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling Character Delete");
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		byte[] delete = new byte[11];
		
		delete[0] = (byte)delete.length;
		delete[4] = (byte)0x03;
		delete[6] = (byte)0x07;
		
		delete[8] = (byte)0x01;
		//Character index
		delete[9] = decrypted[0];
		
		Player player = ((PlayerConnection)con).getPlayer();
		Character character = player.getCharacterByIndex(decrypted[0]);
		
		byte action;
		if (decrypted[1]==(byte)0x00){
			if (character.isAbandoned()){
				action=(byte)0x02;
				//delete
				player.removeCharacter(character);
				CharacterDAO.deleteCharacter(character);
			}else{
				action=(byte)0x01;
				character.setAbandoned(true);
			}
		}else{
			if (character.isAbandoned()){
				action=(byte)0x00;
				character.setAbandoned(false);
			}else{
				//or maybe do nothing?
				action=(byte)0x00;
			}
		}
		
		delete[10] = action;
		
		return delete;
	}
	
}
