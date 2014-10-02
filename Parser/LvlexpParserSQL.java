package Parser;

import java.sql.Connection;
import java.util.LinkedList;

import Database.InstallDAO;
import Database.SQLconnection;

public class LvlexpParserSQL extends Parser{

	public static void parseLvlexpsToSQL(InstallDAO dao, LinkedList<Long> lvls){
		
		System.out.println("Parsing lvls into SQL");
		
		Connection con=new SQLconnection().getConnection();
		
		long exp;
		int lvlAmount=168;
			
		for(int i=0;i<lvlAmount;i++){
			if(!lvls.isEmpty())
				exp=lvls.removeFirst();
			else
				exp=0;
		
			dao.addLvl(con,i,exp);
		}
		
		System.out.println("Parsing into SQL done");
		
	}
	
}
