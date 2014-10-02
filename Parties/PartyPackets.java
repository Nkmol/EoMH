package Parties;

import Tools.BitTools;
import Player.Character;

public class PartyPackets {

	public static byte[] getAskForPtPacket(Character cur, Character otherGuy){
		
		byte[] cid=BitTools.intToByteArray(cur.getCharID());
		byte[] cid2=BitTools.intToByteArray(otherGuy.getCharID());
		
		byte startparty[]=new byte[64];
		startparty[0]=(byte)startparty.length;
		startparty[4]=(byte)0x04;
		startparty[6]=(byte)0x23;
		
		startparty[8]=(byte)0x01;
		startparty[9]=(byte)0x43;
		startparty[10]=(byte)0x65;
		startparty[11]=(byte)0x28;
		
		for(int i=0;i<4;i++){
			startparty[20+i]=cid2[i];
			startparty[60+i]=cid[i];
		}
		
		startparty[16]=(byte)0x01;
		startparty[18]=(byte)0x02;
		startparty[19]=(byte)0x08;
		
		startparty[28]=(byte)0x38;
		startparty[29]=(byte)0x9e;
		startparty[30]=(byte)0x0f;
		startparty[31]=(byte)0xbf;
		startparty[32]=(byte)0x84;
		startparty[33]=(byte)0x6b;
		startparty[34]=(byte)0x15;
		startparty[35]=(byte)0x08;
		startparty[36]=(byte)0x58;
		startparty[37]=(byte)0x43;
		startparty[38]=(byte)0x65;
		startparty[39]=(byte)0x28;
		startparty[40]=(byte)0x40;
		startparty[41]=(byte)0x42;
		startparty[42]=(byte)0xdb;
		startparty[43]=(byte)0x2a;
		
		startparty[48]=(byte)0x2d;
		startparty[49]=(byte)0x12;
		startparty[50]=(byte)0x5d;
		startparty[51]=(byte)0x08;
		startparty[52]=(byte)0x2f;
		startparty[53]=(byte)0x9e;
		startparty[54]=(byte)0x0f;
		startparty[55]=(byte)0xbf;
		startparty[56]=(byte)0x48;
		startparty[57]=(byte)0x9c;
		startparty[58]=(byte)0x5b;
		startparty[59]=(byte)0x08;
		
		return startparty;
		
	}
	
	public static byte[] getAcceptPtPacket(Party pt, Character acceptingGuy){
		
		byte[] cid=BitTools.intToByteArray(acceptingGuy.getCharID());
		byte[] member;
		
		byte startparty[]=new byte[64];
		startparty[0]=(byte)startparty.length;
		startparty[4]=(byte)0x04;
		startparty[6]=(byte)0x23;
		
		startparty[8]=(byte)0x01;
		startparty[9]=(byte)0x9d;
		startparty[10]=(byte)0x0f;
		startparty[11]=(byte)0xbf;
		
		for(int i=0;i<4;i++){
			startparty[20+i]=cid[i];
			startparty[60+i]=cid[i];
		}
		
		for(int j=0;j<pt.getMemberAmount();j++){
			member=BitTools.intToByteArray(pt.getMember(j).getCharID());
			for(int i=0;i<4;i++){
				startparty[28+j*4+i]=member[i];
			}
		}
		
		startparty[16]=(byte)0x01;
		startparty[18]=(byte)0x01;
		startparty[19]=(byte)0x28;
		startparty[24]=(byte)0x28;
		
		return startparty;
		
	}
	
	public static byte[] getRefusePtPacket(Character askingGuy, Character refusingGuy){
		
		byte[] cid=BitTools.intToByteArray(askingGuy.getCharID());
		byte[] cid2=BitTools.intToByteArray(refusingGuy.getCharID());
		
		byte startparty[]=new byte[64];
		startparty[0]=(byte)startparty.length;
		startparty[4]=(byte)0x04;
		startparty[6]=(byte)0x23;
		
		startparty[8]=(byte)0x01;
		startparty[9]=(byte)0xd0;
		startparty[10]=(byte)0xc0;
		startparty[11]=(byte)0x2a;
		
		for(int i=0;i<4;i++){
			startparty[20+i]=cid2[i];
			startparty[60+i]=cid[i];
		}
		
		startparty[16]=(byte)0x01;
		startparty[19]=(byte)0x08;
		
		startparty[28]=(byte)0xa0;
		startparty[29]=(byte)0x9e;
		startparty[30]=(byte)0x0f;
		startparty[31]=(byte)0xbf;
		startparty[32]=(byte)0x44;
		startparty[33]=(byte)0xfc;
		startparty[34]=(byte)0x14;
		startparty[35]=(byte)0x08;
		startparty[36]=(byte)0x18;
		startparty[37]=(byte)0x26;
		startparty[38]=(byte)0xc1;
		startparty[39]=(byte)0x2a;
		startparty[40]=(byte)0x48;
		startparty[41]=(byte)0x9c;
		startparty[42]=(byte)0x5b;
		startparty[43]=(byte)0x08;
		startparty[44]=(byte)0x38;
		startparty[45]=(byte)0x9e;
		startparty[46]=(byte)0x0f;
		startparty[47]=(byte)0xbf;
		startparty[48]=(byte)0x2c;
		startparty[49]=(byte)0x12;
		startparty[50]=(byte)0x5d;
		startparty[51]=(byte)0x08;
		startparty[52]=(byte)0x2f;
		startparty[53]=(byte)0x9e;
		startparty[54]=(byte)0x0f;
		startparty[55]=(byte)0xbf;
		startparty[56]=(byte)0x48;
		startparty[57]=(byte)0x9c;
		startparty[58]=(byte)0x5b;
		startparty[59]=(byte)0x08;
		
		return startparty;
		
	}
	
	public static byte[] getRefreshPtPacket(Character member, Party pt){
		
		byte refreshpt[]=new byte[60];
		refreshpt[0]=(byte)refreshpt.length;
		refreshpt[4]=(byte)0x05;
		refreshpt[6]=(byte)0x26;
		
		refreshpt[8]=(byte)0x01;
		refreshpt[9]=(byte)0x9d;
		refreshpt[10]=(byte)0x0f;
		refreshpt[11]=(byte)0xbf;
		
		byte[] memberid=BitTools.intToByteArray(member.getCharID());
		byte[] name=BitTools.stringToByteArray(member.getName());
		byte[] hp=BitTools.intToByteArray(member.getHp());
		byte[] mana=BitTools.intToByteArray(member.getMana());
		byte[] stam=BitTools.intToByteArray(member.getStamina());
		byte[] maxhp=BitTools.intToByteArray(member.getMaxhp());
		byte[] maxmana=BitTools.intToByteArray(member.getMaxmana());
		byte[] maxstam=BitTools.intToByteArray(member.getMaxstamina());
		
		for(int i=0;i<4;i++){
			refreshpt[16+i]=memberid[i];
		}
		
		for(int i=0;i<name.length;i++){
			refreshpt[20+i]=name[i];
		}
		for(int i=name.length;i<16;i++){
			refreshpt[20+i]=(byte)0x00;
		}
		
		refreshpt[38]=(byte)member.getCharacterClass();
		refreshpt[40]=(byte)member.getLevel();
		
		for(int i=0;i<2;i++){
			refreshpt[44+i]=hp[i];
			refreshpt[48+i]=mana[i];
			refreshpt[50+i]=stam[i];
			refreshpt[52+i]=maxhp[i];
			refreshpt[56+i]=maxmana[i];
			refreshpt[58+i]=maxstam[i];
		}
		
		return refreshpt;
		
	}
	
	public static void sendRefreshPtPacket(Party pt){
		
		Character member;
		byte[] memberid, name, hp, mana, stam, maxhp, maxmana, maxstam;
		
		byte partymembers[]=new byte[60];
		partymembers[0]=(byte)partymembers.length;
		partymembers[4]=(byte)0x05;
		partymembers[6]=(byte)0x26;
		
		partymembers[8]=(byte)0x01;
		partymembers[9]=(byte)0x9d;
		partymembers[10]=(byte)0x0f;
		partymembers[11]=(byte)0xbf;
		
		for(int j=0;j<pt.getMemberAmount();j++){
			
			member=pt.getMember(j);
			memberid=BitTools.intToByteArray(member.getCharID());
			name=BitTools.stringToByteArray(member.getName());
			hp=BitTools.intToByteArray(member.getHp());
			mana=BitTools.intToByteArray(member.getMana());
			stam=BitTools.intToByteArray(member.getStamina());
			maxhp=BitTools.intToByteArray(member.getMaxhp());
			maxmana=BitTools.intToByteArray(member.getMaxmana());
			maxstam=BitTools.intToByteArray(member.getMaxstamina());
			
			for(int i=0;i<4;i++){
				partymembers[16+i]=memberid[i];
			}
			
			for(int i=0;i<name.length;i++){
				partymembers[20+i]=name[i];
			}
			for(int i=name.length;i<16;i++){
				partymembers[20+i]=(byte)0x00;
			}
			
			partymembers[38]=(byte)member.getCharacterClass();
			partymembers[40]=(byte)member.getLevel();
			
			for(int i=0;i<2;i++){
				partymembers[44+i]=hp[i];
				partymembers[48+i]=mana[i];
				partymembers[50+i]=stam[i];
				partymembers[52+i]=maxhp[i];
				partymembers[56+i]=maxmana[i];
				partymembers[58+i]=maxstam[i];
			}
			
			pt.sendToMembers(partymembers,null);
			
		}
		
	}
	
	public static byte[] getChangeLeaderPacket(Party pt, Character oldLeader, Character newLeader){
		
		byte[] cid=BitTools.intToByteArray(oldLeader.getCharID());
		byte[] cid2=BitTools.intToByteArray(newLeader.getCharID());
		byte[] member;
		
		byte changeleader[]=new byte[64];
		changeleader[0]=(byte)changeleader.length;
		changeleader[4]=(byte)0x04;
		changeleader[6]=(byte)0x25;
		
		changeleader[8]=(byte)0x01;
		changeleader[9]=(byte)0x43;
		changeleader[10]=(byte)0x65;
		changeleader[11]=(byte)0x28;
		
		for(int i=0;i<4;i++){
			changeleader[20+i]=cid2[i];
			changeleader[60+i]=cid[i];
		}
		
		for(int j=0;j<pt.getMemberAmount();j++){
			member=BitTools.intToByteArray(pt.getMember(j).getCharID());
			for(int i=0;i<4;i++){
				changeleader[28+j*4+i]=member[i];
			}
		}
		
		changeleader[16]=(byte)0x01;
		changeleader[19]=(byte)0x08;
		changeleader[24]=(byte)0x01;
		
		return changeleader;
		
	}
	
	public static byte[] getLeavePacket(Character leavingGuy, Party leftPt){
		
		byte[] cid=BitTools.intToByteArray(leavingGuy.getCharID());
		byte[] member;
		
		byte leavept[]=new byte[64];
		leavept[0]=(byte)leavept.length;
		leavept[4]=(byte)0x04;
		leavept[6]=(byte)0x24;
		
		leavept[8]=(byte)0x01;
		leavept[9]=(byte)0xd0;
		leavept[10]=(byte)0xc0;
		leavept[11]=(byte)0x2a;
		
		for(int i=0;i<4;i++){
			leavept[20+i]=cid[i];//leaving guy
			leavept[60+i]=cid[i];//banned by (himself)
		}
		
		for(int j=0;j<leftPt.getMemberAmount();j++){
			member=BitTools.intToByteArray(leftPt.getMember(j).getCharID());
			for(int i=0;i<4;i++){
				leavept[28+j*4+i]=member[i];
			}
		}
		
		leavept[16]=(byte)0x01;
		leavept[19]=(byte)0x08;
		leavept[24]=(byte)0x01;
		
		return leavept;
		
	}
	
	public static byte[] getBanPacket(Party pt, Character banningGuy, Character bannedGuy){
		
		byte[] cid=BitTools.intToByteArray(bannedGuy.getCharID());
		byte[] bannedid=BitTools.intToByteArray(bannedGuy.getCharID());
		byte[] member;
		
		byte leavept[]=new byte[64];
		leavept[0]=(byte)leavept.length;
		leavept[4]=(byte)0x04;
		leavept[6]=(byte)0x24;
		
		leavept[8]=(byte)0x01;
		leavept[9]=(byte)0x43;
		leavept[10]=(byte)0x65;
		leavept[11]=(byte)0x28;
		
		for(int i=0;i<4;i++){
			leavept[20+i]=bannedid[i];//leaving guy
			leavept[60+i]=cid[i];//banned by
		}
		
		for(int j=0;j<pt.getMemberAmount();j++){
			member=BitTools.intToByteArray(pt.getMember(j).getCharID());
			for(int i=0;i<4;i++){
				leavept[28+j*4+i]=member[i];
			}
		}
		
		leavept[16]=(byte)0x01;
		leavept[18]=(byte)0x01;
		leavept[19]=(byte)0x08;
		leavept[24]=(byte)0x01;
		
		return leavept;
		
	}
	
}
