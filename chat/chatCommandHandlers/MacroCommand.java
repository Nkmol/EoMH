package chat.chatCommandHandlers;


import Player.Character;
import GameServer.ServerPackets.ServerMessage;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Connections.Connection;
import Database.InstallDAO;
import Database.MacroDAO;
import chat.ChatCommandExecutor;
import chat.ChatParser;

public class MacroCommand implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public MacroCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	public void execute(String[] parameters, Connection source) {
		System.out.println("Received chat command for macros!");
		
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		InstallDAO installDao=InstallDAO.getInstance();
		MacroDAO itemDao=MacroDAO.getInstance();
		
		//----------ADD OR UPDATE A MACRO----------
		if(parameters.length>3 && parameters[0].equals("add")){
			String name=parameters[1];
			String password=parameters[2];
			if(name.length()>16 || password.length()>16){
				new ServerMessage().execute("Name/Password must be <=16 letters", source);
				return;
			}
			//old pw from db
			String oldPassword=itemDao.getMacroPassword(name);
			if(oldPassword!=null && !oldPassword.equals(password)){
				new ServerMessage().execute("Name already exists with different password", source);
				return;
			}
			
			String content="";
			for(int i=3;i<parameters.length;i++){
				content+=parameters[i];
				if(i!=parameters.length-1)
					content+=ChatParser.getInstance().getParameterDelimiter();
			}
			
			if(oldPassword==null){
				installDao.addMacro(itemDao.getSqlConnection(), name, password, content);
				new ServerMessage().execute("Added macro "+name+" with password "+password, source);
			}else{
				itemDao.updateMacro(name, content);
				new ServerMessage().execute("Updated macro "+name, source);
			}
		}
		
		//----------DELETE A MACRO----------
		if(parameters.length>2 && parameters[0].equals("del")){
			String name=parameters[1];
			String password=parameters[2];
			//old pw from db
			String oldPassword=itemDao.getMacroPassword(name);
			if(oldPassword!=null && !oldPassword.equals(password)){
				new ServerMessage().execute("Macro does not exist or wrong pw", source);
				return;
			}
			itemDao.deleteMacro(name);
			new ServerMessage().execute("Deleted macro "+name, source);
		}
		
		//----------UPDATE PASSWORD----------
		if(parameters.length>2 && parameters[0].equals("newpw")){
			String name=parameters[1];
			String password=parameters[2];
			if(itemDao.getMacroPassword(name)==null){
				new ServerMessage().execute("Macro does not exist", source);
				return;
			}
			itemDao.changeMacroPassword(name, password);
			new ServerMessage().execute("New password for macro "+name+" is "+password, source);
		}
		
	}
	
}
