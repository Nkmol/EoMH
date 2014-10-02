package GameServer.GamePackets;

import java.nio.ByteBuffer;

import Player.Character;
import Player.PlayerConnection;
import Tools.BitTools;
import Connections.Connection;
import Encryption.Decryptor;

public class Revive implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling Revival");
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		byte[] cid = BitTools.intToByteArray(cur.getCharID());
		byte[] x=BitTools.floatToByteArray(cur.getlastknownX());
		byte[] y=BitTools.floatToByteArray(cur.getlastknownY());
		if(cur.isReviveSave()){
			cur.setReviveSave(false);
		}else{
			cur.decreaseExp(0.015f);
		}
		//long expL=cur.getExp();
		//byte[] exp=BitTools.longToByteArray(expL);
		
		byte[] revival = new byte[48];
		
		revival[0] = (byte)revival.length;
		revival[4] = (byte)0x04;
		revival[6] = (byte)0x03;
		revival[8] = (byte)0x01;
		revival[9] = (byte)0x9e;
		revival[10] = (byte)0x0f;
		revival[11] = (byte)0xbf;
		
		for(int i=0;i<4;i++) {
			revival[12+i] = cid[i];
		}
		
		revival[16] = (byte)0x01;
		
		revival[18] = (byte)0x15;
		revival[19] = (byte)0x08;
		revival[20] = (byte)0xfe;
		
		revival[24] = (byte)0x0d;
		revival[25] = (byte)0x02;
		
		for(int i=0;i<8;i++){
			//revival[28+i]=exp[i];
		}
		
		for(int i=0;i<4;i++){
			revival[40+i]=x[i];
			revival[44+i]=y[i];
		}
		
		con.addWrite(revival);
		
		cur.revive(true);
		
		return null;
	}
	
}
