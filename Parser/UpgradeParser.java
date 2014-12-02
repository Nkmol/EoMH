package Parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class UpgradeParser extends Parser{
	
	public static LinkedList<ArrayList<Short>> getUpgrlistFromScr(String pathString, int upgrBytesLengthMin){
		
		try{
			//FILE
			Path path = Paths.get(pathString);
			byte[] data = Files.readAllBytes(path);
			System.out.println("BYTES: "+data.length+" ,CREATE UPGRADE LISTS...");
			
			//GET UPGRADES TO LIST
			LinkedList<ArrayList<Short>> upgrs=new LinkedList<ArrayList<Short>>();
			int byteatm=0;
			short shortdata=0;
			int counter;
			ArrayList<Short> wholeUpgr;
			
			while(byteatm<data.length){
				wholeUpgr=new ArrayList<Short>();
				for(int i=0;i<upgrBytesLengthMin;i++){
					shortdata=(short)data[byteatm];
					if(shortdata<0)
						shortdata+=256;
					wholeUpgr.add(shortdata);
					byteatm++;
				}
				upgrs.add(wholeUpgr);
			}
			
			counter=0;
			for(Iterator<ArrayList<Short>> i=upgrs.iterator();i.hasNext();){
				int id=convertBytesToInteger(i.next(),0);
				if(id==0){
					i.remove();
					System.out.println("wrong upgrade detected at index "+counter);
				}
				counter++;
			}
			
			System.out.println("FOUND "+upgrs.size()+" UPGRADES");
			return upgrs;
			
		}catch(Exception e){e.printStackTrace();}
		return null;
		
	}
	
	public static void createUpgrlist(String pathString, LinkedList<ArrayList<Short>> upgrs){
		
		try{
			System.out.println("CREATE UPGRADE LIST TEXT FILE...");
			
			//CREATE UPGRADE LIST TEXT FILE
			BufferedWriter out = new BufferedWriter(new FileWriter(pathString));
			ArrayList<Short> upgr;
			
			out.write("ID        , OLDITEM   , UPGRADER  , NEWITEM   , ITEMSTAGE , UPGRADELVL, FAILRATE  , DUNNO     , BREAKOPTN , UPGRSKILL , DUNNO     , ");
			out.newLine();
			while(!upgrs.isEmpty()){
				upgr=upgrs.removeFirst();
				//ID
				writeIntegerString(out, upgr, 0);
				//OLDITEM
				writeIntegerString(out, upgr, 4);
				//UPGRADER
				writeIntegerString(out, upgr, 8);
				//NEWITEM
				writeIntegerString(out, upgr, 12);
				//ITEMSTAGE
				writeFloatString(out, upgr, 16);
				//UPGRADELVL
				writeFloatString(out, upgr, 20);
				//FAILRATE
				writeFloatString(out, upgr, 24);
				//DUNNO
				writeFloatString(out, upgr, 28);
				//BREAKOPTION
				writeFloatString(out, upgr, 32);
				//UPGRADESKILL
				writeIntegerString(out, upgr, 36);
				//DUNNO
				writeIntegerString(out, upgr, 40);
				
				out.newLine();
			}
			
		    out.close();
		    
		    System.out.println("DONE");
			
			
		}catch(Exception e){e.printStackTrace();}
		
	}

	public static void main(String args[]){
		
		//---------- CHANGE THESE VALUES ----------
		
		//PATH
		String path1String=System.getProperty("user.dir")+"/Data/upgradeitems.scr";
		String path2String=System.getProperty("user.dir")+"/Data/UpgradeItems.txt";
		
		//--------------------------


		System.out.println("START [64mb heap needed]");
		int upgrBytesLengthMin;
		upgrBytesLengthMin=44;
		
		createUpgrlist(path2String, getUpgrlistFromScr(path1String, upgrBytesLengthMin));
		
	}
}
