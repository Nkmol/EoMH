package ServerCore;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import Lobby.ConnectionDispatcher;
import Lobby.Lobby;
import PacketHandler.PacketHandler;
import ThreadPool.PacketHandlerPool;
import Configuration.Configuration;
import Configuration.ConfigurationManager;
import Connections.Connection;
import Connections.ConnectionCollector;
import GameServer.Gameserver;


/**
 * This class acts as facade between essential server core modules.
 * Use this as an interface for your convenience!
 */

public class ServerFacade {
	private ConnectionCollector con;
	private PacketHandlerPool ppool; 
	private ServerCore sc;
	private PacketHandler pckth;
	private InetSocketAddress isa;
	private static ServerFacade instance;
	
	/*
	 * Will start a listening socket with given thread pool size and initial concurrent connections capacity
	 * int threadCount = thread pool size
	 * int initCap = initial connection capacity 
	 */
	
	private ServerFacade(InetSocketAddress isa, int threadCount, int initCap, PacketHandler p) {
		instance = this;
		this.con = new ConnectionCollector(initCap, 50, threadCount);
		this.pckth = p;
		this.isa = isa; 
		this.pckth.initialize(this);
		this.sc = new ServerCore(this.isa, this.con, this.pckth);
	}
	
	public static synchronized ServerFacade getInstance(){
		if (instance == null){
			String t = ConfigurationManager.getProcessName();
			if (t.contentEquals("GameServer")){
				Configuration conf = ConfigurationManager.getConf("GameServer");
				instance = new ServerFacade(new InetSocketAddress(conf.getIntVar("port")), 100, 300, new Gameserver());
				PacketHandlerPool.getInstance().initialize(300);
			}
			else if (t.contentEquals("Lobby")){
				Configuration conf = ConfigurationManager.getConf("Lobby");
				instance = new ServerFacade(new InetSocketAddress(conf.getIntVar("lobbyPort")), 100, 300,new Lobby());
				PacketHandlerPool.getInstance().initialize(10);
			}else {
				Configuration conf = ConfigurationManager.getConf("Lobby");
				instance = new ServerFacade(new InetSocketAddress(conf.getIntVar("cdpPort")), 100, 300, new ConnectionDispatcher());
				PacketHandlerPool.getInstance().initialize(10);
			}
		}
		return instance;
	}
		
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public int getConnectionCount() {
		return this.con.getConnectionCount();
	}
	
	public void stopServer() {
		ServerCore.setRunning(false);
	}
		
	public PacketHandlerPool getPpool() {
		return ppool;
	}
		
	public void finalizeConnection(SocketChannel chan) {
		if(this.con.isChannelRegistered(chan)) {
			Connection tmp = this.con.getConnection(chan);
			tmp.threadSafeDisconnect();
		}
	}
	
	public Selector getSelector() {
		return this.sc.getSelector();
	}
	
	public ServerSocket getServerSocket() {
		return this.sc.getsSocket();
	}
	
	public LinkedBlockingQueue<byte[]> getConnectionReadQueue(SocketChannel chan) {
		return this.con.getConnection(chan).getReadBuffer();
	}
	
	public LinkedBlockingQueue<byte[]> getConnectionWriteQueue(SocketChannel chan) {
		return this.con.getConnection(chan).getWriteBuffer();
	}
	
	public int getReadQueueSize() {
		return this.sc.getReadQsize();
	}
	
	public int getWriteQueueSize() {
		return this.sc.getWriteQsize();
	}
	
	public void setReadQueueSize(int s) {
		this.sc.setReadQsize(s);
	}
	
	public void setWriteQueueSize(int s) {
		this.sc.setWriteQsize(s);
	}

	public PacketHandler getPckth() {
		return pckth;
	}

	public void setPckth(PacketHandler pckth) {
		this.pckth = pckth;
	}
	
	public Connection getConnectionByChannel(SocketChannel sc) {
		return this.con.getConnection(sc);
	}
	
	public ConcurrentMap<SocketChannel, Connection> getConnections() {
		return this.con.getConnectionMap();
	}
	
	public void addWriteByChannel(SocketChannel sc, byte[] bb) {
		if(sc!=null){
			Connection tmp=this.con.getConnection(sc);
			if(tmp!=null)
				tmp.addWrite(bb);
		}
	}
	
	public ConnectionCollector getConnectionCollector() {
		return con;
	}
	
	public void addConnection(SocketChannel cn, int rs, int ws, SelectorThread st) {
		if(cn!=null)
			this.con.addConnection(cn, rs, ws);
	}
	
	public void removeConnection(SocketChannel cn) {
		this.con.removeConnection(cn);
	}
	
	public void flipInterestOpsByChannel(SocketChannel cn, int ops) {
		if(this.con.isChannelRegistered(cn)) {
			this.con.getConnection(cn).flipOps(ops);
		}
	}
	
}


