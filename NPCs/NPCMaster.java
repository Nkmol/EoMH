package NPCs;

import java.util.HashMap;

public class NPCMaster {

	static HashMap<Integer,Npc> NPCuids=new HashMap<Integer,Npc>();
	
	public static void loadNPC(int uid, Npc npc){
		NPCuids.put(uid,npc);
	}
	
	public static Npc getNpcByUid(int uid){
		return NPCuids.get(uid);
	}
	
}
