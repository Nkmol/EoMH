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
		//byte[] locSync=null;
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
			
			current.setWalking(decrypted[13]==0);
			
			//current.sendMovementPackets(BitTools.byteArrayToFloat(x), BitTools.byteArrayToFloat(y), decrypted[13]);
			
			//externmove[37]=(byte)0x04;
			//externmove[38]=(byte)0x4a;
			//externmove[39]=(byte)0x08;
			
			//externmove[42]=(byte)0x80;
			//externmove[43]=(byte)0x3f;
			//externmove[44]=(byte)0x03;//decrypted[17];
		}
		
		//con.addWrite(locSync);
		throw new PaketException();
	}

}
