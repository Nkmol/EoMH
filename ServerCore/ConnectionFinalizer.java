package ServerCore;

import java.util.concurrent.LinkedBlockingQueue;

import Connections.Connection;
import Player.Character;
import Player.PlayerConnection;

public class ConnectionFinalizer {
	private LinkedBlockingQueue<Connection> finaList = new LinkedBlockingQueue<Connection>();
	private ServerFacade sf = ServerFacade.getInstance();
	private static ConnectionFinalizer instance = null;
	
	private ConnectionFinalizer() {
		instance = this;
	}
	
	public static synchronized ConnectionFinalizer getInstance() {
		return (instance == null ) ? new ConnectionFinalizer() : instance;
	}
	
	public void addFinalizableConnection(Connection con) {
			this.finaList.offer(con);
	}
	
	public boolean isWaitingFinalization(Connection con) {
		return this.finaList.contains(con);
	}
	
	public LinkedBlockingQueue<Connection> getFinalizeList() {
		return this.finaList;
	}
	
	public void finalize() {
		Connection tmp;
		PlayerConnection tmplc;
		while(!this.finaList.isEmpty()) {
			tmp = this.finaList.poll();
			if(tmp!=null){
				//In case we had un-clean dc while in game, make sure we leave the gameworld
				if(tmp.isPlayerConnection()){
					tmplc=(PlayerConnection)tmp;
					Character ch = ((PlayerConnection)tmplc).getActiveCharacter();
					if(ch != null) {
						ch.leaveGameWorld(true);
						((PlayerConnection)tmplc).getPlayer().setActiveCharacter(null); //set active character to null
					}
					tmplc.getWriteBuffer().clear();
				}
				this.sf.removeConnection(tmp.getChan());
				tmp.disconnect();
				//In case we had un-clean dc while in game, make sure we leave the gameworld
				//if(tmp.isPlayerConnection()) {
				//	tmplc = (PlayerConnection) tmp;
				//	if(tmplc.getPlayer().hasActiveCharacter()) {
				//		tmplc.getActiveCharacter().leaveGameWorld();
				//	}
				//}
			}
		}
	}
	
	public boolean isEmpty() {
		return this.finaList.isEmpty();
	}
}
