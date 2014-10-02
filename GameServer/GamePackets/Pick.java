package GameServer.GamePackets;

import item.DroppedItem;
import item.ItemCache;
import item.ItemFrame;
import item.inventory.InventoryException;

import java.nio.ByteBuffer;

import Connections.Connection;
import Encryption.Decryptor;
import GameServer.ServerPackets.ServerMessage;
import Player.Character;
import Player.CharacterPackets;
import Player.PlayerConnection;
import Tools.BitTools;
import World.WMap;

public class Pick implements Packet {
	ItemCache cache = ItemCache.getInstance();

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}


	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		for(int i=0;i<decrypted.length;i++) {
            System.out.printf("%02x ", (decrypted[i]&0xFF));
		}
		
		System.out.println("Handling pick");
		byte[] pick = new byte[40];
		byte[] uid = new byte[4];
		
		for(int i=0;i<4;i++) {
			uid[i] = decrypted[i];
		}
		
		int iuid = BitTools.byteArrayToInt(uid);
		int col = (int)decrypted[4] & 0xFF;
		int row = (int)decrypted[5] & 0xFF;
		System.out.println("\nAttempting to pick up item with uid: " + iuid + " at row: " + row + " col: " + col);
		DroppedItem item=WMap.getInstance().getItem(Integer.valueOf(iuid));
		int itemID =item.getItem().getId();
		ItemFrame itams=((ItemFrame)(cache.getItem(itemID)));
		int amount=item.getAmount();
		boolean sendPckt=false;
		
		try{
			
			if(itams != null) {
				//update inv first
				cur.getInventory().updateInv();
				if(amount<=10000 || itams.getId()==217000501){
					if(itams.getId()==217000501 && cur.getPt()!=null){
						cur.getPt().sendItemToMembers(itams, amount, iuid);
					}else{
						if(cur.getInventory().pickItem(itams,amount)!=0)
							throw new InventoryException("Cannot pick item [coin limit]");
						sendPckt=true;
					}
				}else{
					new ServerMessage().execute("Cannot pick item [amount>=10000]",con);
					throw new PaketException();
				}
			} else {
				new ServerMessage().execute("Cannot pick item [item not found on server]",con);
				throw new PaketException();
			}
			
			if (sendPckt)
				pick=CharacterPackets.getPickItemPacket(cur, itams, amount, iuid, decrypted[4], decrypted[5], decrypted[6]);
			
			item.leaveGameWorld();
			
			//save inv
			cur.getInventory().saveInv(cur);
			
		}catch(InventoryException e){
    	
			new ServerMessage().execute(e.getMessage(),con);
			throw new PaketException();
		
		}finally{
			
			//test
			cur.getInventory().printInv();
	
		}
		
		WMap.getInstance().removeItem(Integer.valueOf(iuid));
		
		if(sendPckt)
			return pick;
		return null;
	}

}
