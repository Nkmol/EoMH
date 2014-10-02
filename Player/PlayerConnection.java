package Player;

import java.nio.channels.SocketChannel;

import ServerCore.SelectorThread;


import Connections.Connection;

/**
 * TODO: Utilize this class as special occurance of Connection
 */

public class PlayerConnection extends Connection {
	private Player pl;
	
	
	public PlayerConnection(SocketChannel sc, int rsize, int wsize, SelectorThread sp) {
		super(sc, rsize, wsize, sp);
		// TODO Auto-generated constructor stub
	}

	public Player getPlayer() {
		return this.pl;
	}

	public void setPlayer(Player pl) {
		this.pl = pl;
	}
	
	public Character getActiveCharacter() {
		return this.pl.getActiveCharacter();
	}
	
	public void setActiveCharacter(Character ch) {
		this.pl.setActiveCharacter(ch);
	}
	
	@Override
	public boolean isPlayerConnection() {
		return true;
	}
}
