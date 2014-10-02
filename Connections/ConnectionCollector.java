package Connections;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.nio.channels.*;


public class ConnectionCollector {
	private volatile ConcurrentMap<SocketChannel, Connection> connections;
	
	public static Selector selector;
	
	public ConnectionCollector(int size, float load, int cl) {
		this.connections = new ConcurrentHashMap<SocketChannel, Connection>(size, load, cl);
	}
	
	public void addConnection(SocketChannel sc, int rsize, int wsize) {
		this.connections.putIfAbsent(sc, new Connection(sc, rsize, wsize));
	}
	
	public Connection addAndReturnConnection(SocketChannel sc, int rsize, int wsize) {
		this.connections.putIfAbsent(sc, new Connection(sc, rsize, wsize));
		return this.connections.get(sc);
	}
	
	public synchronized Connection getConnection(SocketChannel sc) {
		//check if dc
		if (sc!=null && this.connections.get(sc)==null){
			System.out.println("ERROR DISCONNECTED.");
		}
		return this.connections.get(sc);
		
	}
	
	public void removeConnection(SocketChannel sc) {
		this.connections.remove(sc);
	}
	
	public int getConnectionCount() {
		return this.connections.size();
	}
		
	public SelectionKey getKeyByChannel(SocketChannel sc) {
		return sc.keyFor(selector);
	}

	public static Selector getSelector() {
		return selector;
	}

	public static void setSelector(Selector select) {
		selector = select;
	}
	
	public boolean isChannelRegistered(SocketChannel ch) {
		return this.connections.containsKey(ch);
	}
	
	public ConcurrentMap<SocketChannel, Connection> getConnectionMap() {
		return this.connections;
	}
}
