package Buffs;

public class ItemBuff extends Buff{
	
	public ItemBuff(Character owner, int buffId, long buffLength, int buffValue){
		super(owner, buffId, buffLength, buffValue);
		startBuff();
	}
	
	@Override
	protected boolean startBuff(){
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
	
	@Override
	public long getTimeLeft(){
		return super.getTimeLeft();
	}
	
	@Override
	public Character getOwner(){
		return super.getOwner();
	}

}
