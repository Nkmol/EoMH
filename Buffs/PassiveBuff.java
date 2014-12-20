package Buffs;

import Player.Character;

public class PassiveBuff extends Buff{
	
	public PassiveBuff(Character owner, short buffId, short buffValue){
		//inf
		super(owner, buffId, 0, buffValue);
	}
	
	@Override
	protected boolean startBuff() throws BuffsException{
		return false;
	}
	
	@Override
	public boolean endBuff(){
		return false;
	}

	
}
