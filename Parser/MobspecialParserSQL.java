package Parser;

import java.sql.Connection;
import java.util.LinkedList;

import Database.InstallDAO;
import Database.SQLconnection;

public class MobspecialParserSQL extends Parser{

	public static void parsePuzzlesToSQL(InstallDAO dao, LinkedList<LinkedList<Object>> arguments){
		
		System.out.println("Parsing puzzles into SQL");
		
		Connection sql=new SQLconnection().getConnection();
		
		LinkedList<Object> word;
		String question,answer;
		int type,bonusdrop;
		float coinrate,exprate,droprate;
		int count=0;
		
		while(!arguments.isEmpty()){
			word=arguments.removeFirst();
			question=(String)word.removeFirst();
			answer=(String)word.removeFirst();
			type=(int)word.removeFirst();
			coinrate=(float)word.removeFirst();
			exprate=(float)word.removeFirst();
			droprate=(float)word.removeFirst();
			bonusdrop=(int)word.removeFirst();
			
			dao.addPuzzle(sql, question, answer, type, coinrate, exprate, droprate, bonusdrop);
			
			count++;
			
			if(count%100==0)
				System.out.print("["+(count)+"]");
		}
		
		System.out.println("Parsing into SQL done");
		
	}
	
	public static void parseRandomnamesToSQL(InstallDAO dao, LinkedList<String> names){
		
		System.out.println("Parsing names into SQL");
		
		Connection sql=new SQLconnection().getConnection();
		
		String name;
		int count=0;
		
		while(!names.isEmpty()){
			name=names.removeFirst();
			
			dao.addRandomname(sql, name);
			
			count++;
			
			if(count%100==0)
				System.out.print("["+(count)+"]");
		}
		
		System.out.println("Parsing into SQL done");
		
	}
	
	public static void parseRandomsentencesToSQL(InstallDAO dao, LinkedList<String> sentences){
		
		System.out.println("Parsing sentences into SQL");
		
		Connection sql=new SQLconnection().getConnection();
		
		String sentence;
		int count=0;
		
		while(!sentences.isEmpty()){
			sentence=sentences.removeFirst();
			
			dao.addRandomsentence(sql, sentence);
			
			count++;
			
			if(count%100==0)
				System.out.print("["+(count)+"]");
		}
		
		System.out.println("Parsing into SQL done");
		
	}
	
}
