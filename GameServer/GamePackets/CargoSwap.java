package GameServer.GamePackets;

import item.cargo.CargoException;

import java.nio.ByteBuffer;

import Connections.Connection;
import Encryption.Decryptor;
import GameServer.ServerPackets.ServerMessage;
import Player.Character;
import Player.PlayerConnection;

public class CargoSwap implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException { 
		System.out.println("Handling cargo item");
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
				
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
				
		decrypted = Decryptor.Decrypt(decrypted);
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		
		//decrypted[0] //??
		//decrypted[1] //y from
		//decrypted[2] //x from
		//decrypted[3] //
		//decrypted[4] //y
		//decrypted[5] //x
		
		System.out.print("[0] " + decrypted[0] + " [1] " + decrypted [1] + " [2] " + decrypted [2] + " [3] " + decrypted [3]);

		//The index byte can differ depending on what the function is
		if((int)decrypted[3] != (int)decrypted[0]) {	
			System.out.print(" swap");
			try {
				return cur.getCargo().Swap((int)decrypted[0], (int)decrypted[3], (int)decrypted[4], (int)decrypted[5]);
			} catch (CargoException e) {
				new ServerMessage().execute(e.getMessage(), con);
			}
		}
		else {
			System.out.print(" move");
			try {
				return cur.getCargo().Move((int)decrypted[1], (int)decrypted[0], (int)decrypted[4], (int)decrypted[5]);
			} catch (CargoException e) {
				new ServerMessage().execute(e.getMessage(), con);
			}
		}
		
		return null;
	}

}
