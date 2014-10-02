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

public class PartyChangeLeaderPacket implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException {
		System.out.println("Handling PartyLeader");
		
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
				throw new PartyException("Cannot set new leader [player does not exist]");
			//doll mustnt be a new leader
			if(otherGuy.isBot())
				throw new PartyException("Cannot make doll leader");
			//player needs to be in the same pt
			if(otherGuy.getPt()==null || otherGuy.getPt()!=pt)
				throw new PartyException("Cannot set new leader [player is not in this pt]");
			pt.setLeader(otherGuy);
			pt.sendToMembers(PartyPackets.getChangeLeaderPacket(pt, cur, otherGuy),null);
			
		}catch(PartyException e){
			new ServerMessage().execute(e.getMessage(),con);
			throw new PaketException();
		}
		
		return null;
	}
	
}
