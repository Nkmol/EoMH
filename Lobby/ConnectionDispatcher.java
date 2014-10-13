package Lobby;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import Connections.Connection;
import PacketHandler.PacketHandler;
import ServerCore.ServerFacade;


/*
 * Once you select a server from server list the client will next connect to port 10002
 * which in turn will return game server ip and port to connect to
 */

public class ConnectionDispatcher implements PacketHandler {
	private final String ip = "25.181.178.166";
	private final String port = "11000";

	private byte[] pckt;
	
	public void initialize(ServerFacade sf) {		
		byte[] ip = this.ip.getBytes();
		byte[] port = this.port.getBytes();
		this.pckt = new byte[ip.length+port.length+13];
		
		for(int i=0;i<this.pckt.length;i++) {
			this.pckt[i] = (byte)0x00; //fill the packet with zeros first
		}
		
		this.pckt[0] = (byte)this.pckt.length; //packet length
		this.pckt[4] = (byte)0x17; //packet header
		
		for(int i=0;i<ip.length;i++) {
			this.pckt[i+8] = ip[i]; //add ip in the packet
		}
		
		this.pckt[(8+ip.length)] = (byte)0x20; //add space between ip and port
		
		for(int i=0;i<port.length;i++) {
			this.pckt[(i+8+ip.length+1)] = port[i]; //add port in the packet
		}
	}

	public void processPacket(ByteBuffer buf, SocketChannel chan) {
		// NOP
		
	}

	public void newConnection(SocketChannel chan) {

	}


	public void newConnection(Connection con) {
		con.addWrite(this.pckt);
		while(!con.getWriteBuffer().isEmpty()) { try { Thread.sleep(5); } catch (InterruptedException e) {} }
		con.threadSafeDisconnect();
	}

	@Override
	public ByteBuffer processPacket(ByteBuffer boss) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processPacket(ByteBuffer buf, Connection con) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processList(Connection con) {
		// TODO Auto-generated method stub
		
	}
	
	
}
