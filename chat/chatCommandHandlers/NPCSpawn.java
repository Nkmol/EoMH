package chat.chatCommandHandlers;



import Connections.Connection;
import ServerCore.ServerFacade;
import Player.Character;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Tools.BitTools;
import chat.ChatCommandExecutor;


public class NPCSpawn implements ChatCommandExecutor {
	private int uniq = 5000000;
	
	private int needsCommandPower;
	
	public NPCSpawn(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	public void execute(String[] parameters, Connection source)	{
		
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		byte[] npcpckt=this.packeteer(cur.getlastknownX(), cur.getlastknownY(), Integer.parseInt(parameters[0]), parameters[1], this.uniq, cur.getCharID());
		cur.sendToMap(npcpckt);
		ServerFacade.getInstance().addWriteByChannel(cur.GetChannel(), npcpckt);
		this.uniq++;
	}

	private byte[] packeteer(float x, float y, int nid, String n, int uniq, int plid) {
		byte[] buff = new byte[611];
		byte[] name = n.getBytes();
		byte[] Xcords = BitTools.floatToByteArray(x);
		byte[] Ycords = BitTools.floatToByteArray(y);
		byte[] id = BitTools.intToByteArray(nid);
		byte[] chid = BitTools.intToByteArray(plid);
		byte[] unique = BitTools.intToByteArray(uniq);
		

		for(int i=0;i<name.length;i++) {
			buff[i+34] = name[i];
		}
			
		buff[0]  = (byte)0x63; 
		buff[1]  = (byte)0x02;
		buff[4]  = (byte)0x04;
		buff[6]  = (byte)0x04;
			
		buff[8] = (byte)0x01;
		
		buff[25] = (byte)0x03; //Some kind of type indicator
		buff[68] = (byte)0x12; //NPC dialog ID
			
		for(int i=0;i<2;i++)  {
			buff[82+i] = id[i];
		}
		
		for(int i=0;i<4;i++) {
			buff[i+9] = chid[i];
			buff[i+102] = Xcords[i];   
			buff[i+106] = Ycords[i];
			buff[i+17] = Xcords[i];
			buff[i+21] = Ycords[i];
			buff[26+i] = unique[i];
		}
			
		return buff;
	}
}
