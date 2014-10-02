package GameServer.GamePackets;

import java.nio.ByteBuffer;

import Connections.Connection;
import Player.PlayerConnection;
import Player.Character;
import Tools.BitTools;
import Encryption.Decryptor;

public class LocationSync implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	
	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException{
		byte[] locSync=null;
		if(con!=null){
			byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
			
			for(int i=0;i<decrypted.length;i++) {
				decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
			}
			
			decrypted = Decryptor.Decrypt(decrypted);
			
			byte[] x = new byte[4];
			byte[] y = new byte[4];
			
			for(int i=0;i<4;i++) {
				x[i] = decrypted[7-i];
				y[i] = decrypted[11-i];
			}
			
			Character current = ((PlayerConnection)con).getActiveCharacter();
			//old: current.updateLocation(BitTools.byteArrayToFloat(x), BitTools.byteArrayToFloat(y));
			//new: movesyncsystem
			current.startMoveTo(BitTools.byteArrayToFloat(x), BitTools.byteArrayToFloat(y));
			
			locSync = new byte[56]; 
			
			locSync[0] = (byte)locSync.length;
			locSync[4] = (byte)0x04;
			locSync[6] = (byte)0x0D;
			
			byte[] id = BitTools.intToByteArray(current.getCharID());
			byte externmove[] = new byte[48]; 
			
			externmove[0] = (byte)externmove.length;
			externmove[4] = (byte)0x05;
			externmove[6] = (byte)0x0D;
			
			externmove[8]  = (byte)0x01;
			
			//byte[] mahtimuna = new byte[] {(byte)0x01, (byte)0xed, (byte)0x5f, (byte)0xbf, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0x3f, (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x36};
			//byte[] mahtileka = new byte[] {(byte)0x13, (byte)0xad, (byte)0xbc, (byte)0x3e};
			
			//for(int i=0;i<mahtimuna.length;i++) {
			//	externmove[i+36] = mahtimuna[i];
			//}
			
			/*
			 * location sync has 2 sets of coordinates: 1st is current location, 2nd is next location sync will take place at
			 * but I'm taking a little shortcut here and just setting both as new intented location as told to us by client
			 */
			for(int i=0;i<4;i++) {
				//1st set
				locSync[16+i] = x[i];   
				locSync[20+i] = y[i]; 
				//2nd set 
				locSync[24+i] = x[i];
				locSync[28+i] = y[i];
				//character id
				locSync[i+12] = id[i];
				
				//externmove is same thing, except this time the packet is to be sent to other players nearby telling them our character moved
				externmove[i+20] = x[3-i];
				externmove[i+24] = y[3-i];			   
				externmove[i+28] = x[3-i];
				externmove[i+32] = y[3-i];			
				externmove[i+12] = id[i];
				//externmove[i+16] = mahtileka[i];
			}
			
			//run/walk
			locSync[40]=decrypted[13];
			current.setWalking(decrypted[13]==0);
			
			externmove[36]=decrypted[13];
			//externmove[37]=(byte)0x04;
			//externmove[38]=(byte)0x4a;
			//externmove[39]=(byte)0x08;
			
			//externmove[42]=(byte)0x80;
			//externmove[43]=(byte)0x3f;
			//externmove[44]=(byte)0x03;//decrypted[17];
			
			current.sendToMap(externmove);
		}
		
		con.addWrite(locSync);
		throw new PaketException();
	}

}
