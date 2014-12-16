package GameServer;

import java.util.Iterator;
import java.util.Map;

import Player.Character;
import ServerCore.ServerFacade;
import World.WMap;
import Database.ServerControlDAO;

public class ServerMaster {
	
	private static String serverName;
	private static ServerEvent currentEvent;
	private static ServerControlDAO dao;

	public static void init(String sName){
		dao=ServerControlDAO.getInstance();
		serverName=sName;
		currentEvent=dao.getCurrentEvent();
	}
	
	public static String getServerName(){
		return serverName;
	}
	
	public static ServerEvent getCurrentEvent(){
		if(currentEvent==null)
			System.out.println("\n\n\nWARNING: NO EVENT LOADED!!! THIS CAN CAUSES ERRORS!!!\n\n\n");
		return currentEvent;
	}
	
	public static void updateEvent(String eventName){
		dao.changeServerControlEvent(eventName);
		currentEvent=dao.getCurrentEvent();
		if(currentEvent!=null){
			System.out.println("New Event: "+currentEvent.getDescription());
			announceChat("New Event: "+currentEvent.getDescription());
		}
	}
	
	public static void announceChat(String text){
		
		byte[] gmsg = new byte[14+text.length()];
		byte[] msg = text.getBytes();
		
		gmsg[0] = (byte)gmsg.length;
		gmsg[4] = (byte)0x03;
		gmsg[6] = (byte)0x50;
		gmsg[7] = (byte)0xC3;
		gmsg[8] = (byte)0x01;
		gmsg[9] = (byte)0x23;
		
		for(int i=0;i<msg.length;i++) {
			gmsg[i+13] = msg[i];
		}
		
		Iterator<Map.Entry<Integer, Character>> iter = WMap.getInstance().getCharacterMap().entrySet().iterator();
		Character tmp;
		while(iter.hasNext()) {
			Map.Entry<Integer, Character> pairs = iter.next();
			tmp = pairs.getValue();
			if(!tmp.isBot())
			ServerFacade.getInstance().addWriteByChannel(tmp.GetChannel(), gmsg);
		}
		
	}
	
}
