package Parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class MapParser extends Parser{
	
	public static LinkedList<LinkedList<Object>> getMaplistFromTxt(String pathString){
		
		try{
			//FILE
			Path path = Paths.get(pathString);
			byte[] data = Files.readAllBytes(path);
			System.out.println("BYTES: "+data.length+" ,CREATE MAP LISTS...");
			
			//GET ITEMS TO LIST
			LinkedList<Short> mapBytes=new LinkedList<Short>();
			LinkedList<LinkedList<Object>> maps=new LinkedList<LinkedList<Object>>();
			int byteatm=0;
			short shortdata=0;
			int id, xgrids, ygrids, xorigin, yorigin, areasize, size;
			String name;
			boolean minus;
			LinkedList<Object> wholeMap;
			
			for(int i=0;i<data.length;i++){
				shortdata=(short)data[i];
				if(shortdata<0)
					shortdata+=256;
				mapBytes.add(shortdata);
			}
			
			for(int i=0;i<86;i++){
				if(!mapBytes.isEmpty())
					mapBytes.removeFirst();
				byteatm+=1;
			}
			while(byteatm<data.length){
				
				wholeMap=new LinkedList<Object>();
				
				//-----ID-----
				LinkedList<Short> idList=new LinkedList<Short>();
				for(int i=0; i<10; i++){
					shortdata=mapBytes.removeFirst();
					if(isInteger(shortdata))
						idList.add((short)(shortdata-48));
					byteatm++;
				}
				id=0;
				size=idList.size();
				for(int i=0;i<size;i++){
					id+=idList.get(i)*Math.pow(10, size-i-1);
				}
				for(int i=0;i<2;i++){
					if(!mapBytes.isEmpty())
						mapBytes.removeFirst();
					byteatm+=1;
				}
				
				//-----NAME-----
				name="";
				for(int i=0; i<10; i++){
					shortdata=mapBytes.removeFirst();
					if(shortdata!=32)
						name+=(char)shortdata;
					byteatm++;
				}
				for(int i=0;i<2;i++){
					if(!mapBytes.isEmpty())
						mapBytes.removeFirst();
					byteatm+=1;
				}
				
				//-----XGRIDS-----
				LinkedList<Short> xgridsList=new LinkedList<Short>();
				for(int i=0; i<10; i++){
					shortdata=mapBytes.removeFirst();
					if(isInteger(shortdata))
						xgridsList.add((short)(shortdata-48));
					byteatm++;
				}
				xgrids=0;
				size=xgridsList.size();
				for(int i=0;i<size;i++){
					xgrids+=xgridsList.get(i)*Math.pow(10, size-i-1);
				}
				for(int i=0;i<2;i++){
					if(!mapBytes.isEmpty())
						mapBytes.removeFirst();
					byteatm+=1;
				}
				
				//-----YGRIDS-----
				LinkedList<Short> ygridsList=new LinkedList<Short>();
				for(int i=0; i<10; i++){
					shortdata=mapBytes.removeFirst();
					if(isInteger(shortdata))
						ygridsList.add((short)(shortdata-48));
					byteatm++;
				}
				ygrids=0;
				size=ygridsList.size();
				for(int i=0;i<size;i++){
					ygrids+=ygridsList.get(i)*Math.pow(10, size-i-1);
				}
				for(int i=0;i<2;i++){
					if(!mapBytes.isEmpty())
						mapBytes.removeFirst();
					byteatm+=1;
				}
				
				//-----XORIGIN-----
				minus=mapBytes.get(0)==45;
				LinkedList<Short> xoriginList=new LinkedList<Short>();
				for(int i=0; i<10; i++){
					shortdata=mapBytes.removeFirst();
					if(isInteger(shortdata))
						xoriginList.add((short)(shortdata-48));
					byteatm++;
				}
				xorigin=0;
				size=xoriginList.size();
				for(int i=0;i<size;i++){
					xorigin+=xoriginList.get(i)*Math.pow(10, size-i-1);
				}
				for(int i=0;i<2;i++){
					if(!mapBytes.isEmpty())
						mapBytes.removeFirst();
					byteatm+=1;
				}
				if (minus)
					xorigin*=-1;
				
				//-----YORIGIN-----
				minus=mapBytes.get(0)==45;
				LinkedList<Short> yoriginList=new LinkedList<Short>();
				for(int i=0; i<10; i++){
					shortdata=mapBytes.removeFirst();
					if(isInteger(shortdata))
						yoriginList.add((short)(shortdata-48));
					byteatm++;
				}
				yorigin=0;
				size=yoriginList.size();
				for(int i=0;i<size;i++){
					yorigin+=yoriginList.get(i)*Math.pow(10, size-i-1);
				}
				for(int i=0;i<2;i++){
					if(!mapBytes.isEmpty())
						mapBytes.removeFirst();
					byteatm+=1;
				}
				if (minus)
					yorigin*=-1;
				
				//-----AREASIZE-----
				LinkedList<Short> areasizeList=new LinkedList<Short>();
				for(int i=0; i<10; i++){
					shortdata=mapBytes.removeFirst();
					if(isInteger(shortdata))
						areasizeList.add((short)(shortdata-48));
					byteatm++;
				}
				areasize=0;
				size=areasizeList.size();
				for(int i=0;i<size;i++){
					areasize+=areasizeList.get(i)*Math.pow(10, size-i-1);
				}
				for(int i=0;i<4;i++){
					if(!mapBytes.isEmpty())
						mapBytes.removeFirst();
					byteatm+=1;
				}
				
				wholeMap.add(id);
				wholeMap.add(name);
				wholeMap.add(xgrids);
				wholeMap.add(ygrids);
				wholeMap.add(xorigin);
				wholeMap.add(yorigin);
				wholeMap.add(areasize);
				maps.add(wholeMap);
				
				System.out.println("ID: "+id+" NAME: "+name+" XGRIDS: "+xgrids+" YGRIDS: "+ygrids+" XORIGIN: "+xorigin+" YORIGIN: "+yorigin+" AREASIZE: "+areasize);
				
			}
			
			return maps;
			
		}catch(Exception e){e.printStackTrace();}
		return null;
		
	}
	
	private static boolean isInteger(short b){
		return (b>=48 && b<=57);
	}
	
	public static void main(String args[]){
		
		//---------- CHANGE THESE VALUES ----------
		
		//PATH
		String pathString=System.getProperty("user.dir")+"/Data/maps.txt";
		
		//--------------------------


		System.out.println("START");
		
		getMaplistFromTxt(pathString);
		
	}
	
}
