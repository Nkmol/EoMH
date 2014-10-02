package World;

import java.util.ArrayList;
import java.util.List;

public class WaypointChain {
	List<Waypoint> chain = new ArrayList<Waypoint>();
	
	public Waypoint pop() {
		return this.chain.remove(0);
	}
	
	public void push(Waypoint wp) {
		this.chain.add(wp);
	}

	public void clearAll() {
		this.chain.clear();
	}
	
	public boolean isEmpty() {
		return this.chain.isEmpty();
	}
	
	public void populate(List<Waypoint> wps) {
		this.chain.addAll(wps);
	}
}
