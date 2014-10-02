package GameServer.GamePackets;

import item.inventory.InventoryException;

import java.nio.ByteBuffer;

import Connections.Connection;
import Database.CharacterDAO;
import Encryption.Decryptor;
import GameServer.ServerPackets.ServerMessage;
import Player.Character;
import Player.CharacterPackets;
import Player.PlayerConnection;
import Tools.BitTools;

public class InventoryManagement implements Packet {

        @Override
        public void execute(ByteBuffer buff) {
                // TODO Auto-generated method stub
                
        }

        
        public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
                System.out.print("Handling inventory: ");
                byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
                
                for(int i=0;i<decrypted.length;i++) {
                        decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
                }
                
                decrypted = Decryptor.Decrypt(decrypted);
                
                for(int i=0;i<decrypted.length;i++) {
                        System.out.printf("%02x ", (decrypted[i]&0xFF));
                }
                
                System.out.println();
                
                byte[] inv = new byte[28];
                Character cur = ((PlayerConnection)con).getActiveCharacter();
                
                //update inv and equip first
                cur.getInventory().updateInv();
        		cur.getEquips().updateEquip();
        		
        		try{
        			byte[] amountByte={decrypted[8],decrypted[9],decrypted[10],decrypted[11]};
        			int amount=BitTools.byteArrayToInt(amountByte);
        			//0 is unequip, 1 is move, 2 is move and swap
        			if(decrypted[0]==(byte)0x00){
        				cur.getInventory().unequipItem(decrypted[1], decrypted[4], decrypted[3], cur.getEquips());
        			}else{
        				cur.getInventory().moveItem(decrypted[1], decrypted[2], amount, decrypted[4], decrypted[3]);
        			}
        			
                	inv[0] = (byte)inv.length;
                	inv[4] = (byte)0x04;
                	inv[6] = (byte)0x10;
                	
                	byte[] chid = BitTools.intToByteArray(cur.getCharID());
                	
                	for(int i=0;i<4;i++) {
                		inv[12+i] = chid[i];
                		inv[19+i] = decrypted[i+1];
                	}
                	
                	inv[16] = (byte)0x01;
                	inv[18] = decrypted[0];
                	//amount
                	inv[24] = decrypted[8];
                	inv[25] = decrypted[9];
                	inv[26] = decrypted[10];
                	inv[27] = decrypted[11];
                	
                	//save inv and equip
                	cur.getInventory().saveInv(cur);
        			cur.getEquips().saveEquip();
        			//update char and send packets
        			if(decrypted[0]==(byte)0x00){
        				cur.getEquips().calculateEquipStats();
        				cur.calculateCharacterStats();
        				CharacterDAO.saveEquipments(cur.getCharID(), cur.getEquips());
        				cur.getArea().sendToMembers(cur.getuid(), CharacterPackets.getExtEquipPacket(cur, decrypted[1],0));
        			}
        		}catch(InventoryException e){
                	
                	new ServerMessage().execute(e.getMessage(),con);
        			throw new PaketException();
        			
                }finally{
        		
                	//test
                	cur.getInventory().printInv();
                	cur.getEquips().printEquip();
        		
                }
                
                return inv;
        }

}