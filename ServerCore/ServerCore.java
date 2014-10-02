package ServerCore;


import java.io.IOException;
import java.nio.channels.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Set;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import Connections.Connection;
import Connections.ConnectionCollector;
import PacketHandler.PacketHandler;
import logging.ServerLogger;

public class ServerCore implements Runnable{
	private Selector selector;
	private ServerSocket sSocket; 
	public static boolean running = true;
	private ServerLogger logging;
	
	private int readQsize = 100; //default read/write queue size for each
	private int writeQsize = 100; //new connection. can be adjusted while running(applies to new connections)
	
	private ConnectionCollector con; 
	private PacketHandler phand;
	private InetSocketAddress bindi;
	
	private final int selectorCount = 3;

	//NOTE: using fixed amount of selectors for now - increase pool size for more
	private LinkedBlockingQueue<SelectorThread> selectorPool = new LinkedBlockingQueue<SelectorThread>(this.selectorCount);
	
	public ServerCore(InetSocketAddress bind, ConnectionCollector cons, PacketHandler ph) {	
		this.phand = ph;
		this.bindi = bind;
		this.con = cons;
		this.logging = ServerLogger.getInstance();
		new Thread(this).start();
	}
	
	//method for initializing listening socket
	
	private void init(InetSocketAddress bind, ConnectionCollector con) {		
		try {
			ServerSocketChannel ssChannel = ServerSocketChannel.open(); 
			ssChannel.configureBlocking(false); //set as non-blocking
			this.selector = Selector.open();
			ssChannel.register(this.selector, SelectionKey.OP_ACCEPT); //set ops to accept connections		
			this.sSocket = ssChannel.socket(); //get socket	
			this.sSocket.bind(bind); //bind to ip/port
			this.con = con;
			
			this.logging.logMessage(Level.INFO, this, "New listening socket at port: " + this.sSocket.getLocalPort());
			
		} catch (ClosedChannelException e) {
			this.logging.logMessage(Level.SEVERE, this, e.getMessage());
		} catch (IOException e) {
			this.logging.logMessage(Level.SEVERE, this, e.getMessage());
		}
		for(int i=0;i<this.selectorCount;i++) {
			SelectorThread st;
			try {
				st = new SelectorThread(Selector.open(), 6000, 6000);			
				this.selectorPool.offer(st);
				new Thread(st).start();
			} catch (IOException e) {
				this.logging.severe(this, "Failed to open selector (I/O Exception) - selector number: " + i);
				System.exit(0);
			}
		}
		this.makeItSo();
	}
	
	//captain Jean Luc Picard
	private void makeItSo() {
		int selected = 0;
		while(running) {
			try {
				selected = this.selector.select(100); 
			} catch (IOException e) {
				this.logging.logMessage(Level.SEVERE, this, e.getMessage());
			}
			if(selected > 0) {
				Set<SelectionKey> readySet = this.selector.selectedKeys();
				Iterator<SelectionKey> keySetIter = readySet.iterator();
				SelectorThread sp = this.selectorPool.poll();
				//Iterate all selected keys
				while(keySetIter.hasNext()) {
					SelectionKey key = keySetIter.next();				
					//always a good idea to check validity of dem keys when dealin' with em
					//a key is valid if it's channel is open and the key's not canceled
					if(key.isValid()) {					
						//channel is ready to accept new connection
						if(key.isAcceptable()) {
							try {
								ServerSocketChannel server = (ServerSocketChannel)key.channel();
								SocketChannel client = server.accept();	
								client.socket().setTcpNoDelay(true); //set no delay flag
								client.configureBlocking(false); //set as non-blocking
								Connection tmpc = this.con.addAndReturnConnection(client, this.readQsize, this.writeQsize);								
								tmpc.registerSelectors(sp); //register selectors - i find it safer to do after instantiating Connection
								if(client.isConnected() && client.isOpen()) {
									this.phand.newConnection(tmpc);									
									this.logging.logMessage(Level.INFO, this, "New connection: " + client);
								} else {
									this.con.removeConnection(client);
								}
							} catch (ClosedChannelException e) {
								this.logging.logMessage(Level.WARNING, this, e.getMessage());
							} catch (IOException e) {
								this.logging.logMessage(Level.WARNING, this, e.getMessage());
							}
						}	
					}				
					keySetIter.remove();				
				}
				this.selectorPool.offer(sp);
			}
		}
	}
	
	//set as false to exit main loop
	public static void setRunning(boolean val) {
		running = val;
	}

	public Selector getSelector() {
		return selector;
	}

	public ServerSocket getsSocket() {
		return sSocket;
	}

	public static boolean isRunning() {
		return running;
	}

	public int getReadQsize() {
		return readQsize;
	}

	public void setReadQsize(int readQsize) {
		this.readQsize = readQsize;
	}

	public int getWriteQsize() {
		return writeQsize;
	}

	public void setWriteQsize(int writeQsize) {
		this.writeQsize = writeQsize;
	}

	public PacketHandler getPacketHandler() {
		return phand;
	}

	public void setPacketHandler(PacketHandler phand) {
		this.phand = phand;
	}

	@Override
	public void run() {
		this.init(bindi, con);
	}


}


