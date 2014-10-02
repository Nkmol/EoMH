package GameServer.GamePackets;

import java.nio.ByteBuffer;

import Connections.Connection;
import Duel.Duel;
import Duel.DuelException;
import Duel.DuelPackets;
import Encryption.Decryptor;
import GameServer.ServerPackets.ServerMessage;
import Player.Character;
import Player.PlayerConnection;
import Tools.BitTools;
import World.WMap;

public class StartDuelPacket implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling Duel");
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		Duel duel=cur.getDuel();
		byte cid2[]={decrypted[4],decrypted[5],decrypted[6],decrypted[7]};
		Character otherGuy=WMap.getInstance().getCharacter(BitTools.byteArrayToInt(cid2));
		if(otherGuy==cur)
			otherGuy=null;
		
		try{
			if(duel!=null)
				throw new DuelException("Cannot ask for duel [you are in a duel]");
			
			//otherGuy must exist
			if(otherGuy==null)
				throw new DuelException("Cannot handle duel [other player does not exist]");
			
			//otherGuy is bot
			if(otherGuy.isBot()){
			
				//ask for duel
				if(decrypted[0]==(byte)0x02){
					//asking guy mustnt be in another duel
					if(otherGuy.getDuel()!=null)
						throw new DuelException("Cannot ask for duel [other player is in duel]");
					//new duel
					duel=new Duel(cur,otherGuy);
					cur.setDuel(duel);
					otherGuy.setDuel(duel);
					cur.addWritePacketWithId(DuelPackets.getStartDuelPacket(cur, otherGuy));
					return null;
				}
				
				//no answer
				cur.addWritePacketWithId(DuelPackets.getRefuseDuelPacket(cur, otherGuy));
				throw new DuelException("Cannot handle duel [no response]");
		
			//otherGuy is player
			}else{
				//lost connection
				if(otherGuy.GetChannel()==null)
					throw new DuelException("Cannot handle duel [other player is not connected]");
				
				if(decrypted[0]==(byte)0x02){
					//asking guy mustnt be in another duel
					if(otherGuy.getDuel()!=null)
						throw new DuelException("Cannot ask for duel [other player is in duel]");
					otherGuy.addWritePacketWithId(DuelPackets.getAskDuelPacket(cur, otherGuy));
					return null;
				}
				
				if(decrypted[0]==(byte)0x01){
					//new duel
					duel=new Duel(otherGuy,cur);
					otherGuy.setDuel(duel);
					cur.setDuel(duel);
					otherGuy.addWritePacketWithId(DuelPackets.getAcceptDuelPacket(otherGuy, cur));
					cur.addWritePacketWithId(DuelPackets.getAcceptDuelPacket(otherGuy, cur));
					otherGuy.addWritePacketWithId(DuelPackets.getStartDuelPacket(cur, cur));
					cur.addWritePacketWithId(DuelPackets.getStartDuelPacket(cur, otherGuy));
					return null;
				}

				if(decrypted[0]==(byte)0x00){
					//refuse duel
					otherGuy.addWritePacketWithId(DuelPackets.getRefuseDuelPacket(otherGuy, cur));
					cur.addWritePacketWithId(DuelPackets.getRefuseDuelPacket(otherGuy, cur));
					return null;
				}
			
			}
		}catch(DuelException e){
			new ServerMessage().execute(e.getMessage(),con);
			throw new PaketException();
		}
		
		return null;
	}
	
}
