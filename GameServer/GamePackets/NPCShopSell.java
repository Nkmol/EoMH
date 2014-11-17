package GameServer.GamePackets;

import item.inventory.InventoryException;

import java.nio.ByteBuffer;

import Connections.Connection;
import Encryption.Decryptor;
import GameServer.ServerPackets.ServerMessage;
import Player.Character;
import Player.PlayerConnection;
import Tools.BitTools;

public class NPCShopSell implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling NPC shop sell");
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		
		byte[] chid=BitTools.intToByteArray(cur.getCharID());
		byte[] npcshop = new byte[32];
		
		try{
			
			//update inv first
            cur.getInventory().updateInv();
            
            cur.getInventory().sellItem(decrypted[5], decrypted[8]);
            byte[] newgold=BitTools.intToByteArray(cur.getInventory().getCoins());
            
    		npcshop[0] = (byte)0x20;
    		npcshop[4] = (byte)0x04;
    		npcshop[6] = (byte)0x14;
    		npcshop[8] = (byte)0x01;
    		for(int i=0;i<4;i++) {
    			npcshop[12+i] = chid[i];
    		}
    		for(int i=0;i<newgold.length;i++) {
    			npcshop[24+i] = newgold[i]; // total new gold
    		}
    		npcshop[16] = (byte)0x01;
    		
    		npcshop[18] = (byte)0x01;
    		npcshop[19] = decrypted[5];
    		npcshop[20] = decrypted[8];
            
            //save inv
        	cur.getInventory().saveInv(cur);
			
		}catch(InventoryException e){
			
			new ServerMessage().execute(e.getMessage(),con);
			throw new PaketException();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return npcshop;
		
	}
	
}
