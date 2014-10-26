package Buffs;

import Player.Character;

public class ItemBuff extends Buff{
	
	public ItemBuff(Character owner, short buffId, long buffLength, short buffValue){
		super(owner, buffId, buffLength, buffValue);
	}
	
	@Override
	protected boolean startBuff() throws BuffsException{
		if(super.startBuff()){
			//do item buff start stuff
			
			return true;
		}
		return false;
	}
	
	@Override
	protected boolean endBuff(){
		if(super.endBuff()){
			//do item buff end stuff
			
			return true;
		}
		return false;
	}
	
}
