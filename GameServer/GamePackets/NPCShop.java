package GameServer.GamePackets;

import item.ItemInInv;
import item.inventory.InventoryException;

import java.nio.ByteBuffer;

import Connections.Connection;
import Encryption.Decryptor;
import GameServer.ServerPackets.ServerMessage;
import NPCs.NPCMaster;
import NPCs.Npc;
import Player.Character;
import Player.PlayerConnection;
import Tools.BitTools;

public class NPCShop implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling NPC shop");
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		
		byte[] chid=BitTools.intToByteArray(cur.getCharID());
		
		byte[]npcshop = new byte[56];
		
		npcshop[0] = (byte)0x38;
		npcshop[4] = (byte)0x04;
		npcshop[6] = (byte)0x13;
		npcshop[8] = (byte)0x01;
		for(int i=0;i<4;i++) {
			npcshop[12+i] = chid[i];
		}
		
		if(decrypted[8]!=0){
			
			try{
				
				//update inv first
                cur.getInventory().updateInv();
				
				byte[] uidBytes=new byte[4];
				for(int i=0;i<4;i++){
					uidBytes[i]=decrypted[i];
				}
				int uid=BitTools.byteArrayToInt(uidBytes);
				Npc npc=NPCMaster.getNpcByUid(uid);
				if(npc==null){
					throw new InventoryException("Cannot buy item [NPC does not exist]");
				}
				int itemID=npc.getItem(decrypted[4]);
				ItemInInv item=new ItemInInv(itemID,decrypted[8]);
				if(item.getItem()==null){
					throw new InventoryException("Cannot buy item [item does not exist]");
				}
				cur.getInventory().buyItem(item, decrypted[7], decrypted[6]);
				byte[] itid=BitTools.intToByteArray(itemID);
				byte[] newgold=BitTools.intToByteArray(cur.getInventory().getCoins());
				byte[] newvp=BitTools.intToByteArray(cur.getInventory().getVendingPoints());
				
				for(int i=0;i<4;i++) {
					npcshop[48+i] = itid[i];
					npcshop[29+i] = newvp[i];  // vending points
				}
				for(int i=0;i<newgold.length;i++) {
					npcshop[16+i] = newgold[i];
				}
				
				npcshop[24] = (byte)0x01;
				
				npcshop[26] = decrypted[5]; // invSLOT
				npcshop[27] = decrypted[6]; // invY
				npcshop[28] = decrypted[7]; // invX
		
				npcshop[52] = decrypted[8]; // stack
				
				//save inv
            	cur.getInventory().saveInv(cur);
            	//show info
    			if(cur.getShowInfos())
    				new ServerMessage().execute("bought item with ID "+itemID,con);
				
			}catch(InventoryException e){
				
				new ServerMessage().execute(e.getMessage(),con);
    			throw new PaketException();
    			
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}else{
			
			npcshop[16] = (byte)0x47;
			npcshop[17] = (byte)0x76;
			npcshop[18] = decrypted[0];
			
			npcshop[24] = (byte)0x01;
			
			npcshop[29] = (byte)0x47;
			
			npcshop[32] = (byte)0x4b;
			npcshop[33] = (byte)0x6b;
			
			npcshop[38] = (byte)0x80;
			npcshop[39] = (byte)0x3f;
			
			npcshop[42] = (byte)0x80;
			npcshop[43] = (byte)0x3f;
			
		}
		
		return npcshop;
		
	}
	
}
