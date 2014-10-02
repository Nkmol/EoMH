package GameServer.GamePackets;

import item.inventory.InventoryException;

import java.nio.ByteBuffer;

import Connections.Connection;
import Encryption.Decryptor;
import GameServer.ServerPackets.ServerMessage;
import Player.Character;
import Player.PlayerConnection;
import Tools.BitTools;

public class DropItem implements Packet {

        @Override
        public void execute(ByteBuffer buff) {
                // TODO Auto-generated method stub
                
        }

        
        public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
                System.out.print("Handling drop: ");
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
                int uid;
                
                //update inv first
        		cur.getInventory().updateInv();
        		
        		try{
        			byte[] amountByte={decrypted[4],decrypted[5],decrypted[6],decrypted[7]};
        			int amount=BitTools.byteArrayToInt(amountByte);
        			
        			uid=cur.getInventory().dropItem(decrypted[1], amount, cur, decrypted[0]==(byte)0xFF);
        			byte[] uidb=BitTools.intToByteArray(uid);
        			
        			inv[0] = (byte)inv.length;
        			inv[4] = (byte)0x04;
        			inv[6] = (byte)0x0E;
                	
        			inv[8] = (byte)0x01;
                	
                	byte[] chid = BitTools.intToByteArray(cur.getCharID());
                	
                	for(int i=0;i<4;i++) {
                		inv[12+i] = chid[i];
                		inv[24+i] = uidb[i];
                	}
                	
                	inv[16] = (byte)0x01;
                	
                	inv[18] = decrypted[0];
                	inv[19] = decrypted[1];
                	
                	//amount
                	inv[20] = decrypted[4];
                	inv[21] = decrypted[5];
                	inv[22] = decrypted[6];
                	inv[23] = decrypted[7];
                	
                	//save inv
        			cur.getInventory().saveInv(cur);
        			
                }catch(InventoryException e){
                	
                	new ServerMessage().execute(e.getMessage(),con);
        			throw new PaketException();
        			
                }finally{
                	
                	//test
            		cur.getInventory().printInv();
                	
                }
        		
                return inv;
        }

}