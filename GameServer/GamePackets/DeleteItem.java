package GameServer.GamePackets;

import item.inventory.InventoryException;
import item.inventory.InventoryPackets;

import java.nio.ByteBuffer;

import Player.Character;
import Player.PlayerConnection;
import Connections.Connection;
import Encryption.Decryptor;
import GameServer.ServerPackets.ServerMessage;

public class DeleteItem implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling delete");
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		byte[] delete = null;
		
		//update inv first
		cur.getInventory().updateInv();
		
		try{
			
			cur.getInventory().removeItem(decrypted[1]);
			
			delete=InventoryPackets.getInventoryDeletePacket(cur, decrypted[1], decrypted[0]);
			
			//save inv
			cur.getInventory().saveInv(cur);
			
		}catch(InventoryException e){
			
			new ServerMessage().execute(e.getMessage(),con);
			throw new PaketException();
			
		}finally{
			
			//test
			cur.getInventory().printInv();
			
		}
		
		return delete;
	}
	
}
