package GameServer.GamePackets;

import item.inventory.InventoryException;
import item.upgrades.UpgradeMaster;

import java.nio.ByteBuffer;

import Connections.Connection;
import Encryption.Decryptor;
import GameServer.ServerPackets.ServerMessage;
import Player.Character;
import Player.PlayerConnection;

public class UpgradeItem implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling Upgrade Item");
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		
		byte[] upgr=null;
		
		try{
			upgr=UpgradeMaster.upgradeItem(cur, decrypted[8], decrypted[9]);
		}catch(InventoryException e){
			new ServerMessage().execute(e.getMessage(),con);
			throw new PaketException();
		}
		
		return upgr;
		
	}
	
}
