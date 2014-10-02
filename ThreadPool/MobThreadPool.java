package ThreadPool;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class MobThreadPool {

		private ExecutorService poolEx; 
		
		public MobThreadPool(int nt) {
			this.poolEx = Executors.newFixedThreadPool(nt, new PoolFactory("Mob task dispatcher. Thread"));
		}
		
		public void executeProcess(Runnable run) {
			this.poolEx.execute(run);
		}
		
		
}

