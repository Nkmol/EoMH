package ExperimentalStuff;

import Player.CharacterPackets;
import Player.Dolls.DollMaster;
import World.Area;
import World.OutOfGridException;
import World.WMap;

public class EffectMaster {
	
	private static int[] effectIds={213062504,213062505,213062508,213062509,213062575,213062576};
	//0 = blue fireworks
	//1 = pink fireworks
	//2 = yellow fireworks
	//3 = red fireworks
	//4 = tree fireworks
	//5 = star fireworks

	/**
	 * Fake CharacterSpawn+ UsableItem+ CharacterDespawn packets
	 * looks like an effect is spawned in the air
	 * @param map
	 * @param X
	 * @param Y
	 * @param effId
	 */
	public static void spawnEffects(int map, float X, float Y, int effId){
		if(effId<effectIds.length){
			int uid=DollMaster.getUid();
			Area area;
			try {
				area = WMap.getInstance().getGrid(map).getAreaByCoords(X, Y);
				if(area!=null){
					byte[] dummySpawn=CharacterPackets.getSimpleDummyExtPacket(X, Y, uid);
					byte[] dummyDespawn=CharacterPackets.getVanishByID(uid);
					byte[] effect=CharacterPackets.getExtUseItemPacket(uid, effectIds[effId]);
					area.sendToMembers(-1, dummySpawn);
					area.sendToMembers(-1, effect);
					area.sendToMembers(-1, dummyDespawn);
				}
			} catch (OutOfGridException e) {
				e.printStackTrace();
			}
		}
	}
	
}