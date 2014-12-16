package Parser;

import java.sql.Connection;
import java.util.LinkedList;

import Database.InstallDAO;
import Database.SQLconnection;

public class FilterParserSQL extends Parser{

	public static void parseFilterToSQL(InstallDAO dao, LinkedList<LinkedList<Object>> arguments, String category){
		
		System.out.println("Parsing filters into SQL");
		
		Connection sql=new SQLconnection().getConnection();
		
		LinkedList<Object> word;
		String command,sqlName;
		int minValue,maxValue,standardValue;
		int count=0;
		
		while(!arguments.isEmpty()){
			word=arguments.removeFirst();
			command=(String)word.removeFirst();
			sqlName=(String)word.removeFirst();
			minValue=(Integer)word.removeFirst();
			maxValue=(Integer)word.removeFirst();
			standardValue=(Integer)word.removeFirst();
			
			dao.addFilter(sql, category, command, sqlName, minValue, maxValue, standardValue);
			
			count++;
			
			if(count%100==0)
				System.out.print("["+(count)+"]");
		}
		
		System.out.println("Parsing into SQL done");
		
	}
	
	public static void parseDescriptionToSQL(InstallDAO dao, LinkedList<LinkedList<Object>> arguments, String category){
		
		System.out.println("Parsing descriptions into SQL");
		
		Connection sql=new SQLconnection().getConnection();
		
		LinkedList<Object> word;
		String description;
		int descValue;
		int count=0;
		
		while(!arguments.isEmpty()){
			word=arguments.removeFirst();
			descValue=(Integer)word.removeFirst();
			description=(String)word.removeFirst();
			
			dao.addDescription(sql, category, descValue, description);
			
			count++;
			
			if(count%100==0)
				System.out.print("["+(count)+"]");
		}
		
		System.out.println("Parsing into SQL done");
		
	}
	
}
