package Buffs.BuffActions;

import Buffs.BuffAction;
import Mob.Mob;
import Player.Character;
import Player.Fightable;
import ServerCore.ServerFacade;
import Tools.BitTools;
import World.OutOfGridException;

public class BuffAction47 implements BuffAction {
	
	short buffId;
	int casteruid;
	
	public BuffAction47(short buffId){
		this.buffId=buffId;
	}

	//increase Final Damage
	public void startBuff(Character ch,short value) {
		//no action except for updating character stats
		ch.calculateCharacterStats();
	}

	public void endBuff(Character ch,short value) {
		//no action except for updating character stats
		ch.calculateCharacterStats();
	}
	
	public short getId(){
		return buffId;
	}
	
	public String getValueType(){
		return "DoT";
	}
	
	public boolean updateOverTime(Fightable owner, short value){
		if(owner.isAlive()) {
			System.out.println("update buff on " + owner.getName() + " with value " + value);
			try {
				owner.recDamage(casteruid, Math.abs(value));
			} catch (OutOfGridException e) {
				System.out.print(e.getMessage());
			}
			owner.refreshHpMpSp();
			return true;
		}
		else
		{
			System.out.println("Owner died, stop buff");
			return false;
		}
	}

	@Override
	public void setCasterId(int uid) {
		casteruid = uid;
	}
}
