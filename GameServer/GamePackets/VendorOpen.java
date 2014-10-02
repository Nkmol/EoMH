package GameServer.GamePackets;

import java.nio.ByteBuffer;

import Connections.Connection;
import Encryption.Decryptor;
import GameServer.ServerPackets.ServerMessage;
import Player.Character;
import World.WMap;
import Player.PlayerConnection;
import item.vendor.VendorException;

public class VendorOpen implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling opening vendor");
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		if(!WMap.getInstance().CharacterExists((int)decrypted[0])) {
			throw new PaketException("Character does not exist!");
		}
		else {
			Character cur = ((PlayerConnection)con).getActiveCharacter();
			Character other = WMap.getInstance().getCharacter((int)decrypted[0]);
			if(other.getVendor() == null)
				new ServerMessage().execute("Vendor does not exist.", con);
			else {
				try {
					return other.getVendor().open(cur, (int)decrypted[1]);
				} catch (VendorException e) {
					// TODO Auto-generated catch block
					new ServerMessage().execute(e.getMessage(), con);
				}
			}
		}
		return null;
	}

}
