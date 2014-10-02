package item.vendor;
import java.awt.Point;
import java.util.List;
import java.util.Map;

import Player.Character;
import Tools.BitTools;
import World.WMap;
import item.ItemVendor;

public class VendorPackets {
	
	static byte[] createVendorFrame(Character cur, int state, String shopname) {
		
		byte[] nameByte = BitTools.stringToByteArray(shopname);
		byte[] chid = BitTools.intToByteArray(cur.getCharID());
		byte[] venState = new byte[52];
		
		venState[0] = (byte)venState.length;
		venState[4] = (byte)0x04;
		venState[6] = (byte)0x37;
		
		venState[8] = (byte)0x01;
		venState[9] = (byte)0x9d;
		venState[10] = (byte)0x0f;
		venState[11] = (byte)0xbf;
		
		for(int i=0;i<4;i++) {
			venState[12+i] = chid[i];
		}
		
		venState[16] = (byte)0x01;
		
		//1 = open, 0 = close
		venState[18] = (byte)state;
		
		if(state == 0)
		{
			System.out.print("Closing vendorshop: " + shopname);
			venState[50] = (byte)0xdb;
			venState[51] = (byte)0x2a;
		}
		else
		{
			System.out.print("Creating vendorshop: " + shopname);
			for(int i=0;i<30;i++)
			{
				if(nameByte[i] != (byte)0x00)
				{
					//shop name
					venState[19+i] = nameByte[i];
				}
				else
				{
					break;
				}
			}
			
			venState[50] = (byte)0x0f;
			venState[51] = (byte)0xbf;
		}
		
		//cur.sendToMap(venState);
		return venState;
	}
	
	static byte[] addItemToVendor(ItemVendor item, int state, Character cur, int x, int y) {
		
		System.out.print("Adding item " + item.getId() + " of index " + item.getInvIndex() + " on position(x, y) (" + x + ", " + y +")");
		byte[] price = BitTools.longToByteArray(item.getPrice());
		byte[] chid = BitTools.intToByteArray(cur.getCharID());
		byte[] venItem = new byte[36];
		
		venItem[0] = (byte)venItem.length;
		venItem[4] = (byte)0x04;
		venItem[6] = (byte)0x39;

		venItem[8] = (byte)0x01;
		venItem[9] = (byte)0x04; //f5 need when remove?
		venItem[10] = (byte)0x67; //10 need when remove?
		venItem[11] = (byte)0x28; //29 need when remove?
	
		for(int i=0;i<4;i++) {
			venItem[12+i] = chid[i];
		}

		venItem[16] = (byte)0x01;

		//Status: 1 = add, 0 = remove
		venItem[18] = (byte)state;
		
		//item inventory id
		venItem[19] = (byte)item.getInvIndex();
		//item number (Unique id for vendor UI)
		venItem[20] = (byte)item.getId();
		
		//y
		venItem[21] = (byte)y;
		//x
		venItem[22] = (byte)x;
		
		//amount
		venItem[24] = (byte)item.getAmount();
		
		venItem[26] = (byte)0x0f;
		venItem[27] = (byte)0xbf;

		//price 6 bytes long
		//first byte can be 0x00 when high number
		venItem[28] = price[0];
		for(int i=0;i<price.length-1;i++)
		{
			venItem[29+i] = price[1+i];
		}
		
		return venItem;
	}	
	
	static byte[] buyItemFromVendor(Character buy, Character sold, int index, int invSlot, int x, int y, int amount) {
		System.out.println("Handling Buying vendor item");
		
		byte[] chid = BitTools.intToByteArray(buy.getCharID());
		byte[] chid1 = BitTools.intToByteArray(sold.getCharID());
		byte[] newMoneyCur = BitTools.intToByteArray(buy.getInventory().getCoins());

		byte[] venBuy = new byte[40];
		venBuy[0] = (byte)venBuy.length;
		
		venBuy[4] = (byte)0x04;
		venBuy[6] = (byte)0x3a;
		
		venBuy[8] = (byte)0x01;
		venBuy[9] = (byte)0x6b;
		venBuy[10] = (byte)0x15;
		venBuy[11] = (byte)0x08;
		
		for(int i=0;i<4;i++) {
			venBuy[12+i] = chid[i];
			venBuy[20+i] = chid1[i];
			venBuy[32+i] = newMoneyCur[i];
		}
		
		venBuy[16] = (byte)0x01;
		
		venBuy[18] = (byte)0xDB;
		venBuy[19] = (byte)0x2A;
		
		venBuy[24] = (byte)index;
		venBuy[25] = (byte)invSlot;
		venBuy[26] = (byte)x;
		venBuy[27] = (byte)y;
		venBuy[28] = (byte)amount;

		byte[] newMoneySold = BitTools.intToByteArray(sold.getInventory().getCoins());
		byte[] venSold = venBuy.clone();
		for(int i=0; i<4; i++) {
			venSold[12+i] = chid1[i];
			venSold[32+i] = newMoneySold[i];
		}
		
		sold.addWritePacketWithId(venSold);
		return venBuy;
	}
	
	static byte[] openVendorFrame(Character cur, Map<Integer, ItemVendor> itemsMap, Map<Integer, Point> coords, int other, int vendorId) {
		byte[] chid = BitTools.intToByteArray(cur.getCharID());
		
		byte[] venOpen = new byte[1304];
		byte[] cdatalength = BitTools.intToByteArray(venOpen.length);
		
		venOpen[0] = (byte)cdatalength[0];
		venOpen[1] = (byte)cdatalength[1];
		
		venOpen[4] = (byte)0x04;
		venOpen[6] = (byte)0x38;
		
		venOpen[8] = (byte)0x01;
		//venOpen[9] = (byte)0x31;
		
		for(int i=0;i<4;i++) {
			venOpen[12+i] = chid[i];
		}
		
		venOpen[16] = (byte)0x01;

		venOpen[20] = (byte)other;
		venOpen[21] = (byte)vendorId;
		int i = 0;
		for (Map.Entry<Integer, ItemVendor> index : itemsMap.entrySet()) {
			for (Map.Entry<Integer, Point> index1 : coords.entrySet()) {
				if(index.getKey() == index1.getKey())
				{
					byte[] itemId = BitTools.intToByteArray(index.getValue().getItemFrame().getId());
					byte[] price = BitTools.longToByteArray(index.getValue().getPrice());
					
					//coords
					venOpen[26+i] = (byte)index1.getValue().y;
					venOpen[27+i] = (byte)index1.getValue().x;
					//item ID
					for(int j=0;j<4;j++) {
						venOpen[28+i+j] = itemId[j];
					}
					//Amount
					venOpen[32+i] = (byte)index.getValue().getAmount();
					
					System.out.print("item " + index.getKey() + " has price " + index.getValue().getPrice() + " \n");

					//Price packets are 8 bytes long
					for(int j=0;j<price.length;j++) {
						venOpen[792+j+ (i/12)*8] = price[j];
					}
					break;
				}
			}
			i+=12; //size of one item packet
		}	
		return venOpen;
	}
	
	public static byte[] getVendorListPacket(Character cur) {
		List<Character> vendorList = WMap.getInstance().getVendingList();
		int vendorSize = vendorList.size() * 36;
		byte[] chid = BitTools.intToByteArray(cur.getCharID());
		byte[] vendorlist = new byte[vendorSize + 14];
		byte[] size = BitTools.intToByteArray(vendorlist.length);
		
		for(int i=0;i<4;i++) {
			vendorlist[i] = size[i];
			vendorlist[9+i] = chid[i];
		}
		
		vendorlist[4] = (byte)0x04;
		vendorlist[6] = (byte)0x4a;
		vendorlist[8] = (byte)0x01;
		
		vendorlist[13] = (byte)0x01;
		
		if(!vendorList.isEmpty()) {
			for(int i=0;i<vendorList.size();i++) {
				Character ch = vendorList.get(i);
				byte[] bCh = BitTools.intToByteArray(ch.getuid()); 
				byte[] vendorname = BitTools.stringToByteArray(ch.getVendor().getShopname());
				//36 is length of shop
				for(int j=0;j<4;j++) {
					vendorlist[14 + i*36 + j] = bCh[j];
					vendorlist[18 + i*36 + j] = vendorname[j];
				}		
			}
		}
		
		return vendorlist;
		
	}
}
