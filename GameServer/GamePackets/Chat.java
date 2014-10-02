package GameServer.GamePackets;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import chat.ChatParser;
import Connections.Connection;
import Encryption.Decryptor;
import GameServer.ServerPackets.ServerMessage;
import Player.Player;
import Player.PlayerConnection;
import Player.Character;
import ServerCore.ServerFacade;
import Tools.BitTools;
import World.WMap;


public class Chat implements Packet {

	@Override
	public void execute(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}


	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) {
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
		Player tmplayer = ((PlayerConnection)con).getPlayer();
		Character current = tmplayer.getActiveCharacter();
		
		
		byte[] name = current.getName().getBytes();
		byte[] chatRelay = new byte[decrypted[19]+44];
		
		chatRelay[0] = (byte)chatRelay.length;
		chatRelay[4] = (byte)0x05;
		chatRelay[6] = (byte)0x07;
		
		for(int i=0;i<name.length;i++) {
			chatRelay[i+20] = name[i];
		}
		
		byte[] stuffz = new byte[] { (byte)0x01, (byte)0xfe, (byte)0x14, (byte)0x08, (byte)0x30, (byte)0x12, (byte)0x0c, (byte)0x00,  
	       (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00};
	       
		for(int i=0;i<stuffz.length;i++) {
			chatRelay[i+8] = stuffz[i];
		}
		
		//chattype
		chatRelay[18]=decrypted[0];
		
		byte[] cid = BitTools.intToByteArray(current.getCharID());
		for(int i=0;i<4;i++) {
			chatRelay[12+i] = cid[i];
		}
		
		byte[] msgbytes = new byte[decrypted[19]];
		
		for(int i=0;i<decrypted[19];i++) {
			chatRelay[i+44] = decrypted[i+23];
			msgbytes[i] = decrypted[i+23];
		}
		
		chatRelay[40] = decrypted[19];
		
		if(current.hasCommands()){
			if(ChatParser.getInstance().parseAndExecuteChatCommand(new String(msgbytes), con))
				return null;
		}
		
		if(decrypted[0]==1){
			//whisper
			int i=0;
			LinkedList<Byte> namechars=new LinkedList<Byte>();
			while(i<16 && decrypted[i+2]!=0){
				namechars.add(decrypted[i+2]);
				i++;
			}
			String chname="";
			i=0;
			while(!namechars.isEmpty()){
				chname+=(char)(namechars.removeFirst().intValue());
				i++;
			}
			Character target=WMap.getInstance().getCharacter(chname);
			if(target!=null){
				ServerFacade.getInstance().addWriteByChannel(target.GetChannel(),chatRelay);
			}else{
				new ServerMessage().execute(chname+" is not online",con);
			}
		}else if(decrypted[0]==0){
			current.sendToMap(chatRelay);
			String text="";
			for(int i=0;i<decrypted[19];i++){
				text+=(char)decrypted[23+i];
			}
			current.sendChatToDolls(text);
		}else if(current.getPt()!=null && decrypted[0]==2)
			current.getPt().sendToMembers(chatRelay,current);
		
		return null;
	}

}
