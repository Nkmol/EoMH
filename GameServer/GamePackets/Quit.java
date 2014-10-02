package GameServer.GamePackets;

import java.nio.ByteBuffer;

import Connections.Connection;
import Player.Character;
import Player.PlayerConnection;


public class Quit implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}


	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) {
        Character ch = ((PlayerConnection)con).getActiveCharacter();
        if(ch != null) {
        	ch.leaveGameWorld(true);
        	((PlayerConnection)con).getPlayer().setActiveCharacter(null); //set active character to null
        }
        con.getWriteBuffer().clear();
		final byte[] quit = new byte[] {(byte)0x09, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x64, (byte)0x00, (byte)0x00};
		return quit;
	}
	
}
