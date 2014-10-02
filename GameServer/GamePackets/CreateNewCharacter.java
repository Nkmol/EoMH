package GameServer.GamePackets;

import item.ItemInInv;
import item.inventory.Inventory;

import java.nio.ByteBuffer;

import Connections.Connection;
import Database.CharacterDAO;
import Encryption.Decryptor;
import Player.PlayerConnection;
import Player.Character;


public class CreateNewCharacter implements Packet {


	public void execute(ByteBuffer buff) {

	}

	public byte[] returnWritableByteBuffer(byte[] buffyTheVampireSlayer, Connection con) {
		
		byte[] decrypted = new byte[(buffyTheVampireSlayer[0] & 0xFF)-8];

		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(buffyTheVampireSlayer[i+8] & 0xFF);
		}
		decrypted = Decryptor.Decrypt(decrypted);
		
		//-----NAME-----
		byte[] characterName = new byte[16]; //if memory serves - max name length = 13 characters
		byte[] realLengthname = null;
		for(int i=0;i<characterName.length;i++) {
			characterName[i] = decrypted[i];
			if(decrypted[i] == 0x00) {
				realLengthname = new byte[i];
				break; 
			}
		}
		for(int i=0;i<realLengthname.length;i++) {
			realLengthname[i] = characterName[i];
		}
		String name = new String(realLengthname);
		if(CharacterDAO.doesCharNameExist(name)){
			final byte[] fail = new byte[] { //static packet to respond to clients' character creation request failed by name
					(byte)0x14, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0xce, 
					(byte)0x9f, (byte)0x2a, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00 
			};
			return fail;
		}
		
		//--------------
		
		final byte[] createNewCharacter = new byte[] { //static packet to respond to clients' character creation request
				(byte)0x14, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x06, (byte)0x00, (byte)0x01, (byte)0x01, 
				(byte)0x12, (byte)0x2b, (byte)0x00, (byte)0xc0, (byte)0xb7, (byte)0xc4, (byte)0x00, (byte)0xe0, (byte)0x21, (byte)0x45 
		};
		
		byte characterClass;
		byte[] stats;
		short statusPointsleft;
		//check that stat points are right
		if(decrypted[26]+decrypted[28]+decrypted[30]+decrypted[32]+decrypted[34]+decrypted[36]==55){
			stats= new byte[5];
			for(int i=0;i<5;i++){
				stats[i]=decrypted[i*2+26];
			}
			statusPointsleft = decrypted[36];
		}else{
			stats = new byte[] { (byte)0x0A, (byte)0x0A, (byte)0x0A, (byte)0x0A, (byte)0x0A };
			statusPointsleft = 5;
		}

		short face=decrypted[18];
		
		short skillpoints=0;
		
		/*
		 * Character bytes:
		 * 01 = Warrior
		 * 02 = Assassin
		 * 03 = Mage
		 * 04 = Monk
		 */
		characterClass = decrypted[decrypted.length-14]; 
		
		if(decrypted[36] <= 0x05) { //remember to make sure that no more than 5 points can be left after creating character
			statusPointsleft = decrypted[decrypted.length-2]; 
		} else {
			//The player is cheating. Do something about it here
		}
		
		stats[0] = decrypted[decrypted.length-4]; //INT
		stats[1] = decrypted[decrypted.length-6]; //AGI
		stats[2] = decrypted[decrypted.length-8]; //VIT
		stats[3] = decrypted[decrypted.length-10];//DEX
		stats[4] = decrypted[decrypted.length-12];//STR
		
		int spawnX = -1660, spawnY = 2344; //Coordinates the new character will spawn at
		
		Character returnedCharacter = CharacterDAO.addAndReturnNewCharacter(name, stats, characterClass, statusPointsleft, skillpoints, ((PlayerConnection)con).getPlayer(), spawnX, spawnY, face);
	
		if(returnedCharacter != null) {
			((PlayerConnection)con).getPlayer().addCharacter(returnedCharacter);
			
			//-----STANDARD EQUIPMENT-----
			
			returnedCharacter.equipStandardEquipment();
			
			//-----STANDARD INVENTORY-----
			
			Inventory inv=returnedCharacter.getInventory();
			//tags:213062726/7
			ItemInInv it1=new ItemInInv(213010001);
			ItemInInv it2=new ItemInInv(213020001);
			it1.setAmount(10000);
			it2.setAmount(10000);
			inv.addItem(7, 0, it1);
			inv.addItem(7, 1, it2);
			switch(characterClass){
				case 1:{
					inv.addItem(0, 0, new ItemInInv(201111001));
					inv.addItem(1, 0, new ItemInInv(201112001));
					inv.addItem(2, 0, new ItemInInv(201113001));
					break;
				}
				case 2:{
					inv.addItem(0, 0, new ItemInInv(201221001));
					inv.addItem(1, 0, new ItemInInv(201222001));
					inv.addItem(2, 0, new ItemInInv(201223001));
					break;
				}
				case 3:{
					inv.addItem(0, 0, new ItemInInv(201131001));
					inv.addItem(2, 0, new ItemInInv(201132001));
					inv.addItem(4, 0, new ItemInInv(201133001));
					break;
				}
				case 4:{
					inv.addItem(0, 0, new ItemInInv(201141001));
					inv.addItem(1, 0, new ItemInInv(201142001));
					inv.addItem(3, 0, new ItemInInv(201143001));
					break;
				}
				default:{
					
					break;
				}
			}
			
		}
		returnedCharacter.getInventory().saveInv(returnedCharacter);
		
		return createNewCharacter;
	}

	
}
