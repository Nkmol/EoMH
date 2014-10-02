package Duel;

import Tools.BitTools;
import Player.Character;

public class DuelPackets {

	public static byte[] getStartDuelPacket(Character askingGuy, Character acceptingGuy){
		
		byte[] cid=BitTools.intToByteArray(askingGuy.getCharID());
		byte[] cid2=BitTools.intToByteArray(acceptingGuy.getCharID());
		
		byte startduel[]=new byte[64];
		startduel[0]=(byte)startduel.length;
		startduel[4]=(byte)0x04;
		startduel[6]=(byte)0x2a;
		
		startduel[8]=(byte)0x01;
		startduel[9]=(byte)0x9d;
		startduel[10]=(byte)0x0f;
		startduel[11]=(byte)0xbf;
		
		for(int i=0;i<4;i++){
			startduel[20+i]=cid[i];
			startduel[24+i]=cid2[i];
			startduel[60+i]=cid[i];
		}
		
		startduel[16]=(byte)0x01;
		startduel[18]=(byte)0x03;
		startduel[19]=(byte)0x08;
		
		startduel[28]=(byte)0xf8;
		startduel[29]=(byte)0x72;
		startduel[30]=(byte)0x86;
		
		startduel[32]=(byte)0x98;
		startduel[33]=(byte)0x90;
		startduel[34]=(byte)0x91;
		startduel[35]=(byte)0x16;
		startduel[36]=(byte)0xc0;
		startduel[37]=(byte)0x11;
		
		startduel[39]=(byte)0x40;
		startduel[40]=(byte)0x01;
		
		startduel[48]=(byte)0x78;
		startduel[49]=(byte)0xc8;
		startduel[50]=(byte)0xec;
		startduel[51]=(byte)0x1a;
		startduel[52]=(byte)0xf4;
		startduel[53]=(byte)0xfa;
		startduel[54]=(byte)0x18;
		
		startduel[57]=(byte)0x29;
		
		return startduel;
		
	}
	
	public static byte[] getAskDuelPacket(Character askingGuy, Character acceptingGuy){
		
		byte[] cid=BitTools.intToByteArray(askingGuy.getCharID());
		byte[] cid2=BitTools.intToByteArray(acceptingGuy.getCharID());
		
		byte startduel[]=new byte[64];
		startduel[0]=(byte)startduel.length;
		startduel[4]=(byte)0x04;
		startduel[6]=(byte)0x2a;
		
		startduel[8]=(byte)0x01;
		startduel[9]=(byte)0x28;
		
		for(int i=0;i<4;i++){
			startduel[20+i]=cid2[i];
			startduel[60+i]=cid[i];
		}
		
		startduel[16]=(byte)0x01;
		startduel[18]=(byte)0x02;
		startduel[19]=(byte)0x08;
		
		startduel[24]=(byte)0x01;
		
		startduel[28]=(byte)0x88;
		startduel[29]=(byte)0x16;
		startduel[30]=(byte)0x5d;
		startduel[31]=(byte)0x08;
		startduel[32]=(byte)0x01;
		startduel[33]=(byte)0xa0;
		startduel[34]=(byte)0xdf;
		startduel[35]=(byte)0x2a;
		startduel[36]=(byte)0x01;
		
		startduel[40]=(byte)0x3c;
		startduel[41]=(byte)0xc0;
		startduel[42]=(byte)0xd1;
		startduel[43]=(byte)0x2a;
		startduel[44]=(byte)0x18;
		startduel[45]=(byte)0x9e;
		startduel[46]=(byte)0x0f;
		startduel[47]=(byte)0xbf;
		startduel[48]=(byte)0xfa;
		startduel[49]=(byte)0x44;
		startduel[50]=(byte)0x08;
		startduel[51]=(byte)0x08;
		startduel[52]=(byte)0x80;
		startduel[53]=(byte)0x16;
		startduel[54]=(byte)0x5d;
		startduel[55]=(byte)0x08;
		
		startduel[57]=(byte)0x28;
		
		return startduel;
		
	}
	
	public static byte[] getRefuseDuelPacket(Character askingGuy, Character refusingGuy){
		
		byte[] cid=BitTools.intToByteArray(askingGuy.getCharID());
		byte[] cid2=BitTools.intToByteArray(refusingGuy.getCharID());
		
		byte startduel[]=new byte[64];
		startduel[0]=(byte)startduel.length;
		startduel[4]=(byte)0x04;
		startduel[6]=(byte)0x2a;
		
		startduel[8]=(byte)0x01;
		startduel[9]=(byte)0x9d;
		startduel[10]=(byte)0x0f;
		startduel[11]=(byte)0xbf;
		
		for(int i=0;i<4;i++){
			startduel[20+i]=cid2[i];
			startduel[60+i]=cid[i];
		}
		
		startduel[16]=(byte)0x01;
		startduel[19]=(byte)0x08;
		
		startduel[24]=(byte)0x30;
		startduel[25]=(byte)0x26;
		startduel[26]=(byte)0xc1;
		startduel[27]=(byte)0x2a;
		startduel[28]=(byte)0x28;
		startduel[29]=(byte)0x57;
		startduel[30]=(byte)0x51;
		startduel[31]=(byte)0x28;
		startduel[32]=(byte)0x28;
		startduel[33]=(byte)0x57;
		startduel[34]=(byte)0x51;
		startduel[35]=(byte)0x28;
		
		startduel[37]=(byte)0x42;
		startduel[38]=(byte)0xdb;
		startduel[39]=(byte)0x2a;
		startduel[40]=(byte)0x44;
		startduel[41]=(byte)0xd0;
		startduel[42]=(byte)0xc0;
		startduel[43]=(byte)0x2a;
		startduel[44]=(byte)0x18;
		startduel[45]=(byte)0x9e;
		startduel[46]=(byte)0x0f;
		startduel[47]=(byte)0xbf;
		startduel[48]=(byte)0x97;
		startduel[49]=(byte)0x96;
		startduel[50]=(byte)0x15;
		startduel[51]=(byte)0x08;
		startduel[52]=(byte)0x08;
		startduel[53]=(byte)0x9e;
		startduel[54]=(byte)0x0f;
		startduel[55]=(byte)0xbf;
		
		startduel[57]=(byte)0x28;
		
		return startduel;
		
	}
	
	public static byte[] getAcceptDuelPacket(Character askingGuy, Character acceptingGuy){
		
		byte[] cid=BitTools.intToByteArray(askingGuy.getCharID());
		byte[] cid2=BitTools.intToByteArray(acceptingGuy.getCharID());
		
		byte startduel[]=new byte[64];
		startduel[0]=(byte)startduel.length;
		startduel[4]=(byte)0x04;
		startduel[6]=(byte)0x2a;
		
		startduel[8]=(byte)0x01;
		startduel[9]=(byte)0x43;
		startduel[10]=(byte)0x65;
		startduel[11]=(byte)0x28;
		
		for(int i=0;i<4;i++){
			startduel[20+i]=cid[i];
			startduel[60+i]=cid2[i];
		}
		
		startduel[16]=(byte)0x01;
		startduel[18]=(byte)0x01;
		startduel[19]=(byte)0x08;
		
		startduel[24]=(byte)0x01;
		
		startduel[28]=(byte)0x18;
		startduel[29]=(byte)0x9e;
		startduel[30]=(byte)0x0f;
		startduel[31]=(byte)0xbf;
		startduel[32]=(byte)0x84;
		startduel[33]=(byte)0x6b;
		startduel[34]=(byte)0x15;
		startduel[35]=(byte)0x08;
		startduel[36]=(byte)0x58;
		startduel[37]=(byte)0x43;
		startduel[37]=(byte)0x65;
		startduel[39]=(byte)0x28;
		startduel[40]=(byte)0xe0;
		startduel[41]=(byte)0x42;
		startduel[42]=(byte)0xdb;
		startduel[43]=(byte)0x2a;
		
		startduel[48]=(byte)0xfa;
		startduel[49]=(byte)0x44;
		startduel[50]=(byte)0x08;
		startduel[51]=(byte)0x08;
		startduel[52]=(byte)0x80;
		startduel[53]=(byte)0x16;
		startduel[54]=(byte)0x5d;
		startduel[55]=(byte)0x08;
		
		startduel[57]=(byte)0x29;
		
		return startduel;
		
	}
	
	public static byte[] getEndDuelPacket(Character winner, Character loser){
		
		byte[] cid=BitTools.intToByteArray(winner.getCharID());
		byte[] cid2=BitTools.intToByteArray(loser.getCharID());
		
		byte endduel[]=new byte[68];
		endduel[0]=(byte)endduel.length;
		endduel[4]=(byte)0x04;
		endduel[6]=(byte)0x2b;
		
		endduel[8]=(byte)0x01;
		endduel[9]=(byte)0x16;
		endduel[10]=(byte)0x5d;
		endduel[11]=(byte)0x08;
		
		for(int i=0;i<4;i++){
			endduel[20+i]=cid[i];
			endduel[44+i]=cid2[i];
		}
		
		endduel[17]=(byte)0x0c;
		endduel[18]=(byte)0xd1;
		endduel[19]=(byte)0x2a;
		
		endduel[24]=(byte)0xb9;
		endduel[24]=(byte)0xe8;
		endduel[24]=(byte)0xbf;
		endduel[24]=(byte)0xeb;
		endduel[28]=(byte)0xc1;
		endduel[29]=(byte)0xd8;
		endduel[30]=(byte)0x31;
		
		endduel[41]=(byte)0x12;
		endduel[42]=(byte)0x5d;
		endduel[43]=(byte)0x08;
		
		endduel[48]=(byte)0xb9;
		endduel[49]=(byte)0xe8;
		endduel[50]=(byte)0xbf;
		endduel[51]=(byte)0xeb;
		endduel[52]=(byte)0xc1;
		endduel[53]=(byte)0xd8;
		
		endduel[65]=(byte)0x98;
		endduel[66]=(byte)0x0f;
		endduel[67]=(byte)0xbf;
		
		return endduel;
		
	}
	
}
