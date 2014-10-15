package Parser;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;

import Database.InstallDAO;
import Database.SQLconnection;

public class NpcSpawnsParserSQL extends Parser{

public static void parseNpcspawnsToSQL(InstallDAO dao, LinkedList<ArrayList<Short>> npcspawns){
		
		System.out.println("Parsing npcspawns into SQL");
		
		Connection con=new SQLconnection().getConnection();
		ArrayList<Short> npcspawn;
		
		int map,id;
		float x,y;
		
		int count=0;
		
		while(!npcspawns.isEmpty()){
				
			npcspawn=npcspawns.removeFirst();
				
			map=convertBytesToByte(npcspawn,0);
			id=convertBytesToSmall(npcspawn,1);
			x=convertBytesToFloat(npcspawn, 5);
			y=convertBytesToFloat(npcspawn, 9);
						
			dao.createNpcSpawnEntry(con,map,id,x,y);
					
				count++;
				
				if(count%100==0)
					System.out.print("["+(count)+"]");
			
		}
		
		System.out.println("Parsing into SQL done");
		
	}
	
}
