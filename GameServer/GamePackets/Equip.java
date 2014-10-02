package GameServer.GamePackets;

import item.inventory.InventoryException;

import java.nio.ByteBuffer;

import Connections.Connection;
import Database.CharacterDAO;
import Encryption.Decryptor;
import GameServer.ServerPackets.ServerMessage;
import Player.CharacterPackets;
import Player.PlayerConnection;
import Player.Character;
import Tools.BitTools;

public class Equip implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}


	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling equip");
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		byte[] eq = new byte[24];
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		byte[] cid = BitTools.intToByteArray(cur.getCharID());
		
		//update inv and equip first
		cur.getInventory().updateInv();
		cur.getEquips().updateEquip();
		
		try{
			if(decrypted[0]==(byte)0x00)
				cur.getEquips().swapEquips(decrypted[1], decrypted[2]);
			else
				cur.getInventory().equipItem(decrypted[1], decrypted[2], cur.getEquips());
		
			eq[0] = (byte)eq.length;
			eq[4] = (byte)0x04;
			eq[6] = (byte)0x0C;
			
			eq[8] = (byte)0x01;
			eq[9] = (byte)0xFF;
			eq[10] = (byte)0x14;
			eq[11] = (byte)0x08;
			eq[16] = (byte)0x01;
			eq[18] = decrypted[0];
			
			for(int i=0;i<4;i++) {
				eq[12+i] = cid[i];
			}
			
			eq[19] = decrypted[1];
			eq[20] = decrypted[2];
			
			//save inv and equip
			cur.getInventory().saveInv(cur);
			cur.getEquips().saveEquip();
			//update char
			cur.getEquips().calculateEquipStats();
			cur.calculateCharacterStats();
			CharacterDAO.saveEquipments(cur.getCharID(), cur.getEquips());
			//send packets to other players
			if(decrypted[0]==(byte)0x00){
				cur.getArea().sendToMembers(cur.getuid(), CharacterPackets.getExtEquipPacket(cur, decrypted[1], cur.getEquips().getEquipmentsSaved().get((int)decrypted[1]).getItem().getId()));
				cur.getArea().sendToMembers(cur.getuid(), CharacterPackets.getExtEquipPacket(cur, decrypted[2], cur.getEquips().getEquipmentsSaved().get((int)decrypted[2]).getItem().getId()));
			}else{
				cur.getArea().sendToMembers(cur.getuid(), CharacterPackets.getExtEquipPacket(cur, decrypted[2], cur.getEquips().getEquipmentsSaved().get((int)decrypted[2]).getItem().getId()));
			}
			
		}catch(InventoryException e){
        	
        	new ServerMessage().execute(e.getMessage(),con);
			throw new PaketException();
			
        }finally{
		
        	//test
        	cur.getInventory().printInv();
        	cur.getEquips().printEquip();
		
        }
		
		return eq;
	}

}
