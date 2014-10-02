package GameServer.GamePackets;

import java.nio.ByteBuffer;

import Connections.Connection;

public class Ping implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) {
		return new byte[0];
	}
	
}
