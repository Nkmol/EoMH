package timer;

import java.util.TimerTask;
import Player.Character;

public class HealingTimer extends TimerTask{

	private Character owner;
	
	public HealingTimer(Character owner){
		
		this.owner=owner;
		
	}
	
	public void run(){
		
		owner.autoHeal();
		
	}
	
}
