package GameServer.GamePackets;

import java.nio.ByteBuffer;

import Connections.Connection;

public interface Packet {
	void execute(ByteBuffer buff);
	byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) throws PaketException;
}
