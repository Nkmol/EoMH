package Parser;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;

import Database.InstallDAO;
import Database.SQLconnection;

public class MobSpawnsParserSQL extends Parser{

public static void parseMobspawnsToSQL(InstallDAO dao, LinkedList<ArrayList<Short>> mobspawns){
		
		System.out.println("Parsing mobspawns into SQL");
		
		Connection con=new SQLconnection().getConnection();
		ArrayList<Short> mobspawn;
		
		int map,id,amount;
		
		int count=0;
		
		while(!mobspawns.isEmpty()){
				
			mobspawn=mobspawns.removeFirst();
				
			map=convertBytesToByte(mobspawn,0);
			id=convertBytesToSmall(mobspawn,1);
			amount=convertBytesToByte(mobspawn,3);
			
			//messy circle spawn stuff
			float x,y,dx,dy,rx,ry,radius;
			x=convertBytesToFloat(mobspawn, 6);
			y=convertBytesToFloat(mobspawn, 10);
			dx=convertBytesToFloat(mobspawn, 14);
			dy=convertBytesToFloat(mobspawn, 18);
			rx=x+dx/2;
			ry=y+dy/2;
			radius=Math.min(Math.abs(dx), Math.abs(dy));
						
			dao.createMobSpawnEntry(con,map,id,amount,rx,ry,radius);
					
				count++;
				
				if(count%100==0)
					System.out.print("["+(count)+"]");
			
		}
		
		System.out.println("Parsing into SQL done");
		
	}
	
}
