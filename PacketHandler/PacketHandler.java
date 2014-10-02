package PacketHandler;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import Connections.Connection;
import ServerCore.ServerFacade;

public interface PacketHandler {
	void initialize(ServerFacade sf); //pass on reference to server interface
	void processPacket(ByteBuffer buf, SocketChannel chan); //pass on packet by channel for post processing
	void processPacket(ByteBuffer buf, Connection con); //pass on packet by Connection object for post processing
	void processList(Connection con); //process the entire packet queue of a connection
	ByteBuffer processPacket(ByteBuffer boss); //return processed bytebuffer
	void newConnection(SocketChannel chan); //invoked each time new connection is established
	void newConnection(Connection con); //invoked each time new connection is established
}
