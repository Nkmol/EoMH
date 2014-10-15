package Gamemaster;

import java.util.HashMap;
import Player.Character;

public class GameMaster {
	
	private static HashMap<Integer, GameMasterRank> ranks=new HashMap<Integer, GameMasterRank>();

	public static void init(HashMap<Integer, GameMasterRank> ranks){
		
		GameMaster.ranks=ranks;
		
	}
	
	public static boolean canUseCommand(Character ch, int needsCommandPower){
		
		int rank=ch.getGMrank();
		
		if(ranks.containsKey(rank) && ranks.get(rank).getCommandpower()>=needsCommandPower){
			return true;
		}
		return false;
		
	}
	
	public static boolean canAllocateGMrank(Character ch, int allocatedRank){
		
		int rank=ch.getGMrank();
		
		if(ranks.containsKey(rank) && ranks.get(rank).getAllocateGMrank()>=allocatedRank){
			return true;
		}
		return false;
		
	}
	
	public static String getGMprename(Character ch){
		
		int rank=ch.getGMrank();
		
		if(ranks.containsKey(rank)){
			return ranks.get(rank).getPrename();
		}
		return "";
		
	}
	
	public static boolean hasGMname(Character ch){
		
		int rank=ch.getGMrank();
		if(ranks.containsKey(rank) && ranks.get(rank).gotGMname()){
			return true;
		}

		return false;
		
	}
	
	public static boolean isPlayer(Character ch){
		
		int rank=ch.getGMrank();
		
		if(ranks.containsKey(rank)){
			return ranks.get(rank).isPlayer();
		}
		return true;
		
	}
	
}
