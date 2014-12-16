package chat.chatCommandHandlers;


import java.util.LinkedList;

import Parser.FilterParser;
import Parser.FilterParserSQL;
import Parser.ItemsetParser;
import Parser.ItemsetParserSQL;
import Parser.MacroParser;
import Parser.MacroParserSQL;
import Parser.MobspecialParser;
import Parser.MobspecialParserSQL;
import Player.Character;
import GameServer.ServerPackets.ServerMessage;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Connections.Connection;
import Database.FilterDAO;
import Database.InstallDAO;
import Database.ItemDAO;
import Database.MacroDAO;
import Database.MobDAO;
import Database.ValueDescriptionDAO;
import chat.ChatCommandExecutor;

public class ImportCommand implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public ImportCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	public void execute(String[] parameters, Connection source) {
		System.out.println("Received import command!");
		
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		//----------IMPORT ITEMSETS----------
		if(parameters.length>1 && parameters[0].equals("itemset")){
			
			LinkedList<LinkedList<Object>> lines=ItemsetParser.structurize(ItemsetParser.getItemsetlistFromTxt("Data/Itemset.txt"));
			
			//only insert new elements
			if(parameters[1].equals("insertOnly")){
				if(ItemDAO.getInstance().updateAllItemsets(lines, false))
					new ServerMessage().execute("Inserted new itemsets", source);
				return;
			}
			//insert new elements and update existing items
			if(parameters[1].equals("updateAll")){
				if(ItemDAO.getInstance().updateAllItemsets(lines, true))
					new ServerMessage().execute("Inserted and updated new itemsets", source);
				return;
			}
			//delete every element in the database and insert new elements
			if(parameters[1].equals("fullReset")){
				if(ItemDAO.getInstance().deleteAllItemsets()){
					ItemsetParserSQL.parseItemsetToSQL(InstallDAO.getInstance(),lines);
					new ServerMessage().execute("Full itemset reset and upload", source);
				}
			}
		}
		
		//----------IMPORT MACROS----------
		if(parameters.length>1 && parameters[0].equals("macro")){
					
			LinkedList<LinkedList<String>> lines=MacroParser.getMacrolistFromTxt("Data/Macro.txt");
					
			//only insert new elements
			if(parameters[1].equals("insertOnly")){
				if(MacroDAO.getInstance().updateAllMacros(lines, false))
					new ServerMessage().execute("Inserted new macros", source);
				return;
			}
			//insert new elements and update existing items
			if(parameters[1].equals("updateAll")){
				if(MacroDAO.getInstance().updateAllMacros(lines, true))
					new ServerMessage().execute("Inserted and updated new macros", source);
				return;
			}
			//delete every element in the database and insert new elements
			if(parameters[1].equals("fullReset")){
				if(MacroDAO.getInstance().deleteAllMacros()){
					MacroParserSQL.parseMacroToSQL(InstallDAO.getInstance(),lines);
					new ServerMessage().execute("Full macro reset and upload", source);
				}
			}
		}
		
		//----------IMPORT FILTERS----------
		if(parameters.length>2 && parameters[0].equals("filter")){
							
			try{
				LinkedList<LinkedList<Object>> lines=FilterParser.getFilterlistFromTxt("Data/"+parameters[2]+"Filters.txt");
				
				//only insert new elements
				if(parameters[1].equals("insertOnly")){
					if(FilterDAO.getInstance().updateAllFilters(lines, false, parameters[2]))
						new ServerMessage().execute("Inserted new filters", source);
					else{throw new Exception();}
					return;
				}
				//insert new elements and update existing items
				if(parameters[1].equals("updateAll")){
					if(FilterDAO.getInstance().updateAllFilters(lines, true, parameters[2]))
						new ServerMessage().execute("Inserted and updated new filters", source);
					else{throw new Exception();}
					return;
				}
				//delete every element in the database and insert new elements
				if(parameters[1].equals("fullReset")){
					if(FilterDAO.getInstance().deleteAllFilters()){
						FilterParserSQL.parseFilterToSQL(InstallDAO.getInstance(),lines,parameters[2]);
						new ServerMessage().execute("Full filter reset and upload", source);
					}else{throw new Exception();}
				}
			}catch(Exception e){
				new ServerMessage().execute("Something went wrong", source);
			}
		}
		
		//----------IMPORT DESCRIPTIONS----------
		if(parameters.length>2 && parameters[0].equals("description")){
				
			try{
				LinkedList<LinkedList<Object>> lines=FilterParser.getDescriptionlistFromTxt("Data/"+parameters[2]+"CategoryDescriptions.txt");
			
				//only insert new elements
				if(parameters[1].equals("insertOnly")){
					if(ValueDescriptionDAO.getInstance().updateAllDescriptions(lines, false, parameters[2]))
						new ServerMessage().execute("Inserted new descriptions", source);
					else{throw new Exception();}
					return;
				}
				//insert new elements and update existing items
				if(parameters[1].equals("updateAll")){
					if(ValueDescriptionDAO.getInstance().updateAllDescriptions(lines, true, parameters[2]))
						new ServerMessage().execute("Inserted and updated new descriptions", source);
					else{throw new Exception();}
					return;
				}
				//delete every element in the database and insert new elements
				if(parameters[1].equals("fullReset")){
					if(ValueDescriptionDAO.getInstance().deleteAllDescriptions()){
						FilterParserSQL.parseDescriptionToSQL(InstallDAO.getInstance(),lines,parameters[2]);
						new ServerMessage().execute("Full description reset and upload", source);
					}else{throw new Exception();}
				}
			}catch(Exception e){
				new ServerMessage().execute("Something went wrong", source);
			}
		}
		
		//----------IMPORT PUZZLES----------
		if(parameters.length>0 && parameters[0].equals("puzzle")){
			MobDAO.deleteAllMobpuzzles();
			MobspecialParserSQL.parsePuzzlesToSQL(InstallDAO.getInstance(), MobspecialParser.structurize(MobspecialParser.getPuzzlelistFromTxt("Data/Puzzles.txt")));
			new ServerMessage().execute("Full puzzlemob reset and upload", source);
			return;
		}
		
		//----------IMPORT RANDOMNAMES----------
		if(parameters.length>0 && parameters[0].equals("randomname")){
			MobDAO.deleteAllRandomnames();
			MobspecialParserSQL.parseRandomnamesToSQL(InstallDAO.getInstance(), MobspecialParser.getNamelistFromTxt("Data/RandomNames.txt", "NAME"));
			new ServerMessage().execute("Full randomname reset and upload", source);
			return;
		}
				
		//----------IMPORT RANDOMSENTENCES----------
		if(parameters.length>0 && parameters[0].equals("randomsentence")){
			MobDAO.deleteAllRandomsentences();
			MobspecialParserSQL.parseRandomsentencesToSQL(InstallDAO.getInstance(), MobspecialParser.getNamelistFromTxt("Data/RandomSentences.txt", "SENTENCE"));
			new ServerMessage().execute("Full randomsentence reset and upload", source);
			return;
		}
		
	}
	
}
