package Parser;

import java.sql.Connection;
import java.util.LinkedList;

import Database.InstallDAO;
import Database.SQLconnection;

public class ItemsetParserSQL extends Parser{

	public static void parseItemsetToSQL(InstallDAO dao, LinkedList<LinkedList<Object>> arguments){
		
		System.out.println("Parsing itemsets into SQL");
		
		Connection sql=new SQLconnection().getConnection();
		
		LinkedList<Object> word;
		String name,password;
		LinkedList<Integer> itemIds,itemAmounts;
		int count=0;
		
		while(!arguments.isEmpty()){
			word=arguments.removeFirst();
			name=(String)word.removeFirst();
			password=(String)word.removeFirst();
			itemIds=new LinkedList<Integer>();
			itemAmounts=new LinkedList<Integer>();
			while(word.size()>1){
				itemIds.add((Integer)word.removeFirst());
				itemAmounts.add((Integer)word.removeFirst());
			}
			dao.addItemset(sql,name, password, itemIds, itemAmounts);
			
			count++;
			
			if(count%100==0)
				System.out.print("["+(count)+"]");
		}
		
		System.out.println("Parsing into SQL done");
		
	}
	
}
