package Parser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class SkillParser extends Parser{
	
	public static LinkedList<ArrayList<Short>> getSkilllistFromScr(String pathString, int skillBytesLengthMin){
		
		try{
			//FILE
			Path path = Paths.get(pathString);
			byte[] data = Files.readAllBytes(path);
			System.out.println("BYTES: "+data.length+" ,CREATE SKILL LISTS...");
			
			//GET ITEMS TO LIST
			LinkedList<Short> skillBytes=new LinkedList<Short>();
			LinkedList<ArrayList<Short>> skills=new LinkedList<ArrayList<Short>>();
			int byteatm=0;
			short shortdata=0;
			int counter;
			ArrayList<Short> wholeSkill;
			
			for(int i=0;i<1600;i++){
				shortdata=(short)data[i];
				if(shortdata<0)
					shortdata+=256;
				skillBytes.add(shortdata);
				byteatm++;
			}
			
			while(byteatm<data.length){
				
				wholeSkill=new ArrayList<Short>();
				counter=0;
				while(counter<skillBytesLengthMin && skillBytes.size()>0){
					
					wholeSkill.add(skillBytes.removeFirst().shortValue());
					counter++;
				}
				
				counter=wholeSkill.get(1496)*8;
				while(counter>0){
					wholeSkill.add(skillBytes.removeFirst().shortValue());
					counter--;
				}
				
				skills.add(wholeSkill);
				while(byteatm<data.length && skillBytes.size()<1600){
					shortdata=(short)data[byteatm];
					if(shortdata<0)
						shortdata+=256;
					skillBytes.add(shortdata);
					byteatm++;
				}
				
			}
			
			counter=0;
			for(Iterator<ArrayList<Short>> i=skills.iterator();i.hasNext();){
				int id=convertBytesToInteger(i.next(),0);
				if(id<11 || id>140000000){
					i.remove();
					System.out.println("wrong skill detected at index "+counter);
				}
				counter++;
			}
			
			System.out.println("FOUND "+skills.size()+" SKILLS");
			return skills;
			
		}catch(Exception e){e.printStackTrace();}
		return null;
		
	}
	
	public static void createSkilllist(String pathString, LinkedList<ArrayList<Short>> skills){
		
		try{
			System.out.println("CREATE SKILL LIST TEXT FILE...");
			
			//CREATE ITEM LIST TEXT FILE
			BufferedWriter out = new BufferedWriter(new FileWriter(pathString));
			ArrayList<Short> skill;
			
			out.write("ID        , SKILLGROUP, NAME                          , ");
			out.write("REQWEPINFO          , ");
			out.write("CLASS     , STAGE     , ");
			out.write("DESCRIPTIONGENERAL                                                                                  , ");
			out.write("DESCRIPTIONEXTRA                        , ");
			out.write("DESCRIPTIONSTATS                           , ");
			out.write("EFFONWEP  , REQSKILL1 , REQSKILL2 , REQSKILL3 , SKILLPOINT, NEXTSKLAW , NEXTSKEVIL, ");
			out.write("LEVEL     , SPECTYPE  , MORESPECTP, NORMALDMG , MOBDMG    , TARGETS   , GENERALTYP, ");
			out.write("FACTION   , NEEDSWEP  , ULTISETID , ISCASTABLE, ISSPECCAST, HEALCOST  , MANACOST  , ");
			out.write("STAMCOST  , DAMAGE    , SPEED     , EFFAMOUNT , EFFECTID1 , EFFECTDUR1, EFFECTVAL1, ");
			out.write("EFFECTID2 , EFFECTDUR2, EFFECTVAL2, EFFECTID3 , EFFECTDUR3, EFFECTVAL3, ");
			out.newLine();
			while(!skills.isEmpty()){
				skill=skills.removeFirst();
				//ID
				writeIntegerString(out, skill, 0);
				//SKILLGROUP
				writeIntegerString(out, skill, 4);
				//NAME
				writeString(out, skill, 8, 30);//to262
				//REQWEPINFO
				writeString(out, skill, 263, 20);//to517
				//CLASS
				writeSmallString(out, skill, 518);
				//STAGE
				writeByteString(out, skill, 520);
				//DESCRIPTIONGENERAL
				writeString(out, skill, 521, 100);//to1032
				//DESCRIPTIONEXTRA
				writeString(out, skill, 1033, 40);
				//DESCRIPTIONSTATS
				writeString(out, skill, 1073, 43);
				//EFFECTONWEP
				writeIntegerString(out, skill, 1276);
				//REQSKILL1
				writeIntegerString(out, skill, 1280);
				//REQSKILL2
				writeIntegerString(out, skill, 1284);
				//REQSKILL3
				writeIntegerString(out, skill, 1288);
				//SKILLPOINTS
				writeIntegerString(out, skill, 1292);
				//NEXTSKILLLAWFUL
				writeIntegerString(out, skill, 1296);
				//NEXTSKILLEVIL
				writeIntegerString(out, skill, 1300);
				//LVL
				writeSmallString(out, skill, 1304);
				//SPECIFICTYPE
				writeSmallString(out, skill, 1306);
				//MORESPECIFICTYPE
				writeIntegerString(out, skill, 1308);
				//NORMALDMGFONT
				writeSmallString(out, skill, 1320);
				//MOBDMGFONT
				writeSmallString(out, skill, 1322);
				//TAGETS
				writeSmallString(out, skill, 1330);
				//GENERALTYPE
				writeSmallString(out, skill, 1332);
				//FACTION
				writeIntegerString(out, skill, 1340);
				//NEEDSWEPTOCAST
				writeIntegerString(out, skill, 1344);
				//ULTISETID
				writeIntegerString(out, skill, 1348);
				//ISCASTABLE
				writeByteString(out, skill, 1352);
				//ISSPECIALCAST
				writeByteString(out, skill, 1353);
				//HEALCOST -signed
				writeSignedSmallString(out, skill, 1368);
				//MANACOST -signed
				writeSignedSmallString(out, skill, 1370);
				//STAMINACOST -signed
				writeSignedSmallString(out, skill, 1372);
				//DMG
				writeIntegerString(out, skill, 1390);
				//SPEED
				writeFloatString(out, skill, 1408);
				//EFFAMOUNT
				writeIntegerString(out, skill, 1496);
				for(int i=0;i<3;i++){
					if(skill.get(1496)>i){
						//EFFID
						writeSmallString(out, skill, 1500+i*8);
						//EFFDURATION
						writeSmallString(out, skill, 1502+i*8);
						//EFFVALUE -signed
						writeSignedSmallString(out, skill, 1504+i*8);
					}else{
						out.write("0         , 0         , 0         , ");
					}
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
		String path1String=System.getProperty("user.dir")+"/Data/skills.scr";
		String path2String=System.getProperty("user.dir")+"/Data/skills.txt";
		
		//--------------------------


		System.out.println("START [64mb heap needed]");
		int skillBytesLengthMin=1500;
		
		createSkilllist(path2String, getSkilllistFromScr(path1String, skillBytesLengthMin));
		
	}
	
}
