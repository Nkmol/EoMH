package Player;

import java.util.LinkedList;

import chat.ChatParser;
import ServerCore.ServerFacade;
import Tools.BitTools;
import World.WMap;
import GameServer.ServerPackets.ServerMessage;
import Player.Character;

public class ChatMaster {

	public static byte[] getChatPacket(int textboxchid, String name, String text, byte type){
		char[] ctext = text.toCharArray();
		byte[] btext = new byte[ctext.length];
		byte[] bname = BitTools.stringToByteArray(name);
		
		for(int i=0;i<ctext.length;i++)
			btext[i]=(byte)ctext[i];
		
		byte[] msg = new byte[44+btext.length];
		
		msg[0] = (byte)(44+btext.length);
		msg[4] = (byte)0x05;
		msg[6] = (byte)0x07;
		msg[8] = (byte)0x01;
		msg[17] = (byte)0x01;
		
		//chat color
		msg[18] = type;
		
		//id
		byte[] cid = BitTools.intToByteArray(textboxchid);
	    for(int i=0;i<4;i++) {
	      msg[12+i] = cid[i];
	    }
		
		//name [Server]
		for(int i=0;i<name.length();i++){
			msg[20+i]=bname[i];
		}
		
		msg[40] = (byte)btext.length;
		
		//text
		for(int i=0;i<btext.length;i++)
			msg[44+i]=btext[i];
		
		return msg;
	}
	
	public static void sendPublicChatLine(Character ch, String name, String text, boolean toOwnerToo){
		ch.sendToMap(getChatPacket(ch.getCharID(),name, text, (byte)0));
		if(toOwnerToo)
			ServerFacade.getInstance().addWriteByChannel(ch.GetChannel(),getChatPacket(ch.getCharID(),name, text, (byte)0));
		ch.sendChatToDolls(text);
	}
	
	public static void sendWhisperChatLine(Character ch, String name, String text, Character target, String tname, boolean toOwnerToo){
		if(toOwnerToo)
			ServerFacade.getInstance().addWriteByChannel(ch.GetChannel(),getChatPacket(ch.getCharID(),name, text, (byte)1));
		if(target!=null && tname!=null){
			ServerFacade.getInstance().addWriteByChannel(target.GetChannel(),getChatPacket(ch.getCharID(),name, text, (byte)1));
		}else{
			new ServerMessage().execute(tname+" is not online",ServerFacade.getInstance().getConnectionByChannel(ch.GetChannel()));
		}
	}

	public static void sendPartyChatLine(Character ch, String name, String text, boolean toOwnerToo){
		if(toOwnerToo)
			ServerFacade.getInstance().addWriteByChannel(ch.GetChannel(),getChatPacket(ch.getCharID(),name, text, (byte)0));
		if(ch.getPt()!=null){
			ch.getPt().sendToMembers(getChatPacket(ch.getCharID(),name, text, (byte)2),ch);
		}
	}
	
	public static void handleChat(Character ch, byte[] packet){
		//TEXT
		LinkedList<Byte> textchars=new LinkedList<Byte>();
		for(int i=0;i<packet[19];i++){
			textchars.add(packet[i+23]);
		}
		String text="";
		while(!textchars.isEmpty()){
			text+=(char)(textchars.removeFirst().intValue());
		}
		//TYPE
		byte type=packet[0];
		//NAME
		String name=ch.getName();
		//COMMAND
		Character target=null;
		String chname=null;
		if(type==1){
			int i=0;
			LinkedList<Byte> namechars=new LinkedList<Byte>();
			while(i<16 && packet[i+2]!=0){
				namechars.add(packet[i+2]);
				i++;
			}
			chname="";
			while(!namechars.isEmpty()){
				chname+=(char)(namechars.removeFirst().intValue());
			}
			target=WMap.getInstance().getCharacter(chname);
		}
		if(ch.hasCommands()){
			ChatParser.getInstance().parseAndExecuteChatCommand(ch, name, text, type, target, chname);
		}
		prepareSendingChat(ch,name,text,type,target,chname,false);
	}
	
	public static void prepareSendingChat(Character ch, String name, String text, byte type, Character target, String targetName, boolean toOwnerToo){
		switch(type){
			case 0:{
				sendPublicChatLine(ch,name,text,toOwnerToo);
				break;
			}
			case 1:{
				sendWhisperChatLine(ch,name,text,target,targetName,toOwnerToo);
				break;
			}
			case 2:{
				sendPartyChatLine(ch,name,text,toOwnerToo);
				break;
			}
			default:{
				break;
			}
		}
	}
	
}
