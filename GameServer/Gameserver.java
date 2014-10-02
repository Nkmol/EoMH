package GameServer;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import logging.ServerLogger;
import Connections.Connection;
import PacketHandler.PacketHandler;
import ServerCore.SelectorThread;
import ServerCore.ServerFacade;
import Database.AccountDAO;
import Database.CharacterDAO;
import Encryption.Decryptor;
import GameServer.GamePackets.*;
import Player.Player;
import Player.PlayerConnection;
import Player.Character;
import ThreadPool.PacketHandlerPool;
import Tools.BitTools;



public class Gameserver implements PacketHandler {

	private Map<Integer, Packet> packetsByHeader = new HashMap<Integer, Packet>();
	private ServerFacade sf;
	private ServerLogger logging = ServerLogger.getInstance();
	private PacketHandlerPool tPool = PacketHandlerPool.getInstance();

	public void initialize(ServerFacade sf) {
 		this.sf = sf;
		this.packetsByHeader.put(Integer.valueOf(1),	new Ping()); 				//Respond to client ping
		this.packetsByHeader.put(Integer.valueOf(666), 	new Quit()); 				//client quits the game(clean quit)
		this.packetsByHeader.put(Integer.valueOf(672), 	new CreateNewCharacter()); 	//handle character creation requests
		this.packetsByHeader.put(Integer.valueOf(673), 	new CharacterDelete());		//handle character delete requests
		this.packetsByHeader.put(Integer.valueOf(675), 	new SelectedCharacter(false));//Respond to selected character logging in game world
		this.packetsByHeader.put(Integer.valueOf(680), 	new SelectedCharacter(true));//Spawn In VV
		this.packetsByHeader.put(Integer.valueOf(1332), new ReturnToSelection()); 	//player returns to character selection screen
		this.packetsByHeader.put(Integer.valueOf(1335), new Revive()); 				//player revives
		this.packetsByHeader.put(Integer.valueOf(1337), new UsableItem()); 			//activate item in skillbar
		this.packetsByHeader.put(Integer.valueOf(1338), new AtkMode()); 			//atk mode on/off
		this.packetsByHeader.put(Integer.valueOf(1339), new Chat()); 				//handle chat messages sent by client
		this.packetsByHeader.put(Integer.valueOf(1344), new Equip());				//equip an item
		this.packetsByHeader.put(Integer.valueOf(1345), new LocationSync()); 		//location sync packet
		this.packetsByHeader.put(Integer.valueOf(1346), new DropItem()); 			//drop item from inv to ground	
		this.packetsByHeader.put(Integer.valueOf(1347), new Pick()); 				//pick item from ground
		this.packetsByHeader.put(Integer.valueOf(1348), new InventoryManagement()); //move item in inventory(including unequipping)
		this.packetsByHeader.put(Integer.valueOf(1349), new SkillIntoBar()); 		//move item/skill into the slots
		this.packetsByHeader.put(Integer.valueOf(1353), new DeleteItem());			//delete item from inventory
		this.packetsByHeader.put(Integer.valueOf(1361), new SetStats());			//set stats in character window
		this.packetsByHeader.put(Integer.valueOf(1362), new CheckEquipment());		//check equip
		this.packetsByHeader.put(Integer.valueOf(1367), new PartyJoinPacket());		//join party
		this.packetsByHeader.put(Integer.valueOf(1368), new PartyLeavePacket());	//leave party
		this.packetsByHeader.put(Integer.valueOf(1369), new PartyChangeLeaderPacket());//change leader in party
		this.packetsByHeader.put(Integer.valueOf(1373), new LearnSkill());			//learn skill
		this.packetsByHeader.put(Integer.valueOf(1374), new StartDuelPacket());		//start duel
		this.packetsByHeader.put(Integer.valueOf(1384), new CastSkill());			//cast skill
		this.packetsByHeader.put(Integer.valueOf(1387), new VendorState());			//Opens or Closes vendor shop
		this.packetsByHeader.put(Integer.valueOf(1388), new VendorOpen());			//Open someones vendor shop
		this.packetsByHeader.put(Integer.valueOf(1389), new VendorItem());			//Adds or remove a vendor item
		this.packetsByHeader.put(Integer.valueOf(1390), new VendorBuyItem());       //Buy item from vendor player
	}


	public void processPacket(ByteBuffer buf, SocketChannel chan) {
	}

	@Override
	public void newConnection(SocketChannel chan) {
		// TODO Auto-generated method stub
		
	}


	public void newConnection(Connection con) {
		/*
		 *  Handle all your authentication logic in this block
		 */
		//String ip = con.getIp();
		//if(ip.equals("127.0.0.1")) { //in case it's localhost - default to localhost account			
				SocketChannel currentChan = con.getChan();
				SelectorThread sp = con.getRegisteredSelector();
				this.sf.getConnections().replace(currentChan, new PlayerConnection(currentChan, 100, 500, sp)); //upon succesful authentication - Connection becomes a PlayerConnection
				PlayerConnection plc = (PlayerConnection)this.sf.getConnections().get(currentChan);
				//Player tmpl = AccountDAO.authenticate("localhost", "localhost"); //look for account entry with localhost:localhost as username:password
				Player tmpl = AccountDAO.getPlayerByIp(con.getIp());
				//acc not found
				if(tmpl==null){
					AccountDAO.createAccount(con.getIp(), con.getIp(), "password", 1);
					tmpl = AccountDAO.getPlayerByIp(con.getIp());
				}
				
				if(tmpl != null) {
					tmpl.setChannel(currentChan);
					plc.setPlayer(tmpl);
					plc.addWrite(Login.authSuccess);
					ArrayList<Character> characters = CharacterDAO.loadCharacters(tmpl.getAccountID());
					
					if(characters != null) {
						tmpl.setCharacters(characters);
						Iterator<Character> citer = characters.iterator();
						ByteBuffer all = ByteBuffer.allocate((characters.size()*653)+8+3);
						byte[] size = BitTools.shortToByteArray((short)all.capacity());
						all.put(size);
						all.put(new byte[] { (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x04, (byte)0x00 });
						
						all.put(new byte[] { (byte)0x01, (byte)0x01, (byte)0x01 });
						
						Character ctm = citer.next();
						ctm.setPlayer(plc.getPlayer());
						
						byte[] tmp = ctm.initCharPacket();
						for(int i=0;i<tmp.length;i++) {
							all.put(tmp[i]);
						}
						
						while(citer.hasNext()) {

							Character ctmp = citer.next();
							ctmp.setPlayer(plc.getPlayer());
															
							byte[] tmpb = ctmp.initCharPacket();
							for(int i=0;i<tmpb.length;i++) {
								all.put(tmpb[i]);
							}
							
							all.put(10, (byte)((all.get(10)*2)+1)); //required increment depending on amount of characters on account
						}
						
						all.flip();
						byte[] meh = new byte[all.limit()];
						all.get(meh);
						plc.addWrite(meh);
						plc.addWrite(Login.account);
					} else {
						plc.addWrite(Login.account);
					}
					
				} else {
					con.addWrite(Login.authFail);
					con.threadSafeDisconnect();
					this.logging.logMessage(Level.INFO, this, "Authentication failed for connection: " + con.getIp());
				}
		/*		
		} else { //in any other case assume failed authentication
			con.addWrite(ByteBuffer.wrap(Login.authFail));
			con.threadSafeDisconnect();
			this.logging.logMessage(Level.INFO, this, "Authentication failed for connection: " + con.getIp());
		}*/
	}


	@Override
	public ByteBuffer processPacket(ByteBuffer boss) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void processPacket(ByteBuffer buf, Connection con) {
		// TODO Auto-generated method stub
		
	}

	//----------delete later, just for print----------
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
		byte[] b = new byte[8];
		
		for(int i=0;i<b.length;i++) {
			b[i] = (byte)(bytes[i] & 0xFF);
		}
		
	    char[] hexChars = new char[b.length * 2];
	    for ( int j = 0; j < b.length; j++ ) {
	        int v = b[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	public static String bytesToHexDecrypted(byte[] bytes) {
		
		byte[] decrypted = new byte[(bytes[0] & 0xFF)-8];
		
		for(int i=0;i<decrypted.length;i++) {
			decrypted[i] = (byte)(bytes[i+8] & 0xFF);
		}
		
		decrypted = Decryptor.Decrypt(decrypted);
		
	    char[] hexChars = new char[decrypted.length * 2];
	    for ( int j = 0; j < decrypted.length; j++ ) {
	        int v = decrypted[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	//--------------------
	
	
	public void processList(Connection con) {
		LinkedBlockingQueue<byte[]> tmpQQ = con.getReadBuffer();
		class nonBlockingProcess implements Runnable {
			private Connection con;
			private LinkedBlockingQueue<byte[]> tmpQQ;
			private Map<Integer, Packet> packetsByHeader;
			
			public nonBlockingProcess(Connection con, LinkedBlockingQueue<byte[]> q, Map<Integer, Packet> packets) {
				this.con = con;
				this.tmpQQ = q;
				this.packetsByHeader = packets;
			}
			
			@Override
			public void run() {
				synchronized(tmpQQ) {
					while(!tmpQQ.isEmpty()) {
						byte[] buf = tmpQQ.poll();
						if(buf[7] == 0x00) {
							//print the packets, EEEVIL development
							System.out.println();
							System.out.print(bytesToHex(buf));
							System.out.print(bytesToHexDecrypted(buf));
							System.out.println();
							int header = (int)((buf[4] & 0xFF)*666) + (int)(buf[6] & 0xFF); //get packet type from header (multiplying by 666 just cause we can :)
							if(this.packetsByHeader.containsKey(Integer.valueOf(header))) {
								try{
								byte[] tmp = this.packetsByHeader.get(Integer.valueOf(header)).returnWritableByteBuffer(buf, con);
								if(tmp != null) {	
									con.addWrite(tmp);
								}
								}catch(PaketException e){
									//just a little response, so client packet doesnt freeze
									byte msg[]=new byte[20];
									msg[0] = (byte)0x14;
									msg[4] = (byte)0x04;
									msg[6] = (byte)0x15;
									byte[] chid=BitTools.intToByteArray(((PlayerConnection)con).getActiveCharacter().getuid());
									for(int i=0;i<4;i++){
										msg[12+i]=chid[i];
									}
									msg[16] = (byte)0x01;
									msg[18] = (byte)0x01;
									msg[19] = (byte)0xff;
									
									con.addWrite(msg);
								}
							}
						} else {
							try{
							con.addWrite(this.packetsByHeader.get(Integer.valueOf(1)).returnWritableByteBuffer(buf, con)); //ping
							}catch(PaketException e){}
						}				
					}
				}	
			}			
		}
		this.tPool.executeProcess(new nonBlockingProcess(con, tmpQQ, this.packetsByHeader));
	}

}
