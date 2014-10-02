package Queue;

import java.util.concurrent.LinkedBlockingQueue;

public class PacketQueue {
	
	private LinkedBlockingQueue<byte[]> readBuffer; 
	private LinkedBlockingQueue<byte[]> writeBuffer;
	
	public PacketQueue(int rsize, int wsize) {
		this.readBuffer = new LinkedBlockingQueue<byte[]>(rsize);
		this.writeBuffer = new LinkedBlockingQueue<byte[]>(wsize);
	}

	public LinkedBlockingQueue<byte[]> getReadBuffer() {
		return readBuffer;
	}

	public LinkedBlockingQueue<byte[]> getWriteBuffer() {
		return writeBuffer;
	}
	

}
