package GameServer.GamePackets;

import java.nio.ByteBuffer;

import Connections.Connection;
import Encryption.Decryptor;
import Player.Player;
import Player.PlayerConnection;
import Player.Character;
import Player.ChatMaster;


public class Chat implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}


	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) {
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		Player tmplayer = ((PlayerConnection)con).getPlayer();
		Character current = tmplayer.getActiveCharacter();
		
		ChatMaster.handleChat(current, decrypted);
		
		return null;
	}

}
