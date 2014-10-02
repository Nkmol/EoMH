package timer;

import java.util.TimerTask;

import Player.Character;

public class VendingPointTimer extends TimerTask {
private Character owner;
	
	public VendingPointTimer(Character owner){
		
		this.owner=owner;
		
	}
	
	public void run(){
		
		owner.getInventory().VendingPointsTimer(owner);
		
	}
}
