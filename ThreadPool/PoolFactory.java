package ThreadPool;

import java.util.concurrent.ThreadFactory;

public class PoolFactory implements ThreadFactory {
	private String threadName;
	private int count = 0;
	
	public PoolFactory(String name) {
		this.threadName = name;
	}

	@Override
	public Thread newThread(Runnable arg0) {
		this.count++;
		return new Thread(arg0, this.threadName + " [" + this.count + "]");
	}

}
