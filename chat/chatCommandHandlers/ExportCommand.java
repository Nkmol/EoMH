package chat.chatCommandHandlers;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.ResultSet;

import Player.Character;
import GameServer.ServerPackets.ServerMessage;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Connections.Connection;
import Database.FilterDAO;
import Database.ItemDAO;
import Database.MacroDAO;
import Database.ValueDescriptionDAO;
import chat.ChatCommandExecutor;

public class ExportCommand implements ChatCommandExecutor{

	private int needsCommandPower;
	
	public ExportCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	public void execute(String[] parameters, Connection source) {
		System.out.println("Received export command!");
		
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		//----------EXPORT ITEMSETS----------
		if(parameters.length>0 && parameters[0].equals("itemset")){
			try{
				BufferedWriter out = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"/Data/Itemset.txt"));
				ResultSet rs=ItemDAO.getInstance().fetchItemsets();
				int id,amount;
				out.write(completeString("Name",16));
				out.write(completeString("Password",16));
				for(int i=0;i<60;i++){
					out.write(completeString("ItemID"+((Integer)(i+1)).toString(),9));
					out.write(completeString("Amount"+((Integer)(i+1)).toString(),9));
				}
				while(rs.next()){
					out.write("\n");
					out.write(completeString(rs.getString(1),16));
					out.write(completeString(rs.getString(2),16));
					for(int i=0;i<60;i++){
						id=rs.getInt(3+i*2);
						amount=rs.getInt(4+i*2);
						if(id!=0 && amount!=0){
							out.write(completeString(((Integer)id).toString(),9));
							out.write(completeString(((Integer)amount).toString(),9));
						}
					}
				}
				out.close();
				rs.close();
				new ServerMessage().execute("Exported itemset", source);
				return;
			}catch(Exception e){
				new ServerMessage().execute("Something went wrong", source);
				return;
			}
		}
		
		//----------EXPORT MACROS----------
		if(parameters.length>0 && parameters[0].equals("macro")){
			try{
				BufferedWriter out = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"/Data/Macro.txt"));
				ResultSet rs=MacroDAO.getInstance().fetchMacros();
				out.write(completeString("Name",16));
				out.write(completeString("Password",16));
				out.write(completeString("Content",85));
				while(rs.next()){
					out.write("\n");
					out.write(completeString(rs.getString(1),16));
					out.write(completeString(rs.getString(2),16));
					out.write(completeString(rs.getString(3),85));
				}
				out.close();
				rs.close();
				new ServerMessage().execute("Exported macros", source);
			}catch(Exception e){
				new ServerMessage().execute("Something went wrong", source);
				return;
			}
		}
		
		//----------EXPORT FILTERS----------
		if(parameters.length>1 && parameters[0].equals("filter")){
			try{
				BufferedWriter out = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"/Data/"+parameters[1]+"Filters.txt"));
				ResultSet rs=FilterDAO.getInstance().fetchFilters(parameters[1]);
				while(rs.next()){
					out.write(rs.getString(1)+",");
					out.write(rs.getString(2)+",");
					out.write(rs.getString(3)+",");
					out.write(rs.getInt(4)+",");
					out.write(rs.getInt(5)+",");
					out.write(rs.getInt(6)+",");
					if(!rs.isLast())
						out.write("\n");
				}
				out.close();
				rs.close();
				new ServerMessage().execute("Exported filters", source);
			}catch(Exception e){
				new ServerMessage().execute("Something went wrong", source);
				return;
			}
		}
		
		//----------EXPORT DESCRIPTIONS----------
		if(parameters.length>1 && parameters[0].equals("description")){
			try{
				BufferedWriter out = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"/Data/"+parameters[1]+"CategoryDescriptions.txt"));
				ResultSet rs=ValueDescriptionDAO.getInstance().fetchDescriptions();
				while(rs.next()){
					out.write(rs.getInt(1)+",");
					out.write(rs.getString(2)+",");
					if(!rs.isLast())
						out.write("\n");
				}
				out.close();
				rs.close();
				new ServerMessage().execute("Exported descriptions", source);
			}catch(Exception e){
				new ServerMessage().execute("Something went wrong", source);
				return;
			}
		}
		
	}
	
	public String completeString(String s,int letters){
		for(int i=s.length();i<letters;i++){
			s+=" ";
		}
		s+=", ";
		return s;
	}
	
}
