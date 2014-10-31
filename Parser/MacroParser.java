package Parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class MacroParser extends Parser{
	
	public static LinkedList<LinkedList<String>> getMacrolistFromTxt(String pathString){
		
		try{
			//FILE
			Path path = Paths.get(pathString);
			byte[] data = Files.readAllBytes(path);
			System.out.println("BYTES: "+data.length+" ,CREATE MACRO LISTS...");
			
			//GET ITEMS TO LIST
			LinkedList<Short> macroBytes=new LinkedList<Short>();
			short shortdata=0;
			LinkedList<String> wholeMacro;
			LinkedList<LinkedList<String>> macros=new LinkedList<LinkedList<String>>();
			
			for(int i=0;i<data.length;i++){
				shortdata=(short)data[i];
				if(shortdata<0)
					shortdata+=256;
				macroBytes.add(shortdata);
			}
			
			//remove headers
			while(macroBytes.removeFirst()!=10);
			
			while(!macroBytes.isEmpty()){
				wholeMacro=new LinkedList<String>();
				for(int i=0;i<3;i++){
					String s="";
					while(macroBytes.getFirst()!=32 && macroBytes.getFirst()!=44){
						s+=(char)macroBytes.removeFirst().shortValue();
					}
					while(macroBytes.getFirst()==32){
						macroBytes.removeFirst();
					}
					for(int j=0;j<2;j++)
						macroBytes.removeFirst();
					wholeMacro.add(s);
				}
				if(!macroBytes.isEmpty())
					macroBytes.removeFirst();
				macros.add(wholeMacro);
			}
			
			System.out.println("FOUND "+macros.size()+" MACROS");
			
			return macros;
			
		}catch(Exception e){e.printStackTrace();}
		return null;
		
	}
	
	public static void main(String args[]){
		
		//---------- CHANGE THESE VALUES ----------
		
		//PATH
		String pathString=System.getProperty("user.dir")+"/Data/Macro.txt";
		
		//--------------------------


		System.out.println("START");
		
		getMacrolistFromTxt(pathString);
		
	}
	
}
