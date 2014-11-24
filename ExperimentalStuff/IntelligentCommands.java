package ExperimentalStuff;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import Tools.StringTools;
import Database.FilterDAO;
import Database.ValueDescriptionDAO;

public class IntelligentCommands {
	
	//                    category         name      attributes
	private static HashMap<String,HashMap<String,LinkedList<Object>>> filters;
	//                    category         name   value
	private static HashMap<String,HashMap<String,Integer>> descriptions;
	//table name for category
	private static HashMap<String, String> tablenames;
	//id name for category
	private static HashMap<String, String> idnames;
	
	public static void init(LinkedList<String> categories){
		loadFilters(categories);
		loadCategoryDescriptions(categories);
		tablenames=new HashMap<String, String>();
		idnames=new HashMap<String, String>();
		//items
		tablenames.put("item", "items");
		idnames.put("item", "itemid");
		//mobs
		tablenames.put("mob", "mobdata");
		idnames.put("mob", "mobID");
	}

	private static void loadFilters(LinkedList<String> categories){
		filters=new HashMap<String,HashMap<String,LinkedList<Object>>>();
		for(Iterator<String> i=categories.iterator();i.hasNext();){
			HashMap<String,LinkedList<Object>> specificFilters=new HashMap<String,LinkedList<Object>>();
			String category=i.next();
			filters.put(category, specificFilters);
			ResultSet rs=FilterDAO.getInstance().fetchFilters(category);
			try{
				while(rs.next()){
					LinkedList<Object> content=new LinkedList<Object>();
					content.add(rs.getString("sqlName"));
					content.add(rs.getLong("minValue"));
					content.add(rs.getLong("mxValue"));
					content.add(rs.getLong("standardValue"));
					
					specificFilters.put(rs.getString("command"), content);
				}
				rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private static void loadCategoryDescriptions(LinkedList<String> categories){
		descriptions=new HashMap<String,HashMap<String,Integer>>();
		for(Iterator<String> i=categories.iterator();i.hasNext();){
			HashMap<String,Integer> specificDescriptions=new HashMap<String,Integer>();
			descriptions.put(i.next(), specificDescriptions);
			ResultSet rs=ValueDescriptionDAO.getInstance().fetchDescriptions();
			try{
				while(rs.next()){
					int descValue=rs.getInt("descValue");
					
					specificDescriptions.put(rs.getString("description"), descValue);
				}
				rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static LinkedList<Integer> getIdsByCommand(Connection con, String category, String nameSystem) throws Exception{
		LinkedList<Integer> ids=new LinkedList<Integer>();
		ResultSet rs=getSqlResultsetFromCommand(con, category, nameSystem);
		while(rs.next()){
			//add Ids to list
			ids.add(rs.getInt(1));
		}
		rs.close();
		return ids;
	}
	
	private static ResultSet getSqlResultsetFromCommand(Connection con, String category, String nameSystem) throws Exception{
		PreparedStatement ps=buildSqlStatementByAttributes(con, category, getFilteredSqlAttributes(category, nameSystem));
		ResultSet rs=ps.executeQuery();
		return rs;
	}
	
	private static PreparedStatement buildSqlStatementByAttributes(Connection con, String category, LinkedList<HashMap<String,Object>> attributes) throws Exception{
		if(attributes==null || attributes.isEmpty()){
			throw new CommandException("Command failed");
		}
		if(!tablenames.containsKey(category) || !idnames.containsKey(category)){
			throw new CommandException("Category does not exist");
		}
		String tablename=tablenames.get(category);
		String idname=idnames.get(category);
		
		String sqlStatement="SELECT "+idname+" FROM "+tablename+" WHERE";
		
		boolean started=false;
		for(Iterator<HashMap<String,Object>> it=attributes.iterator();it.hasNext();){
			HashMap<String,Object> attribute=it.next();
			if(attribute.containsKey("=")){
				if(started)
					sqlStatement+=" AND";
				sqlStatement+=" "+attribute.get("name");
				sqlStatement+="="+attribute.get("=");
				started=true;
			}
			if(attribute.containsKey("<")){
				if(started)
					sqlStatement+=" AND";
				sqlStatement+=" "+attribute.get("name");
				sqlStatement+="<"+attribute.get("<");
				started=true;
			}
			if(attribute.containsKey(">")){
				if(started)
					sqlStatement+=" AND";
				sqlStatement+=" "+attribute.get("name");
				sqlStatement+=">"+attribute.get(">");
				started=true;
			}
		}
		sqlStatement+=";";
		
		System.out.println(sqlStatement);
		
		PreparedStatement ps = con.prepareStatement(sqlStatement);
		return ps;
	}
	
	private static LinkedList<HashMap<String,Object>> getFilteredSqlAttributes(String category, String nameSystem){
		//create attributes
		LinkedList<HashMap<String,Object>> attributes=getNameSystemAttributes(category, nameSystem);
		//filter for existing attributes
		filterRealAttributes(category, attributes);
		//correct min and max values
		correctMinMaxAttributes(category, attributes);
		//translate to sqlName
		translateAttributeNames(category, attributes);
		return attributes;
	}
	
	private static void translateAttributeNames(String category, LinkedList<HashMap<String,Object>> realAttributes){
		if(realAttributes==null)
			return;
		if(!filters.containsKey(category)){
			realAttributes=null;
			return;
		}
		HashMap<String,LinkedList<Object>> filtersByCategory=filters.get(category);
		for(Iterator<HashMap<String,Object>> it=realAttributes.iterator();it.hasNext();){
			HashMap<String,Object> attribute=it.next();
			//translate to sqlName
			attribute.put("name", filtersByCategory.get(attribute.get("name")).get(0));
		}
	}
	
	private static void correctMinMaxAttributes(String category, LinkedList<HashMap<String,Object>> realAttributes){
		if(realAttributes==null)
			return;
		if(!filters.containsKey(category)){
			realAttributes=null;
			return;
		}
		HashMap<String,LinkedList<Object>> filtersByCategory=filters.get(category);
		for(Iterator<HashMap<String,Object>> it=realAttributes.iterator();it.hasNext();){
			HashMap<String,Object> attribute=it.next();
			LinkedList<Object> content=filtersByCategory.get(attribute.get("name"));
			//correct the min and max values of the filters
			correctMinMaxValues(attribute,(Long)content.get(1),(Long)content.get(2));
		}
	}
	
	private static void filterRealAttributes(String category, LinkedList<HashMap<String,Object>> nameSystemAttributes){
		if(nameSystemAttributes==null)
			return;
		if(!filters.containsKey(category)){
			nameSystemAttributes=null;
			return;
		}
		HashMap<String,LinkedList<Object>> filtersByCategory=filters.get(category);
		for(Iterator<HashMap<String,Object>> it=nameSystemAttributes.iterator();it.hasNext();){
			HashMap<String,Object> attribute=it.next();
			//remove non existing attributes
			if(!filtersByCategory.containsKey(attribute.get("name"))){
				it.remove();
			}
		}
		if(filtersByCategory.isEmpty())
			filtersByCategory=null;
	}
	
	private static LinkedList<HashMap<String,Object>> getNameSystemAttributes(String category, String nameSystem){
		LinkedList<HashMap<String,Object>> nameSystemAttributes=new LinkedList<HashMap<String,Object>>();
		
		//DESCRIPTION
		//category must be known
		if(descriptions!=null && descriptions.containsKey(category)){
			//search for a category in the nameSystem
			String description=searchForDescriptions(category, nameSystem);
			if(description!=null){
				//add the category
				HashMap<String,Object> element=new HashMap<String,Object>();
				element.put("name", "Category");
				element.put("=", descriptions.get(category).get(description));
				nameSystemAttributes.add(element);
				if(description.length()<nameSystem.length())
					nameSystem=nameSystem.substring(description.length());
				else
					nameSystem="";
			}
		}
		
		//ATTRIBUTES
		LinkedList<String> attributes=getFullAttributes(nameSystem);
		for(Iterator<String> i=attributes.iterator();i.hasNext();){
			HashMap<String,Object> structuredAttribute=getAttributeStructured(i.next());
			//check for logical errors
			if(!isLogicalFilter(structuredAttribute))
				structuredAttribute=null;
			//only add valid command parts
			if(structuredAttribute!=null)
				nameSystemAttributes.add(structuredAttribute);
		}
		
		return nameSystemAttributes;
	}
	
	//find a description string in the nameSystem
	private static String searchForDescriptions(String category, String nameSystem){
		String description=null;
		HashMap<String,Integer> descValues=descriptions.get(category);
		Set<String> keys=descValues.keySet();
		Iterator<String> i=keys.iterator();
		boolean found=false;
		//compare start of nameSystem with all categories
		while(!found && i.hasNext()){
			String s=i.next();
			if (nameSystem.startsWith(s)){
				found=true;
				description=s;
			}
		}
		return description;
	}
	
	private static LinkedList<String> getFullAttributes(String nameSystem){
		LinkedList<String> fullAttributes=new LinkedList<String>();
		while(!nameSystem.equals("")){
			String attribute="";
			//filter must not start with a value
			if(StringTools.isInteger(Character.toString(nameSystem.charAt(0)))){
				return fullAttributes;
			}
			//get attribute name
			while(!nameSystem.equals("") && !StringTools.isInteger(Character.toString(nameSystem.charAt(0)))){
				//filter must not end with attribute name
				if(nameSystem.length()>1){
					attribute+=nameSystem.charAt(0);
					nameSystem=nameSystem.substring(1);
				}else{
					return fullAttributes;
				}
			}
			//get attribute "=" value
			while(!nameSystem.equals("") && StringTools.isInteger(Character.toString(nameSystem.charAt(0)))){
				attribute+=nameSystem.charAt(0);
				//filters can end here
				if(nameSystem.length()>1){
					nameSystem=nameSystem.substring(1);
				}else{
					nameSystem="";
				}
			}
			boolean hasSmallerOrLarger=false;
			//does a "Larger" follows the string?
			if(nameSystem.indexOf("Larger")==0){
				attribute+=nameSystem.substring(0,6);
				if(nameSystem.length()>6){
					nameSystem=nameSystem.substring(6);
				}else{
					nameSystem="";
				}
				hasSmallerOrLarger=true;
			}else
			//does a "Smaller" follows the string?
			if(nameSystem.indexOf("Smaller")==0){
				attribute+=nameSystem.substring(0,7);
				if(nameSystem.length()>7){
					nameSystem=nameSystem.substring(7);
				}else{
					nameSystem="";
				}
				hasSmallerOrLarger=true;
			}
			//search for another value if there was a "Smaller" or "Larger"
			if(hasSmallerOrLarger){
				if(nameSystem.equals(""))
					return fullAttributes;
				while(!nameSystem.equals("") && StringTools.isInteger(Character.toString(nameSystem.charAt(0)))){
					attribute+=nameSystem.charAt(0);
					//filters can end here
					if(nameSystem.length()>1){
						nameSystem=nameSystem.substring(1);
					}else{
						nameSystem="";
					}
				}
			}
			fullAttributes.add(attribute);
		}
		return fullAttributes;
	}
	
	private static HashMap<String,Object> getAttributeStructured(String attribute){
		HashMap<String,Object> attributeStructured=new HashMap<String,Object>();
		
		String attributeName="";
		
		int indexLarger=attribute.indexOf("Larger");
		int indexSmaller=attribute.indexOf("Smaller");
		
		//only "equals" filter
		if(indexLarger==-1 && indexSmaller==-1){
			//find value and divide string to name and value
			while(!StringTools.isInteger(Character.toString(attribute.charAt(0)))){
				//filter must not end here
				if(attribute.length()>1){
					attributeName+=attribute.charAt(0);
					attribute=attribute.substring(1);
				}else{
					return null;
				}
			}
			attributeStructured.put("=", Long.parseLong(attribute));
		}else{
			//"Smaller" and "Larger" filters
			if(indexLarger!=-1 && indexSmaller!=-1){
				int i=0;
				//filter name must not contain a value
				while(i<indexLarger && i<indexSmaller){
					if(StringTools.isInteger(Character.toString(attribute.charAt(i))))
						return null;
					i++;
				}
				//which is first?
				if(indexLarger<indexSmaller){
					//no name found
					if(indexLarger==0)
						return null;
					LinkedList<Object> largerSmallerAttribute=getLargerSmallerAttribute(attribute);
					attributeName=(String)largerSmallerAttribute.removeFirst();
					attributeStructured.put(">", largerSmallerAttribute.removeFirst());
					attributeStructured.put("<", largerSmallerAttribute.removeFirst());
				}else{
					//no name found
					if(indexSmaller==0)
						return null;
					LinkedList<Object> smallerLargerAttribute=getSmallerLargerAttribute(attribute);
					attributeName=(String)smallerLargerAttribute.removeFirst();
					attributeStructured.put("<", smallerLargerAttribute.removeFirst());
					attributeStructured.put(">", smallerLargerAttribute.removeFirst());
				}
				
			}else{
				//only "Larger"
				if(indexLarger!=-1){
					int i=0;
					//filter name must not contain a value
					while(i<indexLarger){
						if(StringTools.isInteger(Character.toString(attribute.charAt(i))))
							return null;
						i++;
					}
					//no name found
					if(indexLarger==0)
						return null;
					LinkedList<Object> largerAttribute=getLargerAttribute(attribute);
					attributeName=(String)largerAttribute.removeFirst();
					attributeStructured.put(">", (Long)largerAttribute.removeFirst());
				//only "Smaller"
				}else{
					int i=0;
					//filter name must not contain a value
					while(i<indexSmaller){
						if(StringTools.isInteger(Character.toString(attribute.charAt(i))))
							return null;
						i++;
					}
					//no name found
					if(indexSmaller==0)
						return null;
					LinkedList<Object> smallerAttribute=getSmallerAttribute(attribute);
					attributeName=(String)smallerAttribute.removeFirst();
					attributeStructured.put("<", (Long)smallerAttribute.removeFirst());
				}
			}
		}
		
		attributeStructured.put("name", attributeName);
				
		return attributeStructured;
	}
	
	private static LinkedList<Object> getLargerAttribute(String attribute){
		LinkedList<Object> largerAttribute=new LinkedList<Object>();
		//add attribute name
		largerAttribute.add(attribute.substring(0,attribute.indexOf("Larger")));
		//add value
		largerAttribute.add(Long.parseLong(attribute.substring(attribute.indexOf("Larger")+6,attribute.length())));
		return largerAttribute;
	}
	
	private static LinkedList<Object> getSmallerAttribute(String attribute){
		LinkedList<Object> smallerAttribute=new LinkedList<Object>();
		//add attribute name
		smallerAttribute.add(attribute.substring(0,attribute.indexOf("Smaller")));
		//add value
		smallerAttribute.add(Long.parseLong(attribute.substring(attribute.indexOf("Smaller")+7,attribute.length())));
		return smallerAttribute;
	}
	
	private static LinkedList<Object> getLargerSmallerAttribute(String attribute){
		LinkedList<Object> largerSmallerAttribute=new LinkedList<Object>();
		//add attribute name
		largerSmallerAttribute.add(attribute.substring(0,attribute.indexOf("Larger")));
		//add value larger
		largerSmallerAttribute.add(Long.parseLong(attribute.substring(attribute.indexOf("Larger")+6,attribute.indexOf("Smaller"))));
		//add value smaller
		largerSmallerAttribute.add(Long.parseLong(attribute.substring(attribute.indexOf("Smaller")+7,attribute.length())));
		return largerSmallerAttribute;
	}
	
	private static LinkedList<Object> getSmallerLargerAttribute(String attribute){
		LinkedList<Object> smallerLargerAttribute=new LinkedList<Object>();
		//add value
		smallerLargerAttribute.add(attribute.substring(0,attribute.indexOf("Smaller")));
		//add value smaller
		smallerLargerAttribute.add(Long.parseLong(attribute.substring(attribute.indexOf("Smaller")+7,attribute.indexOf("Larger"))));
		//add value larger
		smallerLargerAttribute.add(Long.parseLong(attribute.substring(attribute.indexOf("Larger")+6,attribute.length())));
		return smallerLargerAttribute;
	}
	
	private static boolean isLogicalFilter(HashMap<String,Object> filter){
		//filter must not have "=" and "<" or ">" at the same time
		if(filter.containsKey("=") && (filter.containsKey("<") || filter.containsKey(">")))
			return false;
		//check for mathematical logic of "<" and ">"
		if(filter.containsKey("<") && filter.containsKey(">") && (Long)filter.get("<")-1<=(Long)filter.get(">"))
			return false;
		return true;
	}
	
	private static void correctMinMaxValues(HashMap<String,Object> filter, long minValue, long maxValue){
		if(filter.containsKey("=")){
			if((Long)filter.get("=")<minValue)
				filter.put("=", minValue);
			if((Long)filter.get("=")>maxValue)
				filter.put("=", maxValue);
		}
		//+1 for "<"
		if(filter.containsKey("<")){
			if((Long)filter.get("<")<minValue+1)
				filter.put("<", minValue+1);
			if((Long)filter.get("<")>maxValue+1)
				filter.put("<", maxValue+1);
		}
		//-1 for ">"
		if(filter.containsKey(">")){
			if((Long)filter.get(">")<minValue-1)
				filter.put(">", minValue-1);
			if((Long)filter.get(">")>maxValue-1)
				filter.put(">", maxValue-1);
		}
	}
	
	/*public static void main(String args[]){
		//----------TEST----------
		LinkedList<HashMap<String,Object>> test=getNameSystemAttributes("item", "rtzLarger4Smaller3Smaller3Larger34dfg4Larger4dfg");
		if (test.isEmpty()){
			System.out.println("Command failed! [no valid filters]");
			return;
		}
		for(Iterator<HashMap<String,Object>> i=test.iterator();i.hasNext();){
			HashMap<String,Object> test2=i.next();
			if(test2.containsKey("name"))
				System.out.println("name is: "+test2.get("name"));
			if(test2.containsKey("="))
				System.out.println("= is: "+test2.get("="));
			if(test2.containsKey("<"))
				System.out.println("< is: "+test2.get("<"));
			if(test2.containsKey(">"))
				System.out.println("> is: "+test2.get(">"));
		}
	}*/
	
}
