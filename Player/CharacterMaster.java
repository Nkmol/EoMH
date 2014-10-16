package Player;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import Connections.Connection;
import Gamemaster.GameMaster;
import Player.Character;
import ServerCore.ServerFacade;
import Tools.BitTools;
import World.WMap;

public class CharacterMaster {

	private static int lvlCap;
	private static long lvlExp[];
	private static int highestLvl=9;
	private static final float runningSpeed=24.5f;
	private static final float walkingSpeed=7;
	private static final TreeMap<Integer, Short> fametitles = new TreeMap<Integer, Short>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5098513966635876479L;

		{
			put(18580416, (short)1);
			put(12386944, (short)2);
			put(6193472, (short)3);
			put(3870920, (short)4);
			put(1548368, (short)5);
			put(1161276, (short)6);
			put(774184, (short)7);
			put(580638, (short)8);
			put(387092, (short)9);
		}
	};
	
	public static void init(int lvlCap, long[] lvlExp){
		CharacterMaster.lvlCap=lvlCap;
		CharacterMaster.lvlExp=lvlExp;
	}

	public static int getLvlCap() {
		return lvlCap;
	}

	public static long getLvlExp(int lvl) {
		return lvlExp[lvl];
	}
	
	public static short getFameTitle(int fame) {
		Map<Integer, Short> descending = fametitles.descendingMap();
		for (Map.Entry<Integer, Short> index : descending.entrySet()) {
			if(fame >= index.getKey()) {
				return index.getValue();
			}
		}
		return 0;
	}
	
	public static void announceHighestLevel(Character ch){
		
		if(!GameMaster.isPlayer(ch)){
			return;
		}
		
		if(ch.getLevel()>highestLvl){
			highestLvl=ch.getLevel();
			
			byte[] gmsg1=getGMchatBytes("New highest lvlup this session:");
			byte[] gmsg2=getGMchatBytes("GZ "+ch.getName()+" for lvl "+highestLvl);
			
			Iterator<Map.Entry<Integer, Character>> iter = WMap.getInstance().getCharacterMap().entrySet().iterator();
			Character tmp;
			while(iter.hasNext()) {
				Map.Entry<Integer, Character> pairs = iter.next();
				tmp = pairs.getValue();
				if(!tmp.isBot()){
					ServerFacade.getInstance().addWriteByChannel(tmp.GetChannel(), gmsg1);
					ServerFacade.getInstance().addWriteByChannel(tmp.GetChannel(), gmsg2);
				}
			}
		}
		
	}
	
	public static void announceEventStart(){
		
		byte[] gmsg1=getGMchatBytes("EVENT STARTED");
		byte[] gmsg2=getGMchatBytes("HAVE FUN AND GOOD LUCK");
		byte[] gmsg3=getGMchatBytes("your goal is to kill the lvl 40boss!");
		byte[] gmsg4=getGMchatBytes("but also try to kill the 28 one");
		byte[] gmsg5=getGMchatBytes("it will be worth it to kill him");
			
		Iterator<Map.Entry<Integer, Character>> iter = WMap.getInstance().getCharacterMap().entrySet().iterator();
		Character tmp;
		while(iter.hasNext()) {
			Map.Entry<Integer, Character> pairs = iter.next();
			tmp = pairs.getValue();
			if(!tmp.isBot()){
				ServerFacade.getInstance().addWriteByChannel(tmp.GetChannel(), gmsg1);
				ServerFacade.getInstance().addWriteByChannel(tmp.GetChannel(), gmsg2);
				ServerFacade.getInstance().addWriteByChannel(tmp.GetChannel(), gmsg3);
				ServerFacade.getInstance().addWriteByChannel(tmp.GetChannel(), gmsg4);
				ServerFacade.getInstance().addWriteByChannel(tmp.GetChannel(), gmsg5);
			}
		}
		
	}
	
	public static void announceWinner(Character ch){
		
		byte[] gmsg1=getGMchatBytes("We have a winner");
		byte[] gmsg2=getGMchatBytes("GZ "+ch.getName()+"!");
		byte[] gmsg3=getGMchatBytes("You were the first to kill the boss");
		byte[] gmsg4=getGMchatBytes("Your prize is a lvl 18 wep");
		byte[] gmsg5=getGMchatBytes("Just joking");
		byte[] gmsg6=getGMchatBytes("It is a flame woorden sword");
		byte[] gmsg7=getGMchatBytes("Lvl 1 with 71atk");
		byte[] gmsg8=getGMchatBytes("It will be saved for the next test phase");
		
		Iterator<Map.Entry<Integer, Character>> iter = WMap.getInstance().getCharacterMap().entrySet().iterator();
		Character tmp;
		while(iter.hasNext()) {
			Map.Entry<Integer, Character> pairs = iter.next();
			tmp = pairs.getValue();
			if(!tmp.isBot()){
				ServerFacade.getInstance().addWriteByChannel(tmp.GetChannel(), gmsg1);
				ServerFacade.getInstance().addWriteByChannel(tmp.GetChannel(), gmsg2);
				ServerFacade.getInstance().addWriteByChannel(tmp.GetChannel(), gmsg3);
				ServerFacade.getInstance().addWriteByChannel(tmp.GetChannel(), gmsg4);
				ServerFacade.getInstance().addWriteByChannel(tmp.GetChannel(), gmsg5);
				ServerFacade.getInstance().addWriteByChannel(tmp.GetChannel(), gmsg6);
				ServerFacade.getInstance().addWriteByChannel(tmp.GetChannel(), gmsg7);
				ServerFacade.getInstance().addWriteByChannel(tmp.GetChannel(), gmsg8);
			}
		}
		
	}
	
	public static byte[] getGMchatBytes(String message){
		byte[] gmsg = new byte[14+message.length()];
		byte[] msg = BitTools.stringToByteArray(message);
		
		gmsg[0] = (byte)gmsg.length;
		gmsg[4] = (byte)0x03;
		gmsg[6] = (byte)0x50;
		gmsg[7] = (byte)0xC3;
		gmsg[8] = (byte)0x01;
		gmsg[9] = (byte)0x23;
		
		for(int i=0;i<msg.length;i++) {
			gmsg[i+13] = msg[i];
		}
		return gmsg;
	}
	
	public static byte[] backToSelection(Connection con){
		
		Character current = ((PlayerConnection)con).getActiveCharacter();
		current.leaveGameWorld(true); //leave the gameworld
		con.getWriteBuffer().clear(); //clear all packets pending write(prevent client from crashing as it returns to selection)
		Player tmplayer = ((PlayerConnection)con).getPlayer();
		
		tmplayer.setActiveCharacter(null);
		List<Character> characters = tmplayer.getCharacters();
		Iterator<Character> citer = characters.iterator();
		ByteBuffer all = ByteBuffer.allocate((characters.size()*653)+8+3);
		byte[] size = BitTools.shortToByteArray((short)all.capacity());
		all.put(size);
		all.put(new byte[] { (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x01, (byte)0x00 }); //almost same header/packet as when logging in game(0x04 -> 0x01)
		
		all.put(new byte[] { (byte)0x01, (byte)0x01, (byte)0x01 });
		
		Character ctm = citer.next();
										
		byte[] tmp = ctm.initCharPacket();
		for(int i=0;i<tmp.length;i++) {
			all.put(tmp[i]);
		}
		
		while(citer.hasNext()) {

			Character ctmp = citer.next();
											
			byte[] tmpb = ctmp.initCharPacket();
			for(int i=0;i<tmpb.length;i++) {
				all.put(tmpb[i]);
			}
			
			all.put(10, (byte)((all.get(10)*2)+1)); //required increment depending on amount of characters on account
		}
		
		all.rewind();
		byte[] rval = new byte[all.limit()];
		all.get(rval);
		return rval;
		
	}

	public static float getRunningspeed() {
		return runningSpeed;
	}

	public static float getWalkingspeed() {
		return walkingSpeed;
	}
	
	
	
}
