package GameServer.GamePackets;

import item.vendor.VendorException;

import java.nio.ByteBuffer;

import Connections.Connection;
import Encryption.Decryptor;
import Player.Character;
import Player.PlayerConnection;
import World.WMap;
import Tools.BitTools;
import GameServer.ServerPackets.ServerMessage;

public class VendorBuyItem implements Packet {

	Character buy;
	
	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		if(!WMap.getInstance().CharacterExists((int)decrypted[0]))
			throw new PaketException("Character does not exist!");
		
		buy = ((PlayerConnection)con).getActiveCharacter();
		Character sold = WMap.getInstance().getCharacter((int)decrypted[0]);
		long price = BitTools.byteArrayToInt(new byte[]{decrypted[16], decrypted[17], decrypted[18], decrypted[19], decrypted[20], decrypted[21], decrypted[22], decrypted[23]});
		
		try {
			return sold.getVendor().soldItem(buy, price, (int)decrypted[4], (int)decrypted[5], (int)decrypted[6], (int)decrypted[7], (int)decrypted[8]);
		} catch (VendorException e) {
			// TODO Auto-generated catch block
			new ServerMessage().execute(e.getMessage(), con);
			return null;
		}	
	}
}
