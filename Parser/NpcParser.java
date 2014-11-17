package Parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class NpcParser extends Parser{
	
	public static LinkedList<ArrayList<Short>> getNpclistFromScr(String pathString, int npcBytesLengthMin){
		
		try{
			//FILE
			Path path = Paths.get(pathString);
			byte[] data = Files.readAllBytes(path);
			System.out.println("BYTES: "+data.length+" ,CREATE NPC LISTS...");
			
			//GET MOBS TO LIST
			LinkedList<ArrayList<Short>> npcs=new LinkedList<ArrayList<Short>>();
			int byteatm=0;
			short shortdata=0;
			int counter;
			ArrayList<Short> wholeNpc;
			
			while(byteatm<data.length){
				wholeNpc=new ArrayList<Short>();
				for(int i=0;i<npcBytesLengthMin;i++){
					shortdata=(short)data[byteatm];
					if(shortdata<0)
						shortdata+=256;
					wholeNpc.add(shortdata);
					byteatm++;
				}
				npcs.add(wholeNpc);
			}
			
			counter=0;
			for(Iterator<ArrayList<Short>> i=npcs.iterator();i.hasNext();){
				int id=convertBytesToInteger(i.next(),0);
				if(id==0){
					i.remove();
					System.out.println("wrong npc detected at index "+counter);
				}
				counter++;
			}
			
			System.out.println("FOUND "+npcs.size()+" NPCS");
			return npcs;
			
		}catch(Exception e){e.printStackTrace();}
		return null;
		
	}
	
	public static void createNpclist(String pathString, LinkedList<ArrayList<Short>> npcs){
		
		try{
			System.out.println("CREATE NPC LIST TEXT FILE...");
			
			//CREATE MOB LIST TEXT FILE
			BufferedWriter out = new BufferedWriter(new FileWriter(pathString));
			ArrayList<Short> npc;
			
			out.write("ID        , NAME             , MODULE    , ");
			for(int i=0;i<60;i++){
				if(i<9){
					out.write("ITEM"+(i+1)+"     , ");
				}else{
					out.write("ITEM"+(i+1)+"    , ");
				}
			}
			out.newLine();
			while(!npcs.isEmpty()){
				npc=npcs.removeFirst();
				//ID
				writeSmallString(out, npc, 0);
				//NAME
				writeString(out, npc, 2, 17);
				//MODULE
				writeSmallString(out, npc, 118);
				//ITEMS
				for(int i=0;i<60;i++){
					writeIntegerString(out, npc, 128+i*12);
				}
				
				out.newLine();
			}
			
		    out.close();
		    
		    System.out.println("DONE");
			
			
		}catch(Exception e){e.printStackTrace();}
		
	}

	public static void main(String args[]){
		
		//---------- CHANGE THESE VALUES ----------
		
		//PATH
		String path1String=System.getProperty("user.dir")+"/Data/npcs.scr";
		String path2String=System.getProperty("user.dir")+"/Data/npcs.txt";
		
		//--------------------------


		System.out.println("START [64mb heap needed]");
		int npcBytesLengthMin;
		npcBytesLengthMin=1676;
		
		createNpclist(path2String, getNpclistFromScr(path1String, npcBytesLengthMin));
		
	}
}
