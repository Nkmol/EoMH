package chat;

import java.util.HashMap;
import java.util.Map;

import Database.MacroDAO;
import Player.ChatMaster;
import ServerCore.ServerFacade;
import chat.chatCommandHandlers.*;
import Player.Character;

public class ChatParser {
	private static volatile ChatParser instance = null;
	private final String cmdDelimiter = "!";
	private final String paramDelimiter = ":";
	private Map<String, ChatCommandExecutor> commandList = new HashMap<String, ChatCommandExecutor>();
	
	private ChatParser() {
		//10=only for helping
		//20=can harm players
		//30=can harm server
		//100=only admin
		this.commandList.put("-item", new ItemSpawner(20));
		this.commandList.put("-itemblitz", new ItemBlitz(30));
		this.commandList.put("-announce", new GMChat(10));
		this.commandList.put("-heal", new HealCommand(20));
		this.commandList.put("-npc", new NPCSpawn(30));
		this.commandList.put("-debug", new ThreadDebug(30));
		this.commandList.put("-setstats", new ChatSetStats(20));
		this.commandList.put("-doll", new ChatDollCommand(30));
		this.commandList.put("-reskillmeplx", new Reskill(20));
		this.commandList.put("-gainexp", new GainExp(20));
		this.commandList.put("-lvlup", new LvlUp(20));
		this.commandList.put("-faction", new ChangeFactionCommand(20));
		this.commandList.put("-rejoin", new RejoinWorldCommand(30));
		this.commandList.put("-changesize", new ChangeSizeCommand(20));
		this.commandList.put("-setkao", new SetKaoCommand(20));
		this.commandList.put("-setGMrank", new SetGMrankCommand(20));
		//this.commandList.put("startevent", new StartEventCommand(100));
		this.commandList.put("-setfame", new SetFameCommand(20));
		this.commandList.put("-spawnmob", new SpawnMobCommand(20));
		this.commandList.put("-printstats", new PrintStatsCommand(30));
		this.commandList.put("-vendorpoints", new VenderPoints(30));
		this.commandList.put("-pd", new PartyDuelCommand(0));
		this.commandList.put("-showcommands", new ShowCommands(0));
		this.commandList.put("-tp", new TeleportCommand(20));
		this.commandList.put("-bufficon", new Bufficon(30));
		this.commandList.put("-test", new TestCommand(30));
		this.commandList.put("-itemset", new ItemsetCommand(30));
		this.commandList.put("-export", new ExportCommand(100));
		this.commandList.put("-import", new ImportCommand(100));
		this.commandList.put("-macro", new MacroCommand(0));
		this.commandList.put("-itemlist", new ItemListCommand(30));
		this.commandList.put("-showinfo", new EnableInfosCommand(10));
	}
	
	public static synchronized ChatParser getInstance(){
		if (instance == null){
			instance = new ChatParser();
		}
		return instance;
	}

	public String getCommandDelimiter() {
		return cmdDelimiter;
	}
	
	public String getParameterDelimiter() {
		return this.paramDelimiter;
	}

	public Map<String, ChatCommandExecutor> getCommandList() {
		return commandList;
	}

	public boolean parseAndExecuteChatCommand(Character ch, String name, String text, byte type, Character target, String targetName) {
		System.out.println("Parsing a chat command: " + text);
		if(text.startsWith("<")){
			text=text.substring(1);
			String macro=MacroDAO.getInstance().getMacro(text);
			if(macro==null)
				return false;
			String[] commands = macro.split(this.cmdDelimiter);
			for(int i=0;i<commands.length;i++) {
				String[] splat = commands[i].split(paramDelimiter);
				if(macro!=null && this.commandList.containsKey(splat[0])) {
					String[] params = new String[splat.length-1];
					for(int ri=1;ri<splat.length;ri++) {
						params[ri-1] = splat[ri]; 
					}
					this.commandList.get(splat[0]).execute(params, ServerFacade.getInstance().getConnectionByChannel(ch.GetChannel()));
				}else{
					if(macro!=null){
						ChatMaster.prepareSendingChat(ch, name, macro, type, target, targetName, true);
					}
				}
			}
			return true;
		}else{
			for(int i=0;i<text.length();i++) {
				String[] splat = text.split(paramDelimiter);
				if(this.commandList.containsKey(splat[0])) {
					String[] params = new String[splat.length-1];
					for(int ri=1;ri<splat.length;ri++) {
						params[ri-1] = splat[ri]; 
					}
					this.commandList.get(splat[0]).execute(params, ServerFacade.getInstance().getConnectionByChannel(ch.GetChannel()));
					return true;
				}
			}
		}
		return false;
	}
	
}
