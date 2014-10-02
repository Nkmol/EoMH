package GameServer.ServerPackets;

import Connections.Connection;

public class ServerMessage implements ServerPaket{

	public void execute(String text, Connection con){
		
		System.out.println("Server Message");
		byte[] msg=getChatPacket(text,(byte)6);
		con.addWrite(msg);
		
	}
	
	public void execute(String text, Connection con, byte type){
		
		System.out.println("Server Message");
		byte[] msg=getChatPacket(text,type);
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
	
	private byte[] getChatPacket(String text, byte type){
		
		char[] ctext = text.toCharArray();
		byte[] btext = new byte[ctext.length];
		
		for(int i=0;i<ctext.length;i++)
			btext[i]=(byte)ctext[i];
		
		byte[] msg = new byte[45+btext.length];
		
		msg[0] = (byte)(45+btext.length);
		msg[4] = (byte)0x05;
		msg[6] = (byte)0x07;
		msg[8] = (byte)0x01;
		msg[17] = (byte)0x01;
		//shout chat color
		msg[18] = type;
		
		//name [Server]
		msg[20] = (byte)0x5b;
		msg[21] = (byte)0x53;
		msg[22] = (byte)0x65;
		msg[23] = (byte)0x72;
		msg[24] = (byte)0x76;
		msg[25] = (byte)0x65;
		msg[26] = (byte)0x72;
		msg[27] = (byte)0x5d;
		
		msg[40] = (byte)btext.length;
		
		//text
		for(int i=0;i<btext.length;i++)
			msg[44+i]=btext[i];
		
		msg[btext.length+44] = (byte)0x00;
		
		return msg;
		
	}
	
}
