package Buffs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Database.ItemDAO;
import Player.Fightable;
import Buffs.BuffActions.*;

public class BuffMaster {
	
	private static Map<Short, BuffAction> buffActions;
	
	public static void getAllBuffs() {
		buffActions = new HashMap<Short, BuffAction>();
		//get buff action ids from db and create BuffAction objects and load them to buffActions
		List<Short> itembuffs = ItemDAO.getInstance().getItemBuffs();
		List<Short> skillbuffs = ItemDAO.getInstance().getSkillBuffs();
		List<Short> finalbuffs = new ArrayList<Short>();
		//Combine them (can have double values)
		finalbuffs.addAll(itembuffs);
		finalbuffs.addAll(skillbuffs);
		Collections.sort(finalbuffs);
        
		for(int i=0;i<finalbuffs.size();i++) {
			short buffId=finalbuffs.get(i);
			if(!buffActions.containsKey(buffId)) { //? There are never double values taken from db (UNION)
				BuffAction buff;
				//switch is ok, we change the buffclasses names later :P
				switch(buffId){
					case 2:
					case 7:{
						buff=new MaxHp(buffId);
						break;
					}
					case 6:
					case 62:{
						buff=new HpReg(buffId);
						break;
					}
					case 9:{
						buff=new MaxMana(buffId);
						break;
					}
					case 15:{
						buff=new Attack(buffId);
						break;
					}
					case 16:{
						buff=new Deff(buffId);
						break;
					}
					case 17:{
						buff=new FinalDamage(buffId);
						break;
					}
					case 21:{
						buff=new AttackSucces(buffId);
						break;
					}
					case 30:{
						buff=new AttackRange(buffId);
						break;
					}
					case 22:{
						buff=new CritSucces(buffId);
						break;
					}
					case 42:{
						buff=new LifeTransform(buffId);
						break;
					}
					case 43:{
						buff=new Stun(buffId);
						break;
					}
					case 44:{
						buff=new Hide(buffId);
						break;
					}
					case 47:{
						buff=new DamageOverTime(buffId);
						break;
					}
					case 49:{
						buff=new Freeze(buffId);
						break;
					}
					case 50:{
						buff=new DeffSucces(buffId);
						break;
					}
					case 52:{
						buff=new AbsorbSchield(buffId);
						break;
					}
					case 57:{
						buff=new Crit(buffId);
						break;
					}
					case 64:{
						buff=new DamageDecreased(buffId);
						break;
					}
					case 63:{
						buff=new ManaReg(buffId);
						break;
					}
					default:{
						buff=null;
						break;
					}
				}
				buffActions.put(buffId, buff);
			}
		}
		System.out.println("buff Init DONE!");
	}

	public static BuffAction getBuffAction(short buffId) {
		if(buffActions.containsKey(buffId)) {
			return buffActions.get(buffId);
		}
		return null;
	}
	
	public static long timeClientToServer(int time) {
		return (long)(time * 4 * 1000); //MH time is int * 4(client checks every 4 seconds). Also converting to milliseconds
	}
	
	public static int timeServerToClient(long time) {
		return (int)(time / 4 / 1000); //revert
	}
	
	
//	public static short getBuffSlot(short buffId, LinkedHashMap<Short, Buff> buffsActive)  {
//		//HashMap<Short, Buff> buffsActive = owner.getBuffs();
//		if(buffsActive.containsKey(buffId)) {
//			List<Short> keyList = new ArrayList<Short>(buffsActive.keySet()); //linked to linkedhashmap
//			System.out.println("Keylist buffs: " + keyList.toString());
//			short j = 0;
//			//for (int i = keyList.size()-1;i>= 0;i--) {
//			for(int i = 0; i<keyList.size(); i++) {
//				short key = keyList.get(i);
//				if(key == buffId)
//					return (short)j;
//				else
//					j++;
//			}
//			return (short)-1; //doesn't suppose to happen
//		}
//		else {
//			return (short)buffsActive.size();
//		}
//	}
	
	public static short getBuffSlot(short buffId, Buff[] buffsActive) {
		short firstEmpty = -1;
		for(short i=0;i<buffsActive.length;i++) {
			if(buffsActive[i] != null) {
				if(buffsActive[i].getId() == buffId)
					return i;
			}
			else
			{
				if(firstEmpty == -1)
					firstEmpty = i;
			}		
		}
		return firstEmpty;
	}
}
