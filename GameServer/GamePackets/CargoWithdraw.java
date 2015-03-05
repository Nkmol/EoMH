package GameServer.GamePackets;

import item.cargo.CargoException;

import java.nio.ByteBuffer;

import Connections.Connection;
import Encryption.Decryptor;
import GameServer.ServerPackets.ServerMessage;
import Player.Character;
import Player.PlayerConnection;

public class CargoWithdraw implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling cargo item withdraw");
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
				
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
				
		decrypted = Decryptor.Decrypt(decrypted);
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		
		//decrypted[0] //Cargo index(item)
		//decrypted[2] //y place
		//decrypted[3] //x place
		
		try {
			return cur.getCargo().Withdraw((int)decrypted[0], (int)decrypted[3], (int)decrypted[2]);
		} catch (CargoException e) {
			new ServerMessage().execute(e.getMessage(), con);
		}
		
		return null;
	}

}
