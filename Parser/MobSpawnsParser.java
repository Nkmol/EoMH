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

import Tools.BitTools;

public class MobSpawnsParser extends Parser{

	public static LinkedList<ArrayList<Short>> getMobspawnlistFromArr(String pathString, int mobBytesLengthMin){
		
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
			
			LinkedList<ArrayList<Short>> mobspawns=new LinkedList<ArrayList<Short>>();
			int counter;
			int byteatm;
			short shortdata;
			ArrayList<Short> wholeMobspawn;
			ArrayList<Short> oldMobspawn;
			
			for(int files=0;files<paths.size();files++){
				
				//FILE
				data = Files.readAllBytes(paths.get(files));
				System.out.println("BYTES: "+data.length+" ,CREATE MOBSPAWN LISTS FOR MAP "+maps.get(files));
			
				//GET MOBSPAWNS TO LIST
				byteatm=0;
				shortdata=0;
				counter=1;
				wholeMobspawn=new ArrayList<Short>();
				oldMobspawn=new ArrayList<Short>();
				
				while(byteatm<data.length){
					wholeMobspawn=new ArrayList<Short>();
					for(int i=0;i<mobBytesLengthMin;i++){
						shortdata=(short)data[byteatm];
						if(shortdata<0)
							shortdata+=256;
						wholeMobspawn.add(shortdata);
						byteatm++;
					}
					if(!wholeMobspawn.equals(oldMobspawn)){
						if(!mobspawns.isEmpty() && byteatm!=20){
							mobspawns.getLast().add(0, maps.get(files));
							mobspawns.getLast().add(3, (short)counter);
						}
						mobspawns.add(wholeMobspawn);
						
						counter=1;
					}else{
						counter++;
					}
					oldMobspawn=wholeMobspawn;
				}
				if(!mobspawns.isEmpty()){
					mobspawns.getLast().add(0, maps.get(files));
					mobspawns.getLast().add(3, (short)counter);
				}
			
			}
			
			counter=0;
			for(Iterator<ArrayList<Short>> i=mobspawns.iterator();i.hasNext();){
				int id=convertBytesToSmall(i.next(),0);
				if(id==0){
					i.remove();
					System.out.println("wrong mob detected at index "+counter);
				}
				counter++;
			}
			
			System.out.println("FOUND "+mobspawns.size()+" MOBSPAWNS");
			return mobspawns;
			
		}catch(Exception e){e.printStackTrace();}
		return null;
		
	}
	
	public static void createMoblist(String pathString, LinkedList<ArrayList<Short>> mobspawns){
		
		try{
			System.out.println("CREATE MOBSPAWN LIST TEXT FILE...");
			
			//CREATE MOBSPAWN LIST TEXT FILE
			BufferedWriter out = new BufferedWriter(new FileWriter(pathString));
			ArrayList<Short> mobspawn;
			
			out.write("MAP       , ID        , AMOUNT    , X1        , Y1        , X2        , Y2        , RX        , RY        , RADIUS    , ");
			out.newLine();
			while(!mobspawns.isEmpty()){
				mobspawn=mobspawns.removeFirst();
				//MAP
				writeByteString(out, mobspawn, 0);
				//ID
				writeSmallString(out, mobspawn, 1);
				//AMOUNT
				writeByteString(out, mobspawn, 3);
				//X1
				writeFloatString(out, mobspawn, 6);
				//Y1
				writeFloatString(out, mobspawn, 10);
				//X2
				writeFloatString(out, mobspawn, 14);
				//Y2
				writeFloatString(out, mobspawn, 18);
				
				//messy circle spawn stuff
				short shortdata=0;
				float x,y,dx,dy,rx,ry,radius;
				byte[] brx,bry,br;
				x=convertBytesToFloat(mobspawn, 6);
				y=convertBytesToFloat(mobspawn, 10);
				dx=convertBytesToFloat(mobspawn, 14);
				dy=convertBytesToFloat(mobspawn, 18);
				rx=x+dx/2;
				ry=y+dy/2;
				radius=Math.min(Math.abs(dx), Math.abs(dy));
				brx=BitTools.floatToByteArray(rx);
				bry=BitTools.floatToByteArray(ry);
				br=BitTools.floatToByteArray(radius);
				for(int i=0;i<4;i++){
					shortdata=(short)brx[i];
					if(shortdata<0)
						shortdata+=256;
					mobspawn.add(shortdata);
				}
				for(int i=0;i<4;i++){
					shortdata=(short)bry[i];
					if(shortdata<0)
						shortdata+=256;
					mobspawn.add(shortdata);
				}
				for(int i=0;i<4;i++){
					shortdata=(short)br[i];
					if(shortdata<0)
						shortdata+=256;
					mobspawn.add(shortdata);
				}
				
				//RX
				writeFloatString(out, mobspawn, 22);
				//RY
				writeFloatString(out, mobspawn, 26);
				//RADIUS
				writeFloatString(out, mobspawn, 30);
				
				out.newLine();
			}
			
		    out.close();
		    
		    System.out.println("DONE");
			
			
		}catch(Exception e){e.printStackTrace();}
		
	}

	public static void main(String args[]){
		
		//---------- CHANGE THESE VALUES ----------
		
		//PATH
		String path1String=System.getProperty("user.dir")+"/Data/MobSpawnsMaps.txt";
		String path2String=System.getProperty("user.dir")+"/Data/mobspawns.txt";
		
		//--------------------------


		System.out.println("START [64mb heap needed]");
		int mobBytesLengthMin;
		mobBytesLengthMin=20;
		
		createMoblist(path2String, getMobspawnlistFromArr(path1String, mobBytesLengthMin));
		
	}
	
}
