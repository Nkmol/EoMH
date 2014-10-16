package Parser;

import java.sql.Connection;
import java.util.LinkedList;

import Database.InstallDAO;
import Database.SQLconnection;

public class MapParserSQL extends Parser{

	public static void parseMapsToSQL(InstallDAO dao, LinkedList<LinkedList<Object>> maps){
		
		System.out.println("Parsing maps into SQL");
		
		Connection con=new SQLconnection().getConnection();
		
		int id, xgrids, ygrids, xorigin, yorigin, areasize;
		String name;
		
		LinkedList<Object> map;
		
		while(!maps.isEmpty()){
			map=maps.removeFirst();
			id=(int)map.removeFirst();
			name=(String)map.removeFirst();
			xgrids=(int)map.removeFirst();
			ygrids=(int)map.removeFirst();
			xorigin=(int)map.removeFirst();
			yorigin=(int)map.removeFirst();
			areasize=(int)map.removeFirst();
			dao.addMap(con, id, name, xgrids, ygrids, areasize, xorigin, yorigin, 500);
		}
		
		System.out.println("Parsing into SQL done");
		
	}
	
}
