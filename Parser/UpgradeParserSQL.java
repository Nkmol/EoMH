package Parser;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;

import Database.InstallDAO;
import Database.SQLconnection;

public class UpgradeParserSQL extends Parser{

	public static void parseUpgradesToSQL(InstallDAO dao, LinkedList<ArrayList<Short>> upgrs){
		
		System.out.println("Parsing upgrades into SQL [this takes many minutes/hours]");
		
		Connection sql=new SQLconnection().getConnection();
		
		ArrayList<Short> upgr;
		
		int id,oldit,upgrader,newit,upgrskill;
		float itstage,upgradelvl,failrate,breakoption;
		
		int count=0;
		
		//String statement="INSERT INTO upgrade(upgradeId,oldItem,upgrader,newItem,"
		//		+ "itemStage,upgradeLvl,failRate,breakOption,upgradeSkill) VALUES";
		
		while(!upgrs.isEmpty()){
				
			upgr=upgrs.removeFirst();
				
			id=convertBytesToInteger(upgr,0);
			oldit=convertBytesToInteger(upgr,4);
			upgrader=convertBytesToInteger(upgr,8);
			newit=convertBytesToInteger(upgr,12);
			itstage=convertBytesToFloat(upgr,16);
			upgradelvl=convertBytesToFloat(upgr,20);
			failrate=convertBytesToFloat(upgr,24);
			breakoption=convertBytesToFloat(upgr,32);
			upgrskill=convertBytesToInteger(upgr,36);
			
			//statement+=("("+id+","+oldit+","+upgrader+","+newit+","+itstage+","+upgradelvl+","+failrate+","+breakoption+","+upgrskill+")");
			//if(!upgrs.isEmpty())
			//	statement+=",";
			
			dao.addUpgrade(sql, id, oldit, upgrader, newit, itstage, upgradelvl, failrate, breakoption, upgrskill);
					
				count++;
				
				if(count%100==0)
					System.out.print("["+(count)+"]");
			
		}
		//statement+=";";
		
		//dao.addAllUpgrades(sql, statement);
		
		
		System.out.println("Parsing into SQL done");
		
	}
	
}
