package GameServer.GamePackets;

import item.inventory.InventoryException;

import java.nio.ByteBuffer;

import Player.Character;
import Player.PlayerConnection;
import Tools.BitTools;
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
		byte[] delete = new byte[20];
		
		//update inv first
		cur.getInventory().updateInv();
		
		try{
			
			cur.getInventory().removeItem(decrypted[1]);
		
			byte[] chid = BitTools.intToByteArray(cur.getCharID());
			
			delete[0] = (byte)delete.length;
			delete[4] = (byte)0x04;
			delete[6] = (byte)0x15;
			
			for(int i=0;i<4;i++) {
				delete[12+i] = chid[i];
			}
			
			delete[16] = (byte)0x01;
			delete[18] = decrypted[0];
			delete[19] = decrypted[1];
			
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
