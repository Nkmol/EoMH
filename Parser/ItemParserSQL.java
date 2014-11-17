package Parser;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;

import Database.InstallDAO;
import Database.SQLconnection;

public class ItemParserSQL extends Parser{

	public static void parseItemsToSQL(InstallDAO dao, LinkedList<ArrayList<Short>> items, int client){
		
		System.out.println("Parsing items into SQL");
		
		Connection sql=new SQLconnection().getConnection();
		
		ArrayList<Short> item;
		
		int itemid,baseid,category,againsttype,bonustype,typedmg,bonustypedmg,
		price,isconsumable,ispermanent,equipslot,width,height,minlvl,maxlvl,
		reqstr,reqdex,reqvit,reqint,reqagi,warusable,sinusable,mageusable,
		monkusable,faction,upgradelvl,str,bonusstr,dex,bonusdex,healhp,vit,bonusvit,
		intl,bonusintl,healmana,agi,bonusagi,life,bonuslife,mana,bonusmana,stam,bonusstam,
		critdmg,bonuscritdmg,mindmg,maxdmg,offpower,bonusoffpower,defpower,bonusdefpower,
		pvpdmginc,timetoexpire,seteffectid,amountsetpieces,movespeed,bufficon1,bufftime1, 
		buffvalue1,bufficon2,bufftime2,buffvalue2;
		float atkrange,atkscs,bonusatkscs,defscs,bonusdefscs,critchance,bonuscritchance;
		
		int count=0;
		
		if (client==1){
			
			while(!items.isEmpty()){
				
				item=items.removeFirst();
				
				itemid=convertBytesToInteger(item,52);
				baseid=convertBytesToInteger(item,128);
				category=convertBytesToInteger(item,136);
				againsttype=convertBytesToInteger(item,140);
				bonustype=convertBytesToInteger(item,144);
				typedmg=convertBytesToInteger(item,148);
				bonustypedmg=convertBytesToInteger(item,152);
				atkrange=convertBytesToFloat(item,164);
				price=convertBytesToInteger(item,176);
				isconsumable=convertBytesToByte(item,180);
				ispermanent=convertBytesToByte(item,181);
				equipslot=convertBytesToByte(item,182);
				width=convertBytesToByte(item,183);
				height=convertBytesToByte(item,184);
				minlvl=convertBytesToByte(item,185);
				maxlvl=convertBytesToByte(item,186);
				reqstr=convertBytesToSmall(item,188);
				reqdex=convertBytesToSmall(item,190);
				reqvit=convertBytesToSmall(item,192);
				reqint=convertBytesToSmall(item,194);
				reqagi=convertBytesToSmall(item,196);
				warusable=convertBytesToByte(item,199);
				sinusable=convertBytesToByte(item,200);
				mageusable=convertBytesToByte(item,201);
				monkusable=convertBytesToByte(item,202);
				faction=convertBytesToByte(item,208);
				upgradelvl=convertBytesToByte(item,225);
				str=convertBytesToSmall(item,226);
				bonusstr=convertBytesToSmall(item,228);
				dex=convertBytesToSmall(item,232);
				bonusdex=convertBytesToSmall(item,234);
				vit=convertBytesToSmall(item,238);
				bonusvit=convertBytesToSmall(item,240);
				intl=convertBytesToSmall(item,244);
				bonusintl=convertBytesToSmall(item,246);
				agi=convertBytesToSmall(item,250);
				bonusagi=convertBytesToSmall(item,252);
				healhp=convertBytesToSmall(item,256);
				life=convertBytesToSmall(item,260);
				bonuslife=convertBytesToSmall(item,264);
				healmana=convertBytesToSmall(item,272);
				mana=convertBytesToSmall(item,276);
				bonusmana=convertBytesToSmall(item,280);
				stam=0;
				bonusstam=convertBytesToSmall(item,292);
				atkscs=convertBytesToFloat(item,296);
				bonusatkscs=convertBytesToFloat(item,300);
				defscs=convertBytesToFloat(item,308);
				bonusdefscs=convertBytesToFloat(item,312);
				critchance=convertBytesToFloat(item,320);
				bonuscritchance=convertBytesToFloat(item,324);
				critdmg=convertBytesToSmall(item,332);
				bonuscritdmg=convertBytesToSmall(item,334);
				mindmg=convertBytesToInteger(item,338);
				maxdmg=convertBytesToInteger(item,344);
				offpower=convertBytesToSmall(item,350);
				bonusoffpower=convertBytesToSmall(item,352);
				defpower=convertBytesToSmall(item,356);
				bonusdefpower=convertBytesToSmall(item,358);
				pvpdmginc=convertBytesToByte(item,368);
				timetoexpire=convertBytesToSmall(item,400);
				//TELEMAP 408
				//TELEX 412
				//TELEY 416
				seteffectid=convertBytesToInteger(item,420);
				amountsetpieces=convertBytesToByte(item,424);
				movespeed=convertBytesToByte(item,428);
				bufficon1 = 0; bufftime1 = 0; buffvalue1 = 0; bufficon2 = 0; bufftime2 = 0; buffvalue2 = 0;
				if(item.size() > 456) {
					// TODO Fix old items?
					if(bufficon1 < 300) { //bugged items (old items from pandora) will be ignored
						bufficon1 = convertBytesToSmall(item,456);
						bufftime1 = convertBytesToSmall(item,458);
						buffvalue1 = convertBytesToByte(item,460);
						if(item.size() > 464) {
							bufficon2 = convertBytesToSmall(item,464);
							bufftime2 = convertBytesToSmall(item,466);
							buffvalue2 = convertBytesToByte(item,468);
						}
					}
				}
						
				dao.addItem(sql,itemid,baseid,category,againsttype,bonustype,typedmg,bonustypedmg,atkrange,
						price,isconsumable,ispermanent,equipslot,width,height,minlvl,maxlvl,reqstr,reqdex,reqvit,reqint,reqagi,
						warusable,sinusable,mageusable,monkusable,faction,upgradelvl,str,bonusstr,dex,bonusdex,vit,bonusvit,
						intl,bonusintl,agi,bonusagi,healhp,life,bonuslife,healmana,mana,bonusmana,stam,bonusstam,atkscs,bonusatkscs,defscs,
						bonusdefscs,critchance,bonuscritchance,critdmg,bonuscritdmg,mindmg,maxdmg,offpower,bonusoffpower,
						defpower,bonusdefpower,pvpdmginc,timetoexpire,seteffectid,amountsetpieces,movespeed,bufficon1,bufftime1,buffvalue1,
						bufficon2,bufftime2,buffvalue2);
					
				count++;
				
				if(count%100==0)
					System.out.print("["+(count)+"]");
				
			}
			
		}else{
			
			while(!items.isEmpty()){
				
				item=items.removeFirst();
				
				itemid=convertBytesToInteger(item,52);
				baseid=convertBytesToInteger(item,128);
				category=convertBytesToInteger(item,136);
				againsttype=convertBytesToInteger(item,140);
				bonustype=convertBytesToInteger(item,144);
				typedmg=convertBytesToInteger(item,148);
				bonustypedmg=convertBytesToInteger(item,152);
				atkrange=convertBytesToFloat(item,156);
				price=convertBytesToInteger(item,164);
				isconsumable=convertBytesToByte(item,168);
				ispermanent=convertBytesToByte(item,169);
				equipslot=convertBytesToByte(item,170);
				width=convertBytesToByte(item,171);
				height=convertBytesToByte(item,172);
				minlvl=convertBytesToByte(item,173);
				maxlvl=convertBytesToByte(item,174);
				reqstr=convertBytesToSmall(item,176);
				reqdex=convertBytesToSmall(item,178);
				reqvit=convertBytesToSmall(item,180);
				reqint=convertBytesToSmall(item,182);
				reqagi=convertBytesToSmall(item,184);
				warusable=convertBytesToByte(item,187);
				sinusable=convertBytesToByte(item,188);
				mageusable=convertBytesToByte(item,189);
				monkusable=convertBytesToByte(item,190);
				faction=convertBytesToByte(item,196);
				upgradelvl=convertBytesToByte(item,213);
				str=convertBytesToSmall(item,214);
				bonusstr=convertBytesToSmall(item,216);
				dex=convertBytesToSmall(item,218);
				bonusdex=convertBytesToSmall(item,220);
				vit=convertBytesToSmall(item,222);
				bonusvit=convertBytesToSmall(item,224);
				intl=convertBytesToSmall(item,226);
				bonusintl=convertBytesToSmall(item,228);
				agi=convertBytesToSmall(item,230);
				bonusagi=convertBytesToSmall(item,232);
				healhp=convertBytesToSmall(item,238);
				life=convertBytesToSmall(item,240);
				bonuslife=convertBytesToSmall(item,244);
				healmana=convertBytesToSmall(item,246);
				mana=convertBytesToSmall(item,248);
				bonusmana=convertBytesToSmall(item,252);
				stam=convertBytesToSmall(item,256);
				bonusstam=convertBytesToSmall(item,260);
				atkscs=convertBytesToFloat(item,268);
				bonusatkscs=convertBytesToFloat(item,272);
				defscs=convertBytesToFloat(item,276);
				bonusdefscs=convertBytesToFloat(item,280);
				critchance=convertBytesToFloat(item,284);
				bonuscritchance=convertBytesToFloat(item,288);
				critdmg=convertBytesToSmall(item,292);
				bonuscritdmg=convertBytesToSmall(item,294);
				mindmg=convertBytesToInteger(item,296);
				maxdmg=convertBytesToInteger(item,300);;
				offpower=convertBytesToSmall(item,304);
				bonusoffpower=convertBytesToSmall(item,306);
				defpower=convertBytesToSmall(item,308);
				bonusdefpower=convertBytesToSmall(item,310);
				pvpdmginc=convertBytesToByte(item,316);
				timetoexpire=convertBytesToSmall(item,348);
				seteffectid=convertBytesToInteger(item,368);
				amountsetpieces=convertBytesToByte(item,372);
				movespeed=convertBytesToByte(item,376);
						
				dao.addItem(sql,itemid,baseid,category,againsttype,bonustype,typedmg,bonustypedmg,atkrange,
						price,isconsumable,ispermanent,equipslot,width,height,minlvl,maxlvl,reqstr,reqdex,reqvit,reqint,reqagi,
						warusable,sinusable,mageusable,monkusable,faction,upgradelvl,str,bonusstr,dex,bonusdex,vit,bonusvit,
						intl,bonusintl,agi,bonusagi,healhp,life,bonuslife,healmana,mana,bonusmana,stam,bonusstam,atkscs,bonusatkscs,defscs,
						bonusdefscs,critchance,bonuscritchance,critdmg,bonuscritdmg,mindmg,maxdmg,offpower,bonusoffpower,
						defpower,bonusdefpower,pvpdmginc,timetoexpire,seteffectid,amountsetpieces,movespeed, 0, 0, 0, 0, 0, 0);
					
				count++;
				
				if(count%100==0)
					System.out.print("["+(count)+"]");
				
			}
			
		}
		
		System.out.println("Parsing into SQL done");
		
	}
	
}
