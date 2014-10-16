package Buffs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Database.ItemDAO;
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
			
			if(!buffActions.containsKey(buffId)){
				BuffAction buff;
				//switch is ok, we change the buffclasses names later :P
				switch(buffId){
					case 2:{
						buff=new BuffAction2(buffId);
					}
					case 7:{
						buff=new BuffAction7(buffId);
					}
					case 17:{
						buff=new BuffAction17(buffId);
					}
					default:{
						buff=null;
					}
				}
				buffActions.put(buffId, buff);
			}
			
		}
			
		System.out.println("buff Init DONE!");
	}

	public static BuffAction getBuffAction(int buffId){
		if(buffActions.containsKey(buffId)){
			return buffActions.get(buffId);
		}
		return null;
	}
	
}
