package Buffs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Database.ItemDAO;
import Buffs.BuffActions.*;
import Buffs.BuffActions.buff2;

public class BuffMaster {
	
	private static Map<Integer, BuffAction> buffActions;
	
	public static void getAllBuffs() {
		buffActions = new HashMap<Integer, BuffAction>();
		//get buff action ids from db and create BuffAction objects and load them to buffActions
		List<Integer> itembuffs = ItemDAO.getInstance().getItemBuffs();
		List<Integer> skillbuffs = ItemDAO.getInstance().getSkillBuffs();
		List<Integer> finalbuffs = new ArrayList<Integer>();
		//Combine them (can have double values)
		finalbuffs.addAll(itembuffs);
		finalbuffs.addAll(skillbuffs);
		Collections.sort(finalbuffs);
		
//        try {
//        	BufferedWriter out = new BufferedWriter(new FileWriter("file.txt"));
//        	for(int i=0;i<finalbuffs.size();i++) {
//                out.write(finalbuffs.get(i).toString());
//                out.newLine();
//            }
//            out.close();
//        } catch (IOException e) { }
        
		for(int i=0;i<finalbuffs.size();i++) {
			if(!buffActions.containsKey(finalbuffs.get(i)))  {
				// Find class on name {buffid}
				Class<?> clazz = null;
				Constructor<?> ctor = null;
				Object object = null;
				try {
					clazz = Class.forName("Buffs.BuffActions.buff" + finalbuffs.get(i).toString());
				} catch (ClassNotFoundException e) {
					buffActions.put(finalbuffs.get(i), new BuffActionExample());
					// TODO Auto-generated catch block
					//e.printStackTrace();
					
					continue;
				}
				
				
//					try {
//						ctor = clazz.getConstructor(String.class);
//					} catch (NoSuchMethodException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (SecurityException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				
				try {
					object = clazz.newInstance();
					buffActions.put(finalbuffs.get(i), (BuffAction)object);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
			
		System.out.println("buff Init DONE!");
	}

	public static BuffAction getBuffAction(int buffId){
		if(buffActions.containsKey(buffId)){
			return buffActions.get(buffId);
		}
		return null;
	}
	
}
