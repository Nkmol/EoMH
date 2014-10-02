package chat.chatCommandHandlers;


import Player.Character;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Tools.BitTools;
import Tools.StringTools;
import Connections.Connection;
import chat.ChatCommandExecutor;

public class LvlUp implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public LvlUp(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	public void execute(String[] parameters, Connection source) {
		    System.out.println("Received chat command to change lvl and exp");
		    
			Character cur = ((PlayerConnection)source).getActiveCharacter();
			
			if(!GameMaster.canUseCommand(cur, needsCommandPower)){
				System.out.println("Not enough command power");
				return;
			}
		    
		    if (parameters.length>0) 
		    {
		      byte[] cid = BitTools.intToByteArray(cur.getCharID());
		      
		      int lvlInt=1;
		      long expLong=0;
		      
		      if (StringTools.isInteger(parameters[0]))
		      {
		       if (parameters[0].length()>3 || Integer.parseInt(parameters[0])>167)
		    	   lvlInt=167;
		       else
		    	   lvlInt=Integer.parseInt(parameters[0]);
		      }
		      else
		      System.out.println("Bad parameters [0]: Lvl stays the same");
		      
		      if (parameters.length>1)
		      {
		       if (StringTools.isInteger(parameters[1]))
		       {
		        expLong=Long.parseLong(parameters[1]);
		       }
		      }
		      else
		      System.out.println("Bad parameters [1]: Exp stays the same");
		      
		      cur.setLevel(lvlInt);
		      cur.setExp(expLong);
		      cur.refreshStatPoints();
		      cur.refreshSkillPoints();
		      cur.calculateCharacterStats();
		      
		      //int expFirstInt=(int)(expLong%1838539174);
		      //int expLastInt=(int)(expLong/1838539174);
		      
		      byte[] hp = BitTools.intToByteArray(cur.getMaxhp());
		      byte[] mana = BitTools.intToByteArray(cur.getMaxmana());
		      
		      byte[] lvl = BitTools.intToByteArray(lvlInt);
		      //byte[] expFirst = BitTools.intToByteArray(expFirstInt);
		      //byte[] expLast = BitTools.intToByteArray(expLastInt);
		      byte[] cpleft = BitTools.intToByteArray(cur.getStatPoints());
		      byte[] sp = BitTools.intToByteArray(cur.getSkillPoints());
		      
		      byte[] lvlpckt = new byte[44];
		      lvlpckt[0] = (byte)lvlpckt.length;
		      lvlpckt[4] = (byte)0x05;
		      lvlpckt[6] = (byte)0x20;
		      lvlpckt[8] = (byte)0x01;
		      lvlpckt[9] = (byte)0x39;
		      lvlpckt[10] = (byte)0x07;
		      lvlpckt[11] = (byte)0x08;
		      
		      for(int i=0;i<2;i++){
		    	  lvlpckt[16+i] = lvl[i];
			      lvlpckt[18+i] = cpleft[i];
			      lvlpckt[20+i] = sp[i];
		      }
		      
		      //exp in lvlup packet is pretty fcked up lol, needs different base values
		      for(int i=0;i<4;i++){
		    	  lvlpckt[12+i] = cid[i];
		    	  lvlpckt[24+i] = hp[i];
			      lvlpckt[28+i] = mana[i];
			      //lvlpckt[32+i] = expLast[i];
			      //lvlpckt[36+i] = expFirst[i];
		      }
		      
		      byte[] exp=BitTools.longToByteArray(expLong);
		      
		      byte[] exppckt=new byte[24];
				
		      exppckt[0]=(byte)exppckt.length;
		      exppckt[4]=(byte)0x05;
		      exppckt[6]=(byte)0x0b;
		      exppckt[8]=(byte)0x01;
			
		      for(int i=0;i<4;i++){
		    	  exppckt[12+i]=cid[i];
		      }
				
		      for(int i=0;i<8;i++){
		    	  exppckt[16+i]=exp[i];
		      }
				
		      source.addWrite(lvlpckt);
		      source.addWrite(exppckt);
		      cur.sendToMap(lvlpckt);
		    }
		    else
		    System.out.println("No parameters");
		  }
	
}
