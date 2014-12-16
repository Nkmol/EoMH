package Parser;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;

import Database.InstallDAO;
import Database.SQLconnection;

public class MobParserSQL extends Parser{

	public static void parseMobsToSQL(InstallDAO dao, LinkedList<ArrayList<Short>> mobs, LinkedList<ArrayList<Short>> drops){
		
		System.out.println("Parsing mobs into SQL");
		
		Connection sql=new SQLconnection().getConnection();
		
		ArrayList<Short> mob;
		ArrayList<Short> dropObj1;
		ArrayList<Short> dropObj2;
		
		int id,lvl,skill1,skill2,skill3,minatk,maxatk,deff,hp,atksuc,defsuc,coins,basefame,aggro,attrange,follow,move;
		long basexp;
		int[] drop;
		float[] dropchance;
		
		int count=0;
		int dropindex=0;
		boolean found;
		
		while(!mobs.isEmpty()){
				
			mob=mobs.removeFirst();
				
			id=convertBytesToSmall(mob,0);
			lvl=convertBytesToByte(mob,84);
			minatk=convertBytesToSmall(mob,220);
			maxatk=convertBytesToSmall(mob,222);
			deff=convertBytesToSignedSmall(mob,226);
			hp=convertBytesToInteger(mob,228);
			atksuc=convertBytesToSmall(mob,232);
			defsuc=convertBytesToSmall(mob,234);
			coins=convertBytesToInteger(mob,356);
			skill1=convertBytesToInteger(mob,372);
			skill2=convertBytesToInteger(mob,380);
			skill3=convertBytesToInteger(mob,388);
			
			
			basexp=(long)(Math.pow(1.09, lvl)/10*hp);
			if(lvl>20)
				basexp+=(lvl-20)*0.1*hp;
			if(lvl>30)
				basexp+=(lvl-30)*0.8*hp;
			if(lvl>40)
				basexp+=(lvl-40)*4*hp;
			if(lvl>55)
				basexp+=(lvl-55)*5*hp;
			if(lvl>70)
				basexp+=(lvl-70)*5*hp;
			if(lvl>85)
				basexp+=(lvl-85)*5*hp;
			if(lvl>100)
				basexp+=(lvl-100)*5*hp;
			
			if(basexp<1)
				basexp=1;
			basefame=lvl/5;
			aggro=30;
			attrange=15;
			follow=100;
			move=300;
			
			drop=new int[80];
			dropchance=new float[80];
			
			if(!drops.isEmpty()){
				dropindex=0;
				found=false;
				while(found==false && dropindex+1<drops.size()){
					if(convertBytesToSmall(drops.get(dropindex),0)==id){
						found=true;
					}else{
						dropindex+=2;
					}
				}
				if(found){
					dropObj1=drops.remove(dropindex);
					dropObj2=drops.remove(dropindex);
					for(int i=0;i<80;i++){
						drop[i]=convertBytesToInteger(dropObj1,56+i*12);
						dropchance[i]=convertBytesToIntegerDivisor(dropObj2,i*4,1000000);
					}
				}else{
					drop[0]=0;
					dropchance[0]=0;
				}
			}else{
				drop[0]=0;
				dropchance[0]=0;
			}
						
			dao.createMobDataEntry(sql, id, lvl, skill1, skill2, skill3, minatk, maxatk, deff, hp, atksuc, defsuc, basexp, coins, basefame, aggro, attrange, follow, move, drop, dropchance);
					
				count++;
				
				if(count%100==0)
					System.out.print("["+(count)+"]");
			
		}
		
		System.out.println("Parsing into SQL done");
		
	}
	
}
