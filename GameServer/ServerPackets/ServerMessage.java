package GameServer.ServerPackets;

import Connections.Connection;
import Player.ChatMaster;

public class ServerMessage implements ServerPaket{

	public void execute(String text, Connection con){
		
		System.out.println("Server Message");
		byte[] msg=ChatMaster.getChatPacket(-1,"[Server]", text, (byte)6);
		con.addWrite(msg);
		
	}
	
	public void execute(String text, Connection con, byte type){
		
		System.out.println("Server Message");
		byte[] msg=ChatMaster.getChatPacket(-1,"[Server]", text, type);
		con.addWrite(msg);
		
	}
	
	public void executeAnnounce(String text, Connection con){
		
		System.out.println("Server Message");

		byte[] gmsg = new byte[14+text.length()];
		byte[] msg = text.getBytes();
		
		gmsg[0] = (byte)gmsg.length;
		gmsg[4] = (byte)0x03;
		gmsg[6] = (byte)0x50;
		gmsg[7] = (byte)0xC3;
		gmsg[8] = (byte)0x01;
		gmsg[9] = (byte)0x23;
		
		for(int i=0;i<msg.length;i++) {
			gmsg[i+13] = msg[i];
		}
		
		con.addWrite(gmsg);
		
	}
	
}
