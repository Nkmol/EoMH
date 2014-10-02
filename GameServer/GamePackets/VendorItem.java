package GameServer.GamePackets;

import item.ItemVendor;
import item.ItemInInv;
import item.vendor.VendorException;
import GameServer.ServerPackets.ServerMessage;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import Connections.Connection;
import Encryption.Decryptor;
import Player.Character;
import Player.PlayerConnection;
import Tools.BitTools;

public class VendorItem implements Packet {
	
	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}
	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		if(cur.getVendor() == null)
			throw new PaketException("Vendingshop does not exist");
		
		if(decrypted[0] == 0) {
			// remove item by 'item vendor id'
			try {
				return cur.getVendor().removeItem((int)decrypted[2], (int)decrypted[0], (int)decrypted[4], (int)decrypted[3]);
			} catch (VendorException e) {
				// TODO Auto-generated catch block
				new ServerMessage().execute(e.getMessage(), con);
				return null;
			}
		}
		else {
			long price = BitTools.byteArrayToLong(new byte[]{decrypted[8], decrypted[9], decrypted[10], decrypted[11], decrypted[12], decrypted[13], decrypted[14], decrypted[15]});
			LinkedList<Integer> seq = cur.getInventory().getSeqSaved();
			ItemInInv itemInv = cur.getInventory().getInvSaved().get(seq.get((int)decrypted[1])); // get item by invId
			ItemVendor item;
			try {
				item = cur.getVendor().createItem(itemInv.getItem(), (int)decrypted[1], price, (int)decrypted[6], (int)decrypted[2]);
			} catch (VendorException e1) {
				// TODO Auto-generated catch block
				new ServerMessage().execute(e1.getMessage(), con);
				return null;
			}
			
			try {
				return cur.getVendor().addItem(item, (int)decrypted[0], (int)decrypted[4], (int)decrypted[3]);
			} catch (VendorException e) {
				// TODO Auto-generated catch block
				new ServerMessage().execute(e.getMessage(), con);
				return null;
			}
		}
	}

}
