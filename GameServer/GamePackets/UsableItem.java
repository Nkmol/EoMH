package GameServer.GamePackets;

import item.ConsumableItem;
import item.ItemInInv;
import item.inventory.InventoryException;

import java.nio.ByteBuffer;

import Player.Character;
import Player.CharacterPackets;
import Player.PlayerConnection;
import ServerCore.ServerFacade;
import Tools.BitTools;
import Buffs.BuffMaster;
import Buffs.BuffsException;
import Buffs.ItemBuff;
import Connections.Connection;
import Encryption.Decryptor;
import GameServer.ServerPackets.ServerMessage;

public class UsableItem implements Packet {

	Character cur;
	
	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling Usable Item");
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		cur = ((PlayerConnection)con).getActiveCharacter();
		
        byte[] useitem = new byte[52];
        byte[] extuseitem = new byte[40];
        
        cur.getInventory().updateInv();
        
        ItemInInv item=null;
        try{
        	
        	//COOLDOWN
        	if(!cur.getUseableReady()){
        		throw new PaketException();
        	}else{
        		cur.setUseableReadyFalse();
        	}
        	
        	item=cur.getInventory().decrementItem(decrypted[1]);
        	byte[] amount=BitTools.intToByteArray(item.getAmount());
        	
        	useitem[0] = (byte)useitem.length;
        	useitem[4] = (byte)0x04;
        	useitem[6] = (byte)0x05;
        	
        	useitem[8] = (byte)0x01;
        	useitem[9] = (byte)0x3f;
        	useitem[10] = (byte)0x5d;
        	useitem[11] = (byte)0x08;
        	
        	byte[] cid = BitTools.intToByteArray(cur.getCharID());
        	
        	for(int i=0;i<4;i++){
        		useitem[12+i] = cid[i];
        		useitem[20+i] = amount[i];
        		useitem[28+i] = cid[i];
        	}
        	
        	useitem[16] = (byte)0x01;
        	useitem[18] = (byte)0x01;
        	useitem[19] = decrypted[1];
        	
        	useitem[24] = (byte)0x01;
        	useitem[25] = (byte)0xf5;
        	useitem[26] = (byte)0x10;
        	useitem[27] = (byte)0x29;
        	
        	useitem[49] = (byte)0x90;
        	useitem[50] = (byte)0xd2;
        	useitem[51] = (byte)0x2a;
        	
        	//ext packet
        	extuseitem=CharacterPackets.getExtUseItemPacket(cur, item.getItem().getId());
        	
        	cur.getInventory().saveInv(cur);
        	
        }catch(InventoryException e){
        	
        	new ServerMessage().execute(e.getMessage(),con);
			throw new PaketException();
        }
        
        con.addWrite(useitem);
        cur.sendToMap(extuseitem);
        
        ConsumableItem conitem = ((ConsumableItem)(item.getItem()));
        if(item!=null && item.getItem() instanceof ConsumableItem){
        	//HEAL
        	if(conitem.getHealhp()!=0 || conitem.getHealmana()!=0)
        		cur.addHpMpSp(conitem.getHealhp(), conitem.getHealmana(), (short)0);
    		//TELEPORT
    		if(conitem.getTelemap()!=0){
    			cur.teleportTo(conitem.getTelemap(), conitem.getTelex(), conitem.getTeley());
    			ServerFacade.getInstance().addWriteByChannel(cur.GetChannel(), CharacterPackets.getTeleportPacket());
    		}
    	}
        
        //BUFF
        if(conitem.getBuffId()[0] > 0) {
	        //ItemBuff[] itembuff = new ItemBuff[conitem.getBuffId().length];
	        for(int i=0; i<conitem.getBuffId().length;i++) {
	        	if(conitem.getBuffId()[i] > 0) {
	        		long time = BuffMaster.timeClientToServer(conitem.getBuffTime()[i]); // MH time = int * 4. Also converting to miliseconds
		            ItemBuff buff = new ItemBuff(cur, conitem.getBuffId()[i], time, conitem.getBuffValue()[i]);
		        	
		            if(buff.getAction() == null) {
		            	System.out.println("Buffaction not created for buffid " + conitem.getBuffId()[i]);
		            	return null;
		            }
		        	//Run buff 
		            try {
						buff.activate();
					} catch (BuffsException e) {
						System.out.print(e.getMessage());
				        //TODO add the used item or don't let the packet be excecuted
					}
	        	}
	        }
        }
        
        return null;
	}
	
}
