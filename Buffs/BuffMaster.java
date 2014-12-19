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
					case 2:{
						buff=new BuffAction2(buffId);
						break;
					}
					case 6:{
						buff=new BuffAction6(buffId);
						break;
					}
					case 7:{
						buff=new BuffAction7(buffId);
						break;
					}
					case 15:{
						buff=new BuffAction15(buffId);
						break;
					}
					case 16:{
						buff=new BuffAction16(buffId);
						break;
					}
					case 17:{
						buff=new BuffAction17(buffId);
						break;
					}
					case 21:{
						buff=new BuffAction21(buffId);
						break;
					}
					case 22:{
						buff=new BuffAction22(buffId);
						break;
					}
					case 42:{
						buff=new BuffAction42(buffId);
						break;
					}
					case 43:{
						buff=new BuffAction43(buffId);
						break;
					}
					case 44:{
						buff=new BuffAction44(buffId);
						break;
					}
					case 47:{
						buff=new BuffAction47(buffId);
						break;
					}
					case 49:{
						buff=new BuffAction49(buffId);
						break;
					}
					case 50:{
						buff=new BuffAction50(buffId);
						break;
					}
					case 52:{
						buff=new BuffAction52(buffId);
						break;
					}
					case 57:{
						buff=new BuffAction57(buffId);
						break;
					}
					case 64:{
						buff=new BuffAction64(buffId);
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
