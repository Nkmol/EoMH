package GameServer.GamePackets;

import java.nio.ByteBuffer;

import Player.Character;
import Player.CharacterException;
import Player.PlayerConnection;
import Tools.BitTools;
import Connections.Connection;
import Encryption.Decryptor;
import GameServer.ServerPackets.ServerMessage;

public class SetStats implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling Statpoints");
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		short[] stats=new short[5];
		byte[] statsb=new byte[4];
		for(int i=0;i<5;i++){
			statsb[0]=decrypted[i*2];
			statsb[1]=decrypted[i*2+1];
			stats[i]=(short)BitTools.byteArrayToInt(statsb);
		}
		
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		byte[] cid = BitTools.intToByteArray(cur.getCharID());
		
		byte[] statpckt = new byte[32];
		
		try{
			
			cur.setCStatsInCharwindow(stats);
			byte cpleft[]=BitTools.intToByteArray(cur.getStatPoints());
			
			statpckt[0] = (byte)statpckt.length;
			statpckt[4] = (byte)0x04;
			statpckt[6] = (byte)0x1D;
			statpckt[8] = (byte)0x01;
			
			//CharID
			for(int i=0;i<4;i++)
				statpckt[12+i] = cid[i];
			
			statpckt[16] = (byte)0x01;
			
			//Str
			statpckt[18] = decrypted[0];
			statpckt[19] = decrypted[1];
			
			//Dex
			statpckt[20] = decrypted[2];
			statpckt[21] = decrypted[3];
			
			//Vit
			statpckt[22] = decrypted[4];
			statpckt[23] = decrypted[5];
			
			//Int
			statpckt[24] = decrypted[6];
			statpckt[25] = decrypted[7];
			
			//Agi
			statpckt[26] = decrypted[8];
			statpckt[27] = decrypted[9];
			
			//Stat Points
			statpckt[28] = cpleft[0];
			statpckt[29] = cpleft[1];
			
			statpckt[30] = (byte)0x40;
			statpckt[31] = (byte)0x2A;
			
			cur.calculateCharacterStats();
			
		}catch(CharacterException e){
			
			new ServerMessage().execute(e.getMessage(),con);
			throw new PaketException();
			
		}
		
		return statpckt;
	}
	
}
