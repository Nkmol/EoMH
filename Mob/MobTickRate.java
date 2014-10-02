package Mob;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MobTickRate {
	private List<List<Mob>> mobSlots = new ArrayList<List<Mob>>();
	private int slot = 0;
	
	public MobTickRate(int count) {
		for(int i=0;i<count;i++) {
			this.mobSlots.add(new ArrayList<Mob>());
		}
	}
	
	public void addMob(Mob m) {
		this.mobSlots.get(this.next()).add(m);
	}
	
	public void deleteMob(Mob m){
		for(Iterator<List<Mob>> i=mobSlots.iterator();i.hasNext();){
			List<Mob> mobs=i.next();
			mobs.remove(m);
		}
	}
	
	public List<Mob> getNextMobs() {
		return this.mobSlots.get(this.next());
	}
	
	private int next() {
		if(this.slot < this.mobSlots.size() -1 ) {
			this.slot++;
		} else {
			this.slot = 0;
		}
		return this.slot;
	}
}
