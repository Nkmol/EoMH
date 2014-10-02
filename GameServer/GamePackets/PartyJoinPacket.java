package GameServer.GamePackets;

import java.nio.ByteBuffer;

import Connections.Connection;
import Encryption.Decryptor;
import GameServer.ServerPackets.ServerMessage;
import Player.Character;
import Player.PlayerConnection;
import Tools.BitTools;
import World.WMap;
import Parties.Party;
import Parties.PartyException;
import Parties.PartyPackets;

public class PartyJoinPacket implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling PartyJoin");
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		Character cur = ((PlayerConnection)con).getActiveCharacter();
		Party pt=cur.getPt();
		byte cid2[]={decrypted[4],decrypted[5],decrypted[6],decrypted[7]};
		Character otherGuy=WMap.getInstance().getCharacter(BitTools.byteArrayToInt(cid2));
		if(otherGuy==cur)
			otherGuy=null;
		
		try{
			//otherGuy must exist
			if(otherGuy==null)
				throw new PartyException("Cannot handle pt [other player does not exist]");
			
			//otherGuy is bot
			if(otherGuy.isBot()){
			
				//ask for pt
				if(decrypted[0]==(byte)0x02){
					//asking guy mustnt be in another pt
					if(otherGuy.getPt()!=null)
						throw new PartyException("Cannot inv into pt [other player is in party]");
					//can the asking person inv?
					if(pt!=null && cur!=pt.getLeader())
						throw new PartyException("Cannot inv into pt [you are not the leader]");
					//is the pt not full
					if(pt!=null && pt.getMemberAmount()==8)
						throw new PartyException("Cannot inv into pt [party is full]");
					//new pt
					if(pt==null){
						pt=new Party(cur,otherGuy);
						cur.setPt(pt);
					}else{
						pt.addMember(otherGuy);
					}
					otherGuy.setPt(pt);
					pt.sendToMembers(PartyPackets.getAcceptPtPacket(pt, otherGuy),null);
					pt.refreshFullPt();
					//PartyPackets.sendRefreshPtPacket(pt);
					return null;
				}
				
				//no answer
				cur.addWritePacketWithId(PartyPackets.getRefusePtPacket(cur, otherGuy));
				throw new PartyException("Cannot handle pt [no response]");
		
			//otherGuy is player
			}else{
				//lost connection
				if(otherGuy.GetChannel()==null)
					throw new PartyException("Cannot handle pt [other player is not connected]");
				
				if(decrypted[0]==(byte)0x02){
					//asking guy mustnt be in another pt
					if(otherGuy.getPt()!=null)
						throw new PartyException("Cannot ask for pt [other player is in party]");
					//can the asking person inv?
					if(pt!=null && cur!=pt.getLeader())
						throw new PartyException("Cannot ask for pt [you are not the leader]");
					//is the pt not full
					if(pt!=null && pt.getMemberAmount()==8)
						throw new PartyException("Cannot ask for pt [party is full]");
					otherGuy.addWritePacketWithId(PartyPackets.getAskForPtPacket(cur, otherGuy));
					cur.addWritePacketWithId(PartyPackets.getAskForPtPacket(cur, otherGuy));
					return null;
				}
				
				if(decrypted[0]==(byte)0x01){
					//asking guy mustnt be in another pt
					if(pt!=null)
						throw new PartyException("Cannot accept pt [other player is in party]");
					//is the pt not full
					if(otherGuy.getPt()!=null && otherGuy.getPt().getMemberAmount()==8)
						throw new PartyException("Cannot accept pt [party is full]");
					//new pt
					if(otherGuy.getPt()==null){
						pt=new Party(otherGuy,cur);
						otherGuy.setPt(pt);
					}else{
						pt=otherGuy.getPt();
						pt.addMember(cur);
					}
					cur.setPt(pt);
					pt.sendToMembers(PartyPackets.getAcceptPtPacket(pt, cur),null);
					pt.refreshFullPt();
					//PartyPackets.sendRefreshPtPacket(pt);
					return null;
				}

				if(decrypted[0]==(byte)0x00){
					//cant refuse pt when already in pt
					if(pt!=null)
						throw new PartyException("Wrong packet [party refuse request]");
					otherGuy.addWritePacketWithId(PartyPackets.getRefusePtPacket(cur, otherGuy));
					cur.addWritePacketWithId(PartyPackets.getRefusePtPacket(cur, otherGuy));
					return null;
				}
			
			}
		}catch(PartyException e){
			new ServerMessage().execute(e.getMessage(),con);
			throw new PaketException();
		}
		
		return null;
	}
	
}
