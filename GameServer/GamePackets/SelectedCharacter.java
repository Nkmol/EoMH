package GameServer.GamePackets;

import java.nio.ByteBuffer;
//import java.nio.channels.SelectionKey;

import Connections.Connection;
import Database.CharacterDAO;
import Encryption.Decryptor;
import Player.Player;
import Player.PlayerConnection;
import Player.Character;
//import Tools.BitTools;

public class SelectedCharacter implements Packet {

	boolean spawnInVV;
	
	public SelectedCharacter(boolean spawnInVV){
		this.spawnInVV=spawnInVV;
	}
	
	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) {
		Character ch = null;
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];

		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		decrypted = Decryptor.Decrypt(decrypted);
		
		Player polishPlayer = ((PlayerConnection)con).getPlayer();
		if(polishPlayer != null) {
			ch = polishPlayer.getCharacterByIndex(decrypted[0]);
			polishPlayer.setActiveCharacter(ch);
			if(spawnInVV){
				ch.setCurrentMap(1);
				ch.setX(-1660);
				ch.setY(2344);
				CharacterDAO.saveCharacterLocation(ch);
				byte[] stuff= new byte[24];
				stuff[0]=(byte)stuff.length;
				stuff[4]=(byte)0x03;
				stuff[6]=(byte)0x0e;
				
				stuff[8]=(byte)0x01;
				
				//char index
				stuff[9]=decrypted[0];
				
				stuff[10]=(byte)0x06;
				stuff[11]=(byte)0x08;
				stuff[12]=(byte)0x01;
				
				stuff[17]=(byte)0x80;
				stuff[18]=(byte)0xcf;
				stuff[19]=(byte)0xc4;
				
				stuff[21]=(byte)0x80;
				stuff[22]=(byte)0x12;
				stuff[23]=(byte)0x45;
				
				con.addWrite(stuff);
			}

		polishPlayer.getActiveCharacter().getEquips().calculateEquipStats();
		polishPlayer.getActiveCharacter().joinGameWorld(true,true);
	}
		return null;
	}
	
	
}
