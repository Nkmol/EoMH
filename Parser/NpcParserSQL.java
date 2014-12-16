package Parser;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;

import Database.InstallDAO;
import Database.SQLconnection;

public class NpcParserSQL extends Parser{

	public static void parseNpcsToSQL(InstallDAO dao, LinkedList<ArrayList<Short>> npcs){
		
		System.out.println("Parsing npcs into SQL");
		
		Connection sql=new SQLconnection().getConnection();
		
		ArrayList<Short> npc;
		
		int id,module,items[];
		items=new int[60];
		
		int count=0;
		
		while(!npcs.isEmpty()){
				
			npc=npcs.removeFirst();
				
			id=convertBytesToSmall(npc,0);
			module=convertBytesToSmall(npc,118);
			for(int i=0;i<60;i++){
				items[i]=convertBytesToInteger(npc,128+i*12);
			}
						
			dao.createNpcDataEntry(sql, id, module, items);
					
				count++;
				
				if(count%100==0)
					System.out.print("["+(count)+"]");
			
		}
		
		System.out.println("Parsing into SQL done");
		
	}
	
}
