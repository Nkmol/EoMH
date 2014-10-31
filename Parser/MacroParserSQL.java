package Parser;

import java.sql.Connection;
import java.util.LinkedList;

import Database.InstallDAO;
import Database.SQLconnection;

public class MacroParserSQL extends Parser{

	public static void parseMacroToSQL(InstallDAO dao, LinkedList<LinkedList<String>> arguments){
		
		System.out.println("Parsing macros into SQL");
		
		Connection sql=new SQLconnection().getConnection();
		
		LinkedList<String> word;
		String name,password,content;
		int count=0;
		
		while(!arguments.isEmpty()){
			word=arguments.removeFirst();
			name=(String)word.removeFirst();
			password=(String)word.removeFirst();
			content=(String)word.removeFirst();
			
			dao.addMacro(sql,name, password, content);
			
			count++;
			
			if(count%100==0)
				System.out.print("["+(count)+"]");
		}
		
		System.out.println("Parsing into SQL done");
		
	}
	
}
