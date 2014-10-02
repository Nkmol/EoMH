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

public class PartyLeavePacket implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling PartyLeave");
		
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
			if(pt==null)
				throw new PartyException("Cannot handle pt leave/ban [you are not in a party]");
			
			//otherGuy is bot
			if(otherGuy!=null && otherGuy.isBot()){
				
				if(otherGuy.getPt()!=pt)
					throw new PartyException("Cannot handle pt ban [player is not in this party]");
				
				//ban
				if(decrypted[0]==(byte)0x01){
					if(cur!=pt.getLeader())
						throw new PartyException("Cannot handle pt ban [you are not the leader]");
					pt.leaveParty(otherGuy);
					cur.addWritePacketWithId(PartyPackets.getBanPacket(pt, cur, otherGuy));
					pt.sendToMembers(PartyPackets.getLeavePacket(otherGuy, pt),null);
					return null;
				}
				
			//otherGuy is player
			}else{
				
				//ban
				if(decrypted[0]==(byte)0x01){
					if(otherGuy==null)
						throw new PartyException("Cannot handle pt ban [player does not exist]");
					if(otherGuy.getPt()!=pt)
						throw new PartyException("Cannot handle pt ban [player is not in this party]");
					if(cur!=pt.getLeader())
						throw new PartyException("Cannot handle pt ban [you are not the leader]");
					pt.leaveParty(otherGuy);
					cur.addWritePacketWithId(PartyPackets.getBanPacket(pt, cur, otherGuy));
					otherGuy.addWritePacketWithId(PartyPackets.getBanPacket(pt, cur, otherGuy));
					return null;
				}
				
				//leave
				if(decrypted[0]==(byte)0x00){
					cur.leavePt();
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
