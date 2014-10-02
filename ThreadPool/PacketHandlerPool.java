package ThreadPool;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import logging.ServerLogger;


public class PacketHandlerPool {
	private ExecutorService poolEx; 
	private static volatile PacketHandlerPool instance = null;
	private ServerLogger logging = ServerLogger.getInstance();
	
	private PacketHandlerPool() {
		instance = this;
	}
		
	public static synchronized PacketHandlerPool getInstance() {
		return (instance == null ) ? new PacketHandlerPool() : instance; 
	}
	
	public void executeProcess(Runnable run) {
		if(this.poolEx == null) { this.logging.severe(this, "[!] Thread pool has not been initialized [!]"); System.exit(0); }
		this.poolEx.execute(run);
	}
	
	public void initialize(int n) {
		this.poolEx = Executors.newFixedThreadPool(n, new PoolFactory("Packet handling dispatcher - Thread"));
		this.logging.info(this, "Initialized new thread pool with size: " + n);
	}
}
