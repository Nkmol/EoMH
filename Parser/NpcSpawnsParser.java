package Parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class NpcSpawnsParser extends Parser{

	public static LinkedList<ArrayList<Short>> getNpcspawnlistFromArr(String pathString, int npcBytesLengthMin){
		
		try{
			//getting paths
			LinkedList<Path> paths=new LinkedList<Path>();
			LinkedList<Short> maps=new LinkedList<Short>();
			BufferedReader br = new BufferedReader(new FileReader(pathString));
			String line;
			Path p;
			while ((line = br.readLine()) != null) {
				p=Paths.get(System.getProperty("user.dir")+"/Data/"+line);
				paths.add(p);
				maps.add((short)Integer.parseInt(line.substring(3,6)));
			}
			br.close();
			
			//getting bytes from all paths
			byte[] data;
			
			LinkedList<ArrayList<Short>> npcspawns=new LinkedList<ArrayList<Short>>();
			int counter;
			int byteatm;
			short shortdata;
			ArrayList<Short> wholeNpcspawn;
			int length;
			
			for(int files=0;files<paths.size();files++){
				
				//FILE
				data = Files.readAllBytes(paths.get(files));
				System.out.println("BYTES: "+data.length+" ,CREATE NPCSPAWN LISTS FOR MAP "+maps.get(files));
			
				if(maps.get(files)==207)
					length=20;
				else
					length=npcBytesLengthMin;
				
				//GET MOBSPAWNS TO LIST
				byteatm=0;
				shortdata=0;
				wholeNpcspawn=new ArrayList<Short>();
				
				while(byteatm<data.length){
					wholeNpcspawn=new ArrayList<Short>();
					for(int i=0;i<length;i++){
						shortdata=(short)data[byteatm];
						if(shortdata<0)
							shortdata+=256;
						wholeNpcspawn.add(shortdata);
						byteatm++;
					}
					wholeNpcspawn.add(0, maps.get(files));
					npcspawns.add(wholeNpcspawn);
				}
			
			}
			
			counter=0;
			for(Iterator<ArrayList<Short>> i=npcspawns.iterator();i.hasNext();){
				int id=convertBytesToSmall(i.next(),0);
				if(id==0){
					i.remove();
					System.out.println("wrong npcspawn detected at index "+counter);
				}
				counter++;
			}
			
			System.out.println("FOUND "+npcspawns.size()+" NPCSPAWNS");
			return npcspawns;
			
		}catch(Exception e){e.printStackTrace();}
		return null;
		
	}
	
	public static void createNpcspawnlist(String pathString, LinkedList<ArrayList<Short>> npcspawns){
		
		try{
			System.out.println("CREATE NPCSPAWN LIST TEXT FILE...");
			
			//CREATE MOBSPAWN LIST TEXT FILE
			BufferedWriter out = new BufferedWriter(new FileWriter(pathString));
			ArrayList<Short> npcspawn;
			
			out.write("MAP       , ID        , X1        , Y1        , ");
			out.newLine();
			while(!npcspawns.isEmpty()){
				npcspawn=npcspawns.removeFirst();
				//MAP
				writeByteString(out, npcspawn, 0);
				//ID
				writeSmallString(out, npcspawn, 1);
				//X1
				writeFloatString(out, npcspawn, 5);
				//Y1
				writeFloatString(out, npcspawn, 9);
				
				out.newLine();
			}
			
		    out.close();
		    
		    System.out.println("DONE");
			
			
		}catch(Exception e){e.printStackTrace();}
		
	}

	public static void main(String args[]){
		
		//---------- CHANGE THESE VALUES ----------
		
		//PATH
		String path1String=System.getProperty("user.dir")+"/Data/NpcSpawnsMaps.txt";
		String path2String=System.getProperty("user.dir")+"/Data/npcspawns.txt";
		
		//--------------------------


		System.out.println("START [64mb heap needed]");
		int npcBytesLengthMin;
		npcBytesLengthMin=28;
		
		createNpcspawnlist(path2String, getNpcspawnlistFromArr(path1String, npcBytesLengthMin));
		
	}
	
}
