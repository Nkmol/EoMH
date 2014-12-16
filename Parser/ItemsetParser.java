package Parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class ItemsetParser extends Parser{
	
	public static LinkedList<LinkedList<LinkedList<Short>>> getItemsetlistFromTxt(String pathString){
		
		try{
			//FILE
			Path path = Paths.get(pathString);
			byte[] data = Files.readAllBytes(path);
			System.out.println("BYTES: "+data.length+" ,CREATE ITEMSET LISTS...");
			
			//GET ITEMS TO LIST
			LinkedList<Short> itemsetBytes=new LinkedList<Short>();
			LinkedList<LinkedList<LinkedList<Short>>> itemsets=new LinkedList<LinkedList<LinkedList<Short>>>();
			short shortdata=0;
			LinkedList<LinkedList<Short>> wholeItemset;
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
				wholeItemset=new LinkedList<LinkedList<Short>>();
				while(!itemsetBytes.isEmpty() && itemsetBytes.getFirst()!=10){
					wholeObject=new LinkedList<Short>();
					while(itemsetBytes.getFirst()!=32 && itemsetBytes.getFirst()!=44){
						wholeObject.add(itemsetBytes.removeFirst());
					}
					while(itemsetBytes.getFirst()==32){
						itemsetBytes.removeFirst();
					}
					for(int i=0;i<2;i++){
						itemsetBytes.removeFirst();
					}
					wholeItemset.add(wholeObject);
				}
				itemsets.add(wholeItemset);
				if(!itemsetBytes.isEmpty()){
					itemsetBytes.removeFirst();
				}
			}
			
			System.out.println("FOUND "+itemsets.size()+" ITEMSETS");
			
			return itemsets;
			
		}catch(Exception e){e.printStackTrace();}
		return null;
		
	}
	
	public static LinkedList<LinkedList<Object>> structurize(LinkedList<LinkedList<LinkedList<Short>>> itemsets){
		
		LinkedList<LinkedList<Short>> wholeItemset;
		LinkedList<Short> word;
		String s;
		LinkedList<Object> finalObjects;
		LinkedList<LinkedList<Object>> newItemsets=new LinkedList<LinkedList<Object>>();
		
		while(!itemsets.isEmpty()){
			wholeItemset=itemsets.removeFirst();
			finalObjects=new LinkedList<Object>();
			
			//first 2 strings
			for(int i=0;i<2;i++){
				word=wholeItemset.removeFirst();
				s="";
				while(!word.isEmpty())
					s+=(char)word.removeFirst().shortValue();
				finalObjects.add(s);
			}
			
			while(!wholeItemset.isEmpty()){
				int val=0;
				int i=0;
				word=wholeItemset.removeFirst();
				while(!word.isEmpty()){
					val+=(word.removeLast()-48)*Math.pow(10,i);
					i++;
				}
				finalObjects.add(val);
			}
			
			newItemsets.add(finalObjects);
		}
		
		return newItemsets;
		
	}
	
	public static void main(String args[]){
		
		//---------- CHANGE THESE VALUES ----------
		
		//PATH
		String pathString=System.getProperty("user.dir")+"/Data/Itemset.txt";
		
		//--------------------------


		System.out.println("START");
		
		structurize(getItemsetlistFromTxt(pathString));
		
	}
	
}
