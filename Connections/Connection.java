package Connections;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

import Queue.PacketQueue;
import ServerCore.SelectorThread;


public class Connection {
	private SocketChannel chan;
	private String ip;
	private PacketQueue pcktq;
	private volatile SelectorThread sepair;
	public static final int maxWriteTime = 70; //at most write for maxWriteTime(ms) sequantially before performing a read
	private long writeStamp = 0; //timestamp for timing write
	
	public Connection(SocketChannel sc, int rsize, int wsize) {
		this.chan = sc;
		this.ip = sc.socket().getInetAddress().getHostAddress();
		this.pcktq = new PacketQueue(rsize, wsize);
	}
	
	public Connection(SocketChannel sc, int rsize, int wsize, SelectorThread sp) {
		this.chan = sc;
		this.ip = sc.socket().getInetAddress().getHostAddress();
		this.pcktq = new PacketQueue(rsize, wsize);
		this.sepair = sp;
	}

	public SocketChannel getChan() {
		return chan;
	}

	public String getIp() {
		return ip;
	}
	
	public LinkedBlockingQueue<byte[]> getReadBuffer() {
		return this.pcktq.getReadBuffer();
	}
	
	public LinkedBlockingQueue<byte[]> getWriteBuffer() {
		return this.pcktq.getWriteBuffer();
	}
	
	public void addRead(byte[] bboss) {
		this.pcktq.getReadBuffer().offer(bboss);
	}
	
	public synchronized void registerSelectors(SelectorThread select) {
		this.sepair = select;
		select.register(this.chan, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
	}
	
	public synchronized SelectorThread getRegisteredSelector() {
		return this.sepair;
	}
	
	public void flipOps(int ops) {
		if(this.chan.isConnected() && this.chan.isOpen() && this.chan.isRegistered()) {
			this.sepair.setOps(this.chan, ops);
		}
	}
	
	public synchronized void unregister() {
		if(this.isRegistered()) {
			this.sepair.unregisterChannel(this.chan);
			this.sepair = null;
		}
	}
		
	public boolean disconnect() {
		try {
			this.chan.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean isRegistered() {
		if(this.sepair != null && this.chan.isRegistered()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void addWrite(byte[] bboss) {
		if(this.chan.isRegistered()) {
			this.pcktq.getWriteBuffer().offer(bboss);
			this.flipOps(SelectionKey.OP_WRITE);
		}
	}
	
	public void addWriteButDoNotFlipInterestOps(byte[] bboss) {
		if(this.chan.isRegistered()) {
			this.pcktq.getWriteBuffer().offer(bboss);
		}
	}
	
	public synchronized void setSelectorPair(SelectorThread sp) {
		this.sepair = sp;
	}
	
	public synchronized void threadSafeDisconnect() {
		this.sepair.registerForFinalization(this);
	}
	
	public boolean isPlayerConnection() {
		return false;
	}

	public long getWriteStamp() {
		return writeStamp;
	}

	public void setWriteStamp(long writeStamp) {
		this.writeStamp = writeStamp;
	}
}
