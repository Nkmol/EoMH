package GameServer.GamePackets;

import item.ItemCargo;
import item.ItemInInv;
import item.cargo.CargoPackets;

import java.nio.ByteBuffer;

import Connections.Connection;
import Encryption.Decryptor;
import Player.Character;
import Player.PlayerConnection;

public class CargoDepost implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling cargo item depost");
				
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
				
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
				
		decrypted = Decryptor.Decrypt(decrypted);
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		
		//decrypted[1] //inventory index(item)
		//decrypted[3] //y place
		//decrypted[4] //x place
		
		if((int)decrypted[0] == 1) {
			return cur.getCargo().Depost((int)decrypted[1], (int)decrypted[4], (int)decrypted[3]);
		}
		else if((int)decrypted[0] == 0) return null;
			//cur.getCargo().Withdraw();
		
		return null;
	}

}
