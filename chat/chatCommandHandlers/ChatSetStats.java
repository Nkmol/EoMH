package chat.chatCommandHandlers;

import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Player.Character;
import Tools.StringTools;
import Tools.BitTools;
import Connections.Connection;
import chat.ChatCommandExecutor;

public class ChatSetStats implements ChatCommandExecutor {
  
	private int needsCommandPower;
	
	public ChatSetStats(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
  
  public void execute(String[] parameters, Connection source) {
    System.out.println("Received chat command to set the Players Stats!");
    
	Character cur = ((PlayerConnection)source).getActiveCharacter();
	
	if(!GameMaster.canUseCommand(cur, needsCommandPower)){
		System.out.println("Not enough command power");
		return;
	}
    
    final String[] parametersname=new String[6];
    parametersname[0]="Str";
    parametersname[1]="Dex";
    parametersname[2]="Vit";
    parametersname[3]="Int";
    parametersname[4]="Agi";
    parametersname[5]="Stat Points";
    
    byte[] cid = BitTools.intToByteArray(cur.getCharID());
    short[] curstat=cur.getStats();
    int[] intparameters=new int[6];
    
    //Standards
    for (int i=0; i<5; i++) 
    intparameters[i]=curstat[i];
    intparameters[5]=cur.getStatPoints();
    
    for (int i=0; i<6; i++)
    {
     if (parameters.length>i)
     {
       if (StringTools.isInteger(parameters[i]))
       {
        if (parameters[i].length()>4)
        intparameters[i]=9999;
        else
        intparameters[i]=Integer.parseInt(parameters[i]);
       }
       else
       System.out.println("Bad parameters ["+i+"]: "+parametersname[i]+" stays the same");
     }
     else
     System.out.println("No parameters ["+i+"]: "+parametersname[i]+" stays the same");
    }
    
    for (int i=0; i<5; i++)
    {
      System.out.println("Old "+parametersname[i]+" was "+curstat[i]); 
      curstat[i]=(short)intparameters[i];
      System.out.println("Updated "+parametersname[i]+" to "+curstat[i]);
    }
    cur.setStats(curstat);
    cur.calculateCharacterStats();
    
    System.out.println("Old "+parametersname[5]+" was "+cur.getStatPoints());
    cur.setStatPoints(intparameters[5]);
    System.out.println("Updated "+parametersname[5]+" to "+intparameters[5]);
    
    byte[] str = BitTools.intToByteArray(intparameters[0]);
    byte[] dex = BitTools.intToByteArray(intparameters[1]);
    byte[] vit = BitTools.intToByteArray(intparameters[2]);
    byte[] itl = BitTools.intToByteArray(intparameters[3]);
    byte[] agi = BitTools.intToByteArray(intparameters[4]);
    byte[] cpleft = BitTools.intToByteArray(intparameters[5]);
    
    byte[] statpckt = new byte[32];
    statpckt[0] = (byte)statpckt.length;
    statpckt[4] = (byte)0x04;
    statpckt[6] = (byte)0x1D;
    statpckt[8] = (byte)0x01;
    
    //CharID
    for(int i=0;i<4;i++)
    statpckt[12+i] = cid[i];
    
    statpckt[16] = (byte)0x01;
    
    //Str
    statpckt[18] = str[0];
    statpckt[19] = str[1];
    
    //Dex
    statpckt[20] = dex[0];
    statpckt[21] = dex[1];
    
    //Vit
    statpckt[22] = vit[0];
    statpckt[23] = vit[1];
    
    //Int
    statpckt[24] = itl[0];
    statpckt[25] = itl[1];
    
    //Agi
    statpckt[26] = agi[0];
    statpckt[27] = agi[1];
    
    //Stat Points
    statpckt[28] = cpleft[0];
    statpckt[29] = cpleft[1];
    
    statpckt[30] = (byte)0x40;
    statpckt[31] = (byte)0x2A;
    
    //set char stats
    short[] stats=new short[5];
    for(int i=0;i<5;i++)
    	stats[i]=(short)intparameters[i];
    cur.setCStats(stats);
    cur.setStatPoints(intparameters[5]);
    cur.calculateCharacterStats();
    source.addWrite(statpckt);
  }  
}
