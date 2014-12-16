package Parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;

public class LvlexpParser extends Parser{
	
	public static LinkedList<Long> getLvlexplistFromTxt(String pathString){
		
		try{
			//FILE
			Path path = Paths.get(pathString);
			byte[] data = Files.readAllBytes(path);
			System.out.println("BYTES: "+data.length+" ,CREATE LVLEXP LISTS...");
			
			//GET LVLS TO LIST
			LinkedList<Short> lvlBytes=new LinkedList<Short>();
			LinkedList<Long> lvls=new LinkedList<Long>();
			int byteatm=0;
			short shortdata=0;
			long exp;
			ArrayList<Short> wholeLvl;
			
			for(int i=0;i<data.length;i++){
				shortdata=(short)data[i];
				if(shortdata<0)
					shortdata+=256;
				lvlBytes.add(shortdata);
			}
			
			while(byteatm<data.length){
				
				wholeLvl=new ArrayList<Short>();
				
				while(lvlBytes.getFirst()!=44){
					lvlBytes.removeFirst();
					byteatm++;
				}
				lvlBytes.removeFirst();
				byteatm++;
				
				while(!lvlBytes.isEmpty() && lvlBytes.getFirst()!=13){
					shortdata=lvlBytes.removeFirst();
					wholeLvl.add((short)(shortdata-48));
					byteatm++;
				}
				if(!lvlBytes.isEmpty())
					lvlBytes.removeFirst();
				if(!lvlBytes.isEmpty())
					lvlBytes.removeFirst();
				byteatm+=2;
				exp=0;
				for(int i=0;i<wholeLvl.size();i++){
					exp+=wholeLvl.get(i)*(Math.pow(10,wholeLvl.size()-i-1));
				}
				lvls.add(exp);
				System.out.println(exp);
				
			}
			
			//System.out.println("FOUND "+lvls.size()+" LVLS");
			return lvls;
			
		}catch(Exception e){e.printStackTrace();}
		return null;
		
	}
	
	public static void main(String args[]){
		
		//---------- CHANGE THESE VALUES ----------
		
		//PATH
		String pathString=System.getProperty("user.dir")+"/src/Data/lvls.txt";
		
		//--------------------------


		System.out.println("START");
		
		getLvlexplistFromTxt(pathString);
		
	}
	
}
