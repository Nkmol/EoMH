package timer;

import java.util.TimerTask;

import Player.Dolls.Doll;

public class PotsAndDmgTimer extends TimerTask{

	private Doll owner;
	
	public PotsAndDmgTimer(Doll owner, short hp, short mana){
		
		this.owner=owner;
		
	}
	
	public void run(){
		
		if(!owner.getDoll().isDead()){
			short value=owner.getHpManaPotValueByLvl();
			if(owner.healHp(value)==false){
				owner.healMana(value);
			}
		}
		
	}
	
}
