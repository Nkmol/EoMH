package GameServer.GamePackets;

import java.nio.ByteBuffer;

import Connections.Connection;
import Encryption.Decryptor;
import Player.Character;
import Player.PlayerConnection;
import Player.CharacterPackets;
import item.vendor.Vendor;
import item.vendor.VendorException;
import ServerCore.ServerFacade;
import Tools.BitTools;

public class VendorState implements Packet {
	
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
		int state = (int)decrypted[0];
		byte[] bShopname = new byte[31];
		for(int i=0;i<30;i++)
		{
			if(decrypted[1+i] != (byte)0x00)
			{
				//shop name
				bShopname[i] = decrypted[1+i];
			}
			else
			{
				break;
			}
		}
		
		if(state == 1)
		{
			String shopname = BitTools.byteArrayToString(bShopname);
			Vendor vendor = new Vendor(cur, shopname);
			cur.setVendor(vendor);
			cur.sendToMap(CharacterPackets.getExtVending(cur));
			try {
				//ServerFacade.getInstance().addWriteByChannel(cur.GetChannel(), cur.getVendor().addToVendorList());
				cur.sendToMap(cur.getVendor().addToVendorList());
			} catch (VendorException e) {
				// TODO Auto-generated catch block
				System.out.print(e.getMessage());
			}
			return cur.getVendor().createVendor();
		}
		else
		{		
			byte[] del = cur.getVendor().deleteVendor();
			try {
				//when someone casts this packet to you, you also have to remove your own shop
				byte[] removeVendorList = cur.getVendor().removeFromVendorList();
				ServerFacade.getInstance().addWriteByChannel(cur.GetChannel(), removeVendorList);
				cur.sendToMap(removeVendorList);
			} catch (VendorException e) {
				// TODO Auto-generated catch block
				System.out.print(e.getMessage());
			}
			cur.setVendor(null);
			cur.sendToMap(CharacterPackets.getExtVending(cur));
			return del;
		}
	}
	
}
