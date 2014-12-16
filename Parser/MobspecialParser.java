package Parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class MobspecialParser extends Parser{
	
public static LinkedList<LinkedList<LinkedList<Short>>> getPuzzlelistFromTxt(String pathString){
		
		try{
			//FILE
			Path path = Paths.get(pathString);
			byte[] data = Files.readAllBytes(path);
			System.out.println("BYTES: "+data.length+" ,CREATE PUZZLE LISTS...");
			
			//GET ITEMS TO LIST
			LinkedList<Short> itemsetBytes=new LinkedList<Short>();
			LinkedList<LinkedList<LinkedList<Short>>> puzzles=new LinkedList<LinkedList<LinkedList<Short>>>();
			short shortdata=0;
			LinkedList<LinkedList<Short>> wholePuzzle;
			LinkedList<Short> wholeObject;
			
			for(int i=0;i<data.length;i++){
				shortdata=(short)data[i];
				if(shortdata<0)
					shortdata+=256;
				itemsetBytes.add(shortdata);
			}
			
			//remove headers
			while(itemsetBytes.removeFirst()!=10);
			
			while(!itemsetBytes.isEmpty()){
				wholePuzzle=new LinkedList<LinkedList<Short>>();
				while(!itemsetBytes.isEmpty() && itemsetBytes.getFirst()!=10){
					wholeObject=new LinkedList<Short>();
					while(itemsetBytes.getFirst()!=44){
						wholeObject.add(itemsetBytes.removeFirst());
					}
					for(int i=0;i<2;i++){
						itemsetBytes.removeFirst();
					}
					wholePuzzle.add(wholeObject);
				}
				puzzles.add(wholePuzzle);
				if(!itemsetBytes.isEmpty()){
					itemsetBytes.removeFirst();
				}
			}
			
			System.out.println("FOUND "+puzzles.size()+" PUZZLES");
			
			return puzzles;
			
		}catch(Exception e){e.printStackTrace();}
		return null;
		
	}
	
	public static LinkedList<LinkedList<Object>> structurize(LinkedList<LinkedList<LinkedList<Short>>> puzzles){
		
		LinkedList<LinkedList<Short>> wholePuzzle;
		LinkedList<Short> word;
		String s;
		LinkedList<Object> finalObjects;
		LinkedList<LinkedList<Object>> newPuzzles=new LinkedList<LinkedList<Object>>();
		
		while(!puzzles.isEmpty()){
			wholePuzzle=puzzles.removeFirst();
			finalObjects=new LinkedList<Object>();
			
			//convert all to strings first
			int i=0;
			while(!wholePuzzle.isEmpty()){
				word=wholePuzzle.removeFirst();
				s="";
				while(!word.isEmpty())
					s+=(char)word.removeFirst().shortValue();
				s=s.trim();
				switch(i){
					case 0:{
						finalObjects.add(s);
						break;
					}
					case 1:{
						finalObjects.add(s);
						break;
					}
					case 2:{
						finalObjects.add(Integer.parseInt(s));
						break;
					}
					case 3:{
						finalObjects.add(Float.parseFloat(s));
						break;
					}
					case 4:{
						finalObjects.add(Float.parseFloat(s));
						break;
					}
					case 5:{
						finalObjects.add(Float.parseFloat(s));
						break;
					}
					case 6:{
						finalObjects.add(Integer.parseInt(s));
						break;
					}
					default:{
						break;
					}
				}
				i++;
			}
			
			newPuzzles.add(finalObjects);
		}
		
		return newPuzzles;
		
	}
	
	public static LinkedList<String> getNamelistFromTxt(String pathString, String description){
		try{
			LinkedList<String> names=new LinkedList<>();
			//FILE
			System.out.println("CREATE "+description+" LISTS...");
			BufferedReader br = new BufferedReader(new FileReader(pathString));
			try {
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();
				names.add(line);

				while (line != null) {
					sb.append(line);
					sb.append(System.lineSeparator());
					line = br.readLine();
					names.add(line);
				}
				
				names.removeLast();
				
				return names;
			} finally {
				br.close();
			}
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	public static void main(String args[]){
		
		//---------- CHANGE THESE VALUES ----------
		
		//PATH
		String pathString1=System.getProperty("user.dir")+"/Data/Puzzles.txt";
		
		//--------------------------


		System.out.println("START");
		
		structurize(getPuzzlelistFromTxt(pathString1));
		
	}
	
}
