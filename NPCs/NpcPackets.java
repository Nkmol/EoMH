package NPCs;

import Tools.BitTools;
import Player.Character;

public class NpcPackets {

	public static byte[] getNpcSpawnPacket(Npc npc, Character ch) {
		byte[] npcSpawn = new byte[615];
		byte[] name = npc.getName().getBytes();
		byte[] Xcords = BitTools.floatToByteArray(npc.getlastknownX());
		byte[] Ycords = BitTools.floatToByteArray(npc.getlastknownY());
		byte[] chXcords = BitTools.floatToByteArray(ch.getlastknownX());
		byte[] chYcords = BitTools.floatToByteArray(ch.getlastknownY());
		byte[] id = BitTools.intToByteArray(npc.getId());
		byte[] chid = BitTools.intToByteArray(ch.getCharID());
		byte[] unique = BitTools.intToByteArray(npc.getuid());
		

		for(int i=0;i<name.length;i++) {
			npcSpawn[i+34] = name[i];
		}
			
		npcSpawn[0]  = (byte)0x67; 
		npcSpawn[1]  = (byte)0x02;
		npcSpawn[4]  = (byte)0x04;
		npcSpawn[6]  = (byte)0x04;
			
		npcSpawn[8] = (byte)0x01;
		
		npcSpawn[25] = (byte)0x03; //Some kind of type indicator
		npcSpawn[68] = (byte)0x12; //NPC dialog ID
			
		for(int i=0;i<2;i++)  {
			npcSpawn[82+i] = id[i];
		}
		
		for(int i=0;i<4;i++) {
			npcSpawn[i+9] = chid[i];
			npcSpawn[i+102] = Xcords[i];   
			npcSpawn[i+106] = Ycords[i];
			npcSpawn[i+17] = chXcords[i];
			npcSpawn[i+21] = chYcords[i];
			npcSpawn[26+i] = unique[i];
		}
		
		npcSpawn[612] = (byte)0x22;
		npcSpawn[613] = (byte)0x08;
			
		return npcSpawn;
	}
	
	public static byte[] getNpcVanishPacket(Npc npc, Character ch){
		
		byte[] uid=BitTools.intToByteArray(npc.getuid());
		byte[] chid = BitTools.intToByteArray(ch.getCharID());
		
		byte[] nv = new byte[20];
		nv[0] = (byte)nv.length;
		nv[4] = (byte)0x05;
		nv[6] = (byte)0x0F;
		nv[8] = (byte)0x01;
		
		
		for(int i=0;i<4;i++) {
			nv[12+i] = chid[i];
			nv[16+i] = uid[i];
		}
		
		return nv;
		
	}
	
}
