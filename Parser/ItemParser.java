package Parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class ItemParser extends Parser{
	
	public static LinkedList<ArrayList<Short>> getItemlistFromScr(String pathString, int itemBytesLengthMin){
		try{
			//FILE
			Path path = Paths.get(pathString);
			byte[] data = Files.readAllBytes(path);
			System.out.println("BYTES: "+data.length+" ,CREATE ITEM LISTS...");
			
			//GET ITEMS TO LIST
			LinkedList<Short> itemBytes=new LinkedList<Short>();
			LinkedList<ArrayList<Short>> items=new LinkedList<ArrayList<Short>>();
			int byteatm=0;
			short shortdata=0;
			int counter;
			int id1;
			int id2;
			ArrayList<Short> wholeItem;
			
			for(int i=0;i<1000;i++){
				shortdata=(short)data[i];
				if(shortdata<0)
					shortdata+=256;
				itemBytes.add(shortdata);
				byteatm++;
			}
			
			while(byteatm<data.length){
				
				wholeItem=new ArrayList<Short>();
				counter=0;
				while(counter<itemBytesLengthMin && itemBytes.size()>0){
					
					wholeItem.add(itemBytes.removeFirst().shortValue());
					counter++;
				}
				if(itemBytes.size()>131){
					id1=itemBytes.get(52)+itemBytes.get(53)*256+itemBytes.get(54)*65536+itemBytes.get(55)*16777216;
					id2=itemBytes.get(128)+itemBytes.get(129)*256+itemBytes.get(130)*65536+itemBytes.get(131)*16777216;
				}else{
					id1=0;
					id2=0;
				}
				while((id1<200000000 || id1>300000000 || id2<200000000 || id2>300000000)&& !itemBytes.isEmpty()){
					if(!itemBytes.isEmpty())
						wholeItem.add(itemBytes.removeFirst().shortValue());
					if(itemBytes.size()>131){
						id1=itemBytes.get(52)+itemBytes.get(53)*256+itemBytes.get(54)*65536+itemBytes.get(55)*16777216;
						id2=itemBytes.get(128)+itemBytes.get(129)*256+itemBytes.get(130)*65536+itemBytes.get(131)*16777216;
					}
				}
				items.add(wholeItem);
				while(byteatm<data.length && itemBytes.size()<1000){
					shortdata=(short)data[byteatm];
					if(shortdata<0)
						shortdata+=256;
					itemBytes.add(shortdata);
					byteatm++;
				}
				
			}
			
			counter=0;
			for(Iterator<ArrayList<Short>> i=items.iterator();i.hasNext();){
				int id=convertBytesToInteger(i.next(),52);
				if(id<200000000 || id>300000000){
					i.remove();
					System.out.println("wrong item detected at index "+counter);
				}
				counter++;
			}
			
			System.out.println("FOUND "+items.size()+" ITEMS");
			return items;
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	
	
	public static void createItemlist(String pathString, LinkedList<ArrayList<Short>> items, int client){
		try{
			System.out.println("CREATE ITEM LIST TEXT FILE...");
			
			//CREATE ITEM LIST TEXT FILE
			BufferedWriter out = new BufferedWriter(new FileWriter(pathString));
			ArrayList<Short> item;
			
			if(client==1){
				out.write("ID        , NAME                                                , ");
				out.write("DESCRIPTION                                                             , ");
				out.write("BASEID    , TEXTUREID , CATEGORY  , AGAINSTTYP, ATYPEBONUS, TYPEDMG   , TDMGBONUS , ");
				out.write("ATKRANGE  , NPCPRICE  , STACKABLE , PERMANENT , EQUIPSLOT , WIDTH     , HEIGHT    , ");
				out.write("MINLVL    , MAXLVL    , REQSTR    , REQDEX    , REQVIT    , REQINT    , REQAGI    , ");
				out.write("WARUSABLE , SINUSABLE , MAGEUSABLE, MONKUSABLE, FACTION   , UPGRADELVL, STR       , BONUSSTR  , ");
				out.write("DEX       , BONUSDEX  , VIT       , BONUSVIT  , INT       , BONUSINT  , AGI       , ");
				out.write("BONUSAGI  , HEALHP    , LIFE      , BONUSLIFE , HEALMANA  , MANA      , BONUSMANA , STAMINA   , BONUSSTAM , ");
				out.write("ATKSCS    , BONUSATKSC, DEFSCS    , BONUSDEFSC, CRITCHANCE, BONUSCRCH , CRITDMG   , ");
				out.write("BONUSCRITD, MINDMG    , MAXDMG    , OFFPOWER  , BONUSOFFP , DEFPOWER  , BONUSDEFP , ");
				out.write("PVPDMGINC , TIMETOEXPI, SETID     , SETPIECES , MOVESPEED , BUFFICON1 , BUFFTIME1 , BUFFVALUE1 , BUFFICON2 , BUFFTIME2 , BUFFVALUE2");
				out.newLine();
				while(!items.isEmpty()){
					item=items.removeFirst();
					//ID
					writeIntegerString(out, item, 52);
					//NAME
					writeString(out, item, 0, 52);
					//DESCRIPTION
					writeString(out, item, 56, 72);
					//BASEID
					writeIntegerString(out, item, 128);
					//TEXTUREID
					writeIntegerString(out, item, 132);
					//CATEGORY
					writeIntegerString(out, item, 136);
					//AGAINSTTYPE
					writeIntegerString(out, item, 140);
					//AGAINSTTYPEBONUS
					writeIntegerString(out, item, 144);
					//TYPEDMG
					writeIntegerString(out, item, 148);
					//TYPEDMGBONUS
					writeIntegerString(out, item, 152);
					//ATKRANGE
					writeFloatString(out, item, 164);
					//NPCPRICE
					writeIntegerString(out, item, 176);
					//ISSTACKABLE
					writeByteString(out, item, 180);
					//ISPERMANENT
					writeByteString(out, item, 181);
					//EQUIPSLOT
					writeByteString(out, item, 182);
					//WIDTH
					writeByteString(out, item, 183);
					//HEIGHT
					writeByteString(out, item, 184);
					//MINLVL
					writeByteString(out, item, 185);
					//MAXLVL
					writeByteString(out, item, 186);
					//REQSTR
					writeSmallString(out, item, 188);
					//REQDEX
					writeSmallString(out, item, 190);
					//REQVIT
					writeSmallString(out, item, 192);
					//REQINT
					writeSmallString(out, item, 194);
					//REQAGI
					writeSmallString(out, item, 196);
					//WARUSABLE
					writeByteString(out, item, 199);
					//SINUSABLE
					writeByteString(out, item, 200);
					//MAGEUSABLE
					writeByteString(out, item, 201);
					//MONKUSABLE
					writeByteString(out, item, 202);
					//FACTION
					writeByteString(out, item, 208);
					//UPGRADELVL
					writeByteString(out, item, 225);
					//STR
					writeSmallString(out, item, 226);
					//BONUSSTR
					writeSmallString(out, item, 228);
					//DEX
					writeSmallString(out, item, 232);
					//BONUSDEX
					writeSmallString(out, item, 234);
					//VIT
					writeSmallString(out, item, 238);
					//BONUSVIT
					writeSmallString(out, item, 240);
					//INT
					writeSmallString(out, item, 244);
					//BONUSINT
					writeSmallString(out, item, 246);
					//AGI
					writeSmallString(out, item, 250);
					//BONUSAGI
					writeSmallString(out, item, 252);
					//HEALHP
					writeSmallString(out, item, 256);
					//LIFE
					writeSmallString(out, item, 260);
					//BONUSLIFE
					writeSmallString(out, item, 264);
					//HEALMANA
					writeSmallString(out, item, 272);
					//MANA
					writeSmallString(out, item, 276);
					//BONUSMANA
					writeSmallString(out, item, 280);
					//STAMINA?
					out.write("0         , ");
					//BONUSSTAMINA
					writeSmallString(out, item, 292);
					//ATKSCS
					writeFloatString(out, item, 296);
					//BONUSATKSCS
					writeFloatString(out, item, 300);
					//DEFSCS
					writeFloatString(out, item, 308);
					//BONUSDEFSCS
					writeFloatString(out, item, 312);
					//CRITCHANCE
					writeFloatString(out, item, 320);
					//BONUSCRITCHANCE
					writeFloatString(out, item, 324);
					//CRITDMG
					writeSmallString(out, item, 332);
					//BONUSCRITDMG
					writeSmallString(out, item, 334);
					//MINDMG
					writeIntegerString(out, item, 338);
					//MAXDMG
					writeIntegerString(out, item, 344);
					//OFFPOWER
					writeSmallString(out, item, 350);
					//BONUSOFFPOWER
					writeSmallString(out, item, 352);
					//DEFPOWER
					writeSmallString(out, item, 356);
					//BONUSDEFPOWER
					writeSmallString(out, item, 358);
					//PVPDMGINC
					writeByteString(out, item, 368);
					//TIMETOEXPIRE
					writeSmallString(out, item, 400);
					//TELEMAP
					writeIntegerString(out, item, 408);
					//TELEX
					writeFloatString(out, item, 412);
					//TELEY
					writeFloatString(out, item, 416);
					//SETEFFECTID
					writeIntegerString(out, item, 420);
					//SETPIECES
					writeByteString(out, item, 424);
					//MOVESPEED
					writeByteString(out, item, 428);
					if(item.size() > 456) {
						//BUFFICON
						writeByteString(out, item, 456);
						//BUFFTIME
						writeSmallString(out, item, 458);
						//BUFFVALUE
						writeByteString(out, item, 460);
						//System.out.println(item.size() + " ID: " + writeIntegerString(out, item, 52));
						if(item.size() > 464) {
							//BUFFICON2
							writeByteString(out, item, 464);
							//BUFFTIME2
							writeSmallString(out, item, 466);
							//BUFFVALUE2
							writeByteString(out, item, 468);
						}
						else {
							out.write("0         , ");
							out.write("0         , ");
							out.write("0         , ");
						}
					}
					else {
						out.write("0         , ");
						out.write("0         , ");
						out.write("0         , ");
						out.write("0         , ");
						out.write("0         , ");
						out.write("0         , ");
					}
					out.newLine();
				}
			}else{
				out.write("ID        , NAME                                                , ");
				out.write("DESCRIPTION                                                             , ");
				out.write("BASEID    , TEXTUREID , CATEGORY  , AGAINSTTYP, ATYPEBONUS, TYPEDMG   , TDMGBONUS , ");
				out.write("ATKRANGE  , NPCPRICE  , STACKABLE , PERMANENT , EQUIPSLOT , WIDTH     , HEIGHT    , ");
				out.write("MINLVL    , MAXLVL    , REQSTR    , REQDEX    , REQVIT    , REQINT    , REQAGI    , ");
				out.write("WARUSABLE , SINUSABLE , MAGEUSABLE, MONKUSABLE, FACTION   , UPGRADELVL, STR       , BONUSSTR  , ");
				out.write("DEX       , BONUSDEX  , VIT       , BONUSVIT  , INT       , BONUSINT  , AGI       , ");
				out.write("BONUSAGI  , LIFE      , BONUSLIFE , MANA      , BONUSMANA , STAMINA   , BONUSSTAM , ");
				out.write("ATKSCS    , BONUSATKSC, DEFSCS    , BONUSDEFSC, CRITCHANCE, BONUSCRCH , CRITDMG   , ");
				out.write("BONUSCRITD, MINDMG    , MAXDMG    , OFFPOWER  , BONUSOFFP , DEFPOWER  , BONUSDEFP , ");
				out.write("PVPDMGINC , TIMETOEXPI, SETID     , SETPIECES , MOVESPEED");
				out.newLine();
				while(!items.isEmpty()){
					item=items.removeFirst();
					//ID
					writeIntegerString(out, item, 52);
					//NAME
					writeString(out, item, 0, 52);
					//DESCRIPTION
					writeString(out, item, 56, 72);
					//BASEID
					writeIntegerString(out, item, 128);
					//TEXTUREID
					writeIntegerString(out, item, 132);
					//CATEGORY
					writeIntegerString(out, item, 136);
					//AGAINSTTYPE
					writeIntegerString(out, item, 140);
					//AGAINSTTYPEBONUS
					writeIntegerString(out, item, 144);
					//TYPEDMG
					writeIntegerString(out, item, 148);
					//TYPEDMGBONUS
					writeIntegerString(out, item, 152);
					//ATKRANGE
					writeFloatString(out, item, 156);
					//NPCPRICE
					writeIntegerString(out, item, 164);
					//ISSTACKABLE
					writeByteString(out, item, 168);
					//ISPERMANENT
					writeByteString(out, item, 169);
					//EQUIPSLOT
					writeByteString(out, item, 170);
					//WIDTH
					writeByteString(out, item, 171);
					//HEIGHT
					writeByteString(out, item, 172);
					//MINLVL
					writeByteString(out, item, 173);
					//MAXLVL
					writeByteString(out, item, 174);
					//REQSTR
					writeSmallString(out, item, 176);
					//REQDEX
					writeSmallString(out, item, 178);
					//REQVIT
					writeSmallString(out, item, 180);
					//REQINT
					writeSmallString(out, item, 182);
					//REQAGI
					writeSmallString(out, item, 184);
					//WARUSABLE
					writeByteString(out, item, 187);
					//SINUSABLE
					writeByteString(out, item, 188);
					//MAGEUSABLE
					writeByteString(out, item, 189);
					//MONKUSABLE
					writeByteString(out, item, 190);
					//FACTION
					writeByteString(out, item, 196);
					//UPGRADELVL
					writeByteString(out, item, 213);
					//STR
					writeSmallString(out, item, 214);
					//BONUSSTR
					writeSmallString(out, item, 216);
					//DEX
					writeSmallString(out, item, 218);
					//BONUSDEX
					writeSmallString(out, item, 220);
					//VIT
					writeSmallString(out, item, 222);
					//BONUSVIT
					writeSmallString(out, item, 224);
					//INT
					writeSmallString(out, item, 226);
					//BONUSINT
					writeSmallString(out, item, 228);
					//AGI
					writeSmallString(out, item, 230);
					//BONUSAGI
					writeSmallString(out, item, 232);
					//LIFE
					writeSmallString(out, item, 240);
					//BONUSLIFE
					writeSmallString(out, item, 244);
					//MANA
					writeSmallString(out, item, 248);
					//BONUSMANA
					writeSmallString(out, item, 252);
					//STAMINA
					writeSmallString(out, item, 256);
					//BONUSSTAMINA
					writeSmallString(out, item, 260);
					//ATKSCS
					writeFloatString(out, item, 268);
					//BONUSATKSCS
					writeFloatString(out, item, 272);
					//DEFSCS
					writeFloatString(out, item, 276);
					//BONUSDEFSCS
					writeFloatString(out, item, 280);
					//CRITCHANCE
					writeFloatString(out, item, 284);
					//BONUSCRITCHANCE
					writeFloatString(out, item, 288);
					//CRITDMG
					writeSmallString(out, item, 292);
					//BONUSCRITDMG
					writeSmallString(out, item, 294);
					//MINDMG
					writeIntegerString(out, item, 296);
					//MAXDMG
					writeIntegerString(out, item, 300);
					//OFFPOWER
					writeSmallString(out, item, 304);
					//BONUSOFFPOWER
					writeSmallString(out, item, 306);
					//DEFPOWER
					writeSmallString(out, item, 308);
					//BONUSDEFPOWER
					writeSmallString(out, item, 310);
					//PVPDMGINC
					writeByteString(out, item, 316);
					//TIMETOEXPIRE
					writeSmallString(out, item, 348);
					//SETEFFECTID
					writeIntegerString(out, item, 368);
					//SETPIECES
					writeByteString(out, item, 372);
					//MOVESPEED
					writeByteString(out, item, 376);

					out.newLine();
				}
			}
			
		    out.close();
		    
		    System.out.println("DONE");
			
			
		}catch(Exception e){e.printStackTrace();}
	}

	public static void main(String args[]){
		
		//---------- CHANGE THESE VALUES ----------
		
		//PATH
		String path1String=System.getProperty("user.dir")+"/Data/items.scr";
		String path2String=System.getProperty("user.dir")+"/Data/items.txt";
		//CLIENT 1=KOREAN 2=GMH
		int client=1;
		
		//--------------------------


		System.out.println("START [256mb heap needed]");
		int itemBytesLengthMin;
		if(client==1)
			itemBytesLengthMin=456; //Eastern potion = 428
		else
			itemBytesLengthMin=404;
		createItemlist(path2String, getItemlistFromScr(path1String, itemBytesLengthMin), client);
		
	}
}