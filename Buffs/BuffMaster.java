package Buffs;

import java.util.HashMap;

public class BuffMaster {
	
	private static HashMap<Integer, BuffAction> buffActions;
	
	public void init(){
		//get buff action ids from db and create BuffAction objects and load them to buffActions
	}

	public static BuffAction getBuffAction(int buffId){
		if(buffActions.containsKey(buffId)){
			return buffActions.get(buffId);
		}
		return null;
	}
	
}
