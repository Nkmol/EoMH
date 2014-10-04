package Parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class MobParser extends Parser{
	
	public static LinkedList<ArrayList<Short>> getMoblistFromScr(String pathString, int mobBytesLengthMin){
		
		try{
			//FILE
			Path path = Paths.get(pathString);
			byte[] data = Files.readAllBytes(path);
			System.out.println("BYTES: "+data.length+" ,CREATE MOB LISTS...");
			
			//GET ITEMS TO LIST
			LinkedList<ArrayList<Short>> mobs=new LinkedList<ArrayList<Short>>();
			int byteatm=0;
			short shortdata=0;
			int counter;
			ArrayList<Short> wholeMob;
			
			while(byteatm<data.length){
				wholeMob=new ArrayList<Short>();
				for(int i=0;i<mobBytesLengthMin;i++){
					shortdata=(short)data[byteatm];
					if(shortdata<0)
						shortdata+=256;
					wholeMob.add(shortdata);
					byteatm++;
				}
				mobs.add(wholeMob);
			}
			
			counter=0;
			for(Iterator<ArrayList<Short>> i=mobs.iterator();i.hasNext();){
				int id=convertBytesToInteger(i.next(),0);
				if(id==0){
					i.remove();
					System.out.println("wrong mob detected at index "+counter);
				}
				counter++;
			}
			
			System.out.println("FOUND "+mobs.size()+" MOBS");
			return mobs;
			
		}catch(Exception e){e.printStackTrace();}
		return null;
		
	}
	
	public static LinkedList<ArrayList<Short>> getDroplistFromScr(String pathString, int dropBytesLength1, int dropBytesLength2){
		
		try{
			//FILE
			Path path = Paths.get(pathString);
			byte[] data = Files.readAllBytes(path);
			System.out.println("BYTES: "+data.length+" ,CREATE DROP LISTS...");
			
			//GET ITEMS TO LIST
			LinkedList<ArrayList<Short>> drops=new LinkedList<ArrayList<Short>>();
			int byteatm=0;
			short shortdata=0;
			ArrayList<Short> wholeDrop;
			
			while(byteatm<data.length){
				wholeDrop=new ArrayList<Short>();
				for(int i=0;i<dropBytesLength1;i++){
					shortdata=(short)data[byteatm];
					if(shortdata<0)
						shortdata+=256;
					wholeDrop.add(shortdata);
					byteatm++;
				}
				drops.add(wholeDrop);
				wholeDrop=new ArrayList<Short>();
				for(int i=0;i<dropBytesLength2;i++){
					shortdata=(short)data[byteatm];
					if(shortdata<0)
						shortdata+=256;
					wholeDrop.add(shortdata);
					byteatm++;
				}
				drops.add(wholeDrop);
			}
			
			System.out.println("FOUND "+(drops.size()/2)+" DROPS");
			return drops;
			
		}catch(Exception e){e.printStackTrace();}
		return null;
		
	}
	
	public static void createDroplist(String pathString, LinkedList<ArrayList<Short>> drops){
		
		try{
			System.out.println("CREATE DROP LIST TEXT FILE...");
			
			//CREATE ITEM LIST TEXT FILE
			BufferedWriter out = new BufferedWriter(new FileWriter(pathString));
			ArrayList<Short> drop1,drop2;
			
			out.write("MOBID     , MOBLVL    , DROPID    , DROPRATE  , ");
			out.newLine();
			
			while(!drops.isEmpty()){
				drop1=drops.removeFirst();
				drop2=drops.removeFirst();
				
				for(int i=0;i<80;i++){
					if(convertBytesToInteger(drop1, 56+i*12)!=0){
						//MOBID
						writeSmallString(out, drop1, 0);
						//MOBLVL
						writeByteString(out, drop1, 51);
						//DROPID
						writeIntegerString(out, drop1, 56+i*12);
						//DROPRATE
						writeIntegerDivisorString(out, drop2, i*4, 10000);
						out.newLine();
					}
				}
			}
			
		    out.close();
		    
		    System.out.println("DONE");
			
			
		}catch(Exception e){e.printStackTrace();}
		
	}
	
	public static void createMoblist(String pathString, LinkedList<ArrayList<Short>> mobs){
		
		try{
			System.out.println("CREATE MOB LIST TEXT FILE...");
			
			//CREATE ITEM LIST TEXT FILE
			BufferedWriter out = new BufferedWriter(new FileWriter(pathString));
			ArrayList<Short> mob;
			
			out.write("ID        , NAME             , LOCATION                         , ");
			out.write("LVL       , MINATK    , MAXATK    , DEF       , HP        , ATKSUC    , DEFSUC    , COINS     , EXP            , ");
			out.write("BASEFAME            , SKILL1    , SKILL2    , SKILL3    , ");
			out.newLine();
			while(!mobs.isEmpty()){
				mob=mobs.removeFirst();
				//ID
				writeSmallString(out, mob, 0);
				//NAME
				writeString(out, mob, 2, 17);
				//LOCATION
				writeString(out, mob, 19, 33);
				//LVL
				int lvl=writeByteString(out, mob, 84);
				//MINATK
				writeSmallString(out, mob, 220);
				//MAXATK
				writeSmallString(out, mob, 222);
				//DEF
				writeSignedSmallString(out, mob, 226);
				//HP
				int hp=writeIntegerString(out, mob, 228);
				//ATK SUC
				writeSmallString(out, mob, 232);
				//DEF SUC
				writeSmallString(out, mob, 234);
				//COINS
				writeIntegerString(out, mob, 356);
				//EXP (approximated)
				long exp;
				exp=(long)(Math.pow(1.09, lvl)/10*hp);
				if(lvl>20)
					exp+=(lvl-20)*0.1*hp;
				if(lvl>30)
					exp+=(lvl-30)*0.8*hp;
				if(lvl>40)
					exp+=(lvl-40)*4*hp;
				if(lvl>55)
					exp+=(lvl-55)*5*hp;
				if(lvl>70)
					exp+=(lvl-70)*5*hp;
				if(lvl>85)
					exp+=(lvl-85)*5*hp;
				if(lvl>100)
					exp+=(lvl-100)*5*hp;
				if(exp<1)
					exp=1;
				String expS=Long.toString(exp);
				out.write(expS);
				//FAME lvl/5
				int fame = lvl/5;
				out.write(fame);
				for(int i=0;i<15-expS.length();i++){
					out.write(" ");
				}
				out.write(", ");
				//SKILL1
				writeIntegerString(out, mob, 372);
				//SKILL2
				writeIntegerString(out, mob, 380);
				//SKILL3
				writeIntegerString(out, mob, 388);
				
				out.newLine();
			}
			
		    out.close();
		    
		    System.out.println("DONE");
			
			
		}catch(Exception e){e.printStackTrace();}
		
	}

	public static void main(String args[]){
		
		//---------- CHANGE THESE VALUES ----------
		
		//PATH
		String path1String=System.getProperty("user.dir")+"/Data/mobs.scr";
		String path2String=System.getProperty("user.dir")+"/Data/mobs.txt";
		
		String path3String=System.getProperty("user.dir")+"/Data/mobsitem.scr";
		String path4String=System.getProperty("user.dir")+"/Data/mobsitem.txt";
		
		//--------------------------


		System.out.println("START [64mb heap needed]");
		int mobBytesLengthMin,dropBytesLength1,dropBytesLength2;
		mobBytesLengthMin=456;
		dropBytesLength1=1012;
		dropBytesLength2=800;
		
		createMoblist(path2String, getMoblistFromScr(path1String, mobBytesLengthMin));
		
		createDroplist(path4String, getDroplistFromScr(path3String, dropBytesLength1, dropBytesLength2));
		
	}
}
