package chat.chatCommandHandlers;


import Player.Character;
import GameServer.ServerMaster;
import GameServer.ServerPackets.ServerMessage;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Connections.Connection;
import Database.ServerControlDAO;
import chat.ChatCommandExecutor;

public class EventCommand implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public EventCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	public void execute(String[] parameters, Connection source) {
		System.out.println("Received event command!");
		
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		if(parameters.length>0){
			
			if(parameters.length>1 && parameters[0].equals("start")){
				if(ServerControlDAO.getInstance().changeServerControlEvent(parameters[1])){
					ServerMaster.updateEvent(parameters[1]);
					new ServerMessage().execute("Changed the servers event to "+parameters[1],source);
				}else{
					new ServerMessage().execute("Failed to change the event to "+parameters[1],source);
				}
				return;
			}
			
			if(parameters.length>11 && parameters[0].equals("add")){
				if(ServerControlDAO.getInstance().addEvent(parameters[1], Float.parseFloat(parameters[2]),
						Float.parseFloat(parameters[3]), Float.parseFloat(parameters[4]), Float.parseFloat(parameters[5]),
						Float.parseFloat(parameters[6]), Integer.parseInt(parameters[7]), Integer.parseInt(parameters[8]),
						Integer.parseInt(parameters[9]), Integer.parseInt(parameters[10]), Float.parseFloat(parameters[11]),
						parameters[12]))
					new ServerMessage().execute("Added the event "+parameters[1],source);
				else
					new ServerMessage().execute("Failed to add the event "+parameters[1],source);
				return;
			}
			
			if(parameters.length>11 && parameters[0].equals("update")){
				if(ServerControlDAO.getInstance().updateEvent(parameters[1], Float.parseFloat(parameters[2]),
						Float.parseFloat(parameters[3]), Float.parseFloat(parameters[4]), Float.parseFloat(parameters[5]),
						Float.parseFloat(parameters[6]), Integer.parseInt(parameters[7]), Integer.parseInt(parameters[8]),
						Integer.parseInt(parameters[9]), Integer.parseInt(parameters[10]), Float.parseFloat(parameters[11]),
						parameters[12]))
					new ServerMessage().execute("Updated the event "+parameters[1],source);
				else
					new ServerMessage().execute("Failed to update the event "+parameters[1],source);
			}
			
			if(parameters.length>1 && parameters[0].equals("delete")){
				if(ServerControlDAO.getInstance().deleteEvent(parameters[1]))
					new ServerMessage().execute("Deleted the event "+parameters[1],source);
				else
					new ServerMessage().execute("Failed to delete the event "+parameters[1],source);
			}
			
		}
		
	}
	
}
