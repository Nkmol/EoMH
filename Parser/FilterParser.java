package Parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class FilterParser extends Parser{
	
	public static LinkedList<LinkedList<Object>> getFilterlistFromTxt(String pathString){
		
		try{
			//FILE
			Path path = Paths.get(pathString);
			byte[] data = Files.readAllBytes(path);
			System.out.println("BYTES: "+data.length+" ,CREATE FILTER LISTS...");
			
			//GET ITEMS TO LIST
			LinkedList<Short> filterBytes=new LinkedList<Short>();
			short shortdata=0;
			LinkedList<Object> wholeFilter;
			LinkedList<LinkedList<Object>> filters=new LinkedList<LinkedList<Object>>();
			
			for(int i=0;i<data.length;i++){
				shortdata=(short)data[i];
				if(shortdata<0)
					shortdata+=256;
				filterBytes.add(shortdata);
			}
			
			String s;
			while(!filterBytes.isEmpty()){
				wholeFilter=new LinkedList<Object>();
				
				//CommandName
				s="";
				while(filterBytes.getFirst()!=44){
					s+=(char)filterBytes.removeFirst().shortValue();
				}
				filterBytes.removeFirst();
				wholeFilter.add(s);
				
				//SQLname
				s="";
				while(filterBytes.getFirst()!=44){
					s+=(char)filterBytes.removeFirst().shortValue();
				}
				filterBytes.removeFirst();
				wholeFilter.add(s);
				
				//MinValue
				s="";
				while(filterBytes.getFirst()!=44){
					s+=(char)filterBytes.removeFirst().shortValue();
				}
				filterBytes.removeFirst();
				wholeFilter.add(Long.parseLong(s));
				
				//MaxValue
				s="";
				while(filterBytes.getFirst()!=44){
					s+=(char)filterBytes.removeFirst().shortValue();
				}
				filterBytes.removeFirst();
				wholeFilter.add(Long.parseLong(s));
				
				//StandardValue
				s="";
				while(filterBytes.getFirst()!=44){
					s+=(char)filterBytes.removeFirst().shortValue();
				}
				filterBytes.removeFirst();
				wholeFilter.add(Long.parseLong(s));
				
				if(!filterBytes.isEmpty()){
					filterBytes.removeFirst();
					filterBytes.removeFirst();
				}
				
				filters.add(wholeFilter);
			}
			
			System.out.println("FOUND "+filters.size()+" FILTERS");
			
			return filters;
			
		}catch(Exception e){e.printStackTrace();}
		return null;
		
	}
	
public static LinkedList<LinkedList<Object>> getDescriptionlistFromTxt(String pathString){
		
		try{
			//FILE
			Path path = Paths.get(pathString);
			byte[] data = Files.readAllBytes(path);
			System.out.println("BYTES: "+data.length+" ,CREATE DESCRIPTION LISTS...");
			
			//GET ITEMS TO LIST
			LinkedList<Short> descBytes=new LinkedList<Short>();
			short shortdata=0;
			LinkedList<Object> wholeDesc;
			LinkedList<LinkedList<Object>> descriptions=new LinkedList<LinkedList<Object>>();
			
			for(int i=0;i<data.length;i++){
				shortdata=(short)data[i];
				if(shortdata<0)
					shortdata+=256;
				descBytes.add(shortdata);
			}
			
			String s;
			while(!descBytes.isEmpty()){
				wholeDesc=new LinkedList<Object>();
				
				//Value
				s="";
				while(descBytes.getFirst()!=44){
					s+=(char)descBytes.removeFirst().shortValue();
				}
				descBytes.removeFirst();
				wholeDesc.add(Integer.parseInt(s));
				
				//Description
				s="";
				while(descBytes.getFirst()!=44){
					s+=(char)descBytes.removeFirst().shortValue();
				}
				descBytes.removeFirst();
				wholeDesc.add(s);
				
				if(!descBytes.isEmpty()){
					descBytes.removeFirst();
					descBytes.removeFirst();
				}
				
				descriptions.add(wholeDesc);
			}
			
			System.out.println("FOUND "+descriptions.size()+" DESCRIPTIONS");
			
			return descriptions;
			
		}catch(Exception e){e.printStackTrace();}
		return null;
		
	}
	
	public static void main(String args[]){
		
		//---------- CHANGE THESE VALUES ----------
		
		//PATH
		String pathString1=System.getProperty("user.dir")+"/Data/itemFilters.txt";
		String pathString2=System.getProperty("user.dir")+"/Data/itemCategoryDescriptions.txt";
		
		//--------------------------


		System.out.println("START");
		
		getFilterlistFromTxt(pathString1);
		getDescriptionlistFromTxt(pathString2);
		
	}
	
}
