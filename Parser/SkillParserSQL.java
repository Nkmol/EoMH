package Parser;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;

import Database.InstallDAO;
import Database.SQLconnection;

public class SkillParserSQL extends Parser{

	public static void parseSkillsToSQL(InstallDAO dao, LinkedList<ArrayList<Short>> skills){
		
		System.out.println("Parsing skills into SQL");
		
		Connection sql=new SQLconnection().getConnection();
		
		ArrayList<Short> skill;
		
		int skillid,skillgroup,chclass,stage,effectOnWep,reqSkill1,reqSkill2,reqSkill3,skillpoints,nextSkillLawful,
		nextSkillEvil,lvl,specificType,moreSpecificType,normalDmgFont,mobDmgFont,targets,generalType,faction,
		needsWepToCast,ultiSetId,isCastable,isSpecialCast,healCost,manaCost,staminaCost,dmg,effAmount,
		effId1,effDuration1,effValue1,effId2,effDuration2,effValue2,effId3,effDuration3,effValue3;
		float speed;
		
		int count=0;
		
		while(!skills.isEmpty()){
				
			skill=skills.removeFirst();
				
			skillid=convertBytesToInteger(skill,0);
			skillgroup=convertBytesToInteger(skill,4);
			chclass=convertBytesToSmall(skill,518);
			stage=convertBytesToByte(skill,520);
			effectOnWep=convertBytesToInteger(skill,1276);
			reqSkill1=convertBytesToInteger(skill,1280);
			reqSkill2=convertBytesToInteger(skill,1284);
			reqSkill3=convertBytesToInteger(skill,1288);
			skillpoints=convertBytesToInteger(skill,1292);
			nextSkillLawful=convertBytesToInteger(skill,1296);
			nextSkillEvil=convertBytesToInteger(skill,1300);
			lvl=convertBytesToSmall(skill,1304);
			specificType=convertBytesToSmall(skill,1306);
			moreSpecificType=convertBytesToInteger(skill,1308);
			normalDmgFont=convertBytesToSmall(skill,1320);
			mobDmgFont=convertBytesToSmall(skill,1322);
			targets=convertBytesToSmall(skill,1330);
			generalType=convertBytesToSmall(skill,1332);
			faction=convertBytesToInteger(skill,1340);
			needsWepToCast=convertBytesToInteger(skill,1344);
			ultiSetId=convertBytesToInteger(skill,1348);
			isCastable=convertBytesToByte(skill,1352);
			isSpecialCast=convertBytesToByte(skill,1353);
			healCost=convertBytesToSignedSmall(skill,1368);
			manaCost=convertBytesToSignedSmall(skill,1370);
			staminaCost=convertBytesToSignedSmall(skill,1372);
			dmg=convertBytesToInteger(skill,1390);
			speed=convertBytesToFloat(skill,1408);
			effAmount=convertBytesToInteger(skill,1496);
			if(skill.get(1496)>0){
				effId1=convertBytesToSmall(skill,1500);
				effDuration1=convertBytesToSmall(skill,1502);
				effValue1=convertBytesToSignedSmall(skill,1504);
			}else{
				effId1=0;
				effDuration1=0;
				effValue1=0;
			}
			if(skill.get(1496)>1){
				effId2=convertBytesToSmall(skill,1508);
				effDuration2=convertBytesToSmall(skill,1510);
				effValue2=convertBytesToSignedSmall(skill,1512);
			}else{
				effId2=0;
				effDuration2=0;
				effValue2=0;
			}
			if(skill.get(1496)>2){
				effId3=convertBytesToSmall(skill,1516);
				effDuration3=convertBytesToSmall(skill,1518);
				effValue3=convertBytesToSignedSmall(skill,1520);
			}else{
				effId3=0;
				effDuration3=0;
				effValue3=0;
			}
						
			dao.addSkill(sql, skillid,skillgroup,chclass,stage,effectOnWep,reqSkill1,reqSkill2,reqSkill3,skillpoints,nextSkillLawful,
					nextSkillEvil,lvl,specificType,moreSpecificType,normalDmgFont,mobDmgFont,targets,generalType,faction,
					needsWepToCast,ultiSetId,isCastable,isSpecialCast,healCost,manaCost,staminaCost,dmg,speed,effAmount,
					effId1,effDuration1,effValue1,effId2,effDuration2,effValue2,effId3,effDuration3,effValue3);
					
				count++;
				
				if(count%100==0)
					System.out.print("["+(count)+"]");
			
		}
		
		System.out.println("Parsing into SQL done");
		
	}
	
}
