package Lobby;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import Connections.Connection;
import PacketHandler.PacketHandler;
import ServerCore.ServerFacade;

public class Lobby implements PacketHandler {
	private ServerFacade sface;
	
	public static final byte serverID = 0x02; //don't ask why 2
	
	public static final byte serverIsAvailable = 0x01;
	public static final byte serverIsBusy = 0x02;
	public static final byte serverIsVeryBusy = 0x03;
	public static final byte holyShit = 0x04;
	
	private byte[] lobbyPacket;
	
	public void processPacket(ByteBuffer buf, SocketChannel chan) {
		// NOP		
	}

	/*
	 * Upon new connection send em packet telling there's one server online
	 */
	public void newConnection(SocketChannel chan) {
		this.sface.getConnectionByChannel(chan).addWrite(this.lobbyPacket);
	}


	public void initialize(ServerFacade sf) {
		this.sface = sf;
		
		this.lobbyPacket = new byte[16];
		
		for(int i=0;i<this.lobbyPacket.length;i++) {
			this.lobbyPacket[i] = 0x00;
		}
		 
		this.lobbyPacket[0] = (byte)this.lobbyPacket.length; //packet length
		this.lobbyPacket[4] = 0x01; //amount of servers
		this.lobbyPacket[8] = serverID; //server names are hard coded in client itself. they are distinctable by this byte 
		this.lobbyPacket[12] = serverIsAvailable; //server status
	}

	
	public void newConnection(Connection con) {
		con.addWrite(this.lobbyPacket);
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
