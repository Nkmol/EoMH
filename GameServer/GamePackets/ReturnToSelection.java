package GameServer.GamePackets;

import java.nio.ByteBuffer;



import Connections.Connection;
import Player.CharacterMaster;

public class ReturnToSelection implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) {
		return CharacterMaster.backToSelection(con);
	}

}
