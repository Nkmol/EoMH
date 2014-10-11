package chat;

import java.util.HashMap;
import java.util.Map;

import Connections.Connection;
import chat.chatCommandHandlers.*;

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
		this.commandList.put("item", new ItemSpawner(20));
		this.commandList.put("itemblitz", new ItemBlitz(30));
		this.commandList.put("announce", new GMChat(10));
		this.commandList.put("heal", new HealCommand(20));
		this.commandList.put("npc", new NPCSpawn(30));
		this.commandList.put("debug", new ThreadDebug(30));
		this.commandList.put("setstats", new ChatSetStats(20));
		this.commandList.put("doll", new ChatDollCommand(30));
		this.commandList.put("reskillmeplx", new Reskill(20));
		this.commandList.put("gainexp", new GainExp(20));
		this.commandList.put("lvlup", new LvlUp(20));
		this.commandList.put("faction", new ChangeFactionCommand(20));
		this.commandList.put("rejoin", new RejoinWorldCommand(30));
		this.commandList.put("changesize", new ChangeSizeCommand(20));
		this.commandList.put("setkao", new SetKaoCommand(20));
		this.commandList.put("setGMrank", new SetGMrankCommand(20));
		//this.commandList.put("startevent", new StartEventCommand(100));
		this.commandList.put("setfame", new SetFameCommand(20));
		this.commandList.put("spawnmob", new SpawnMobCommand(20));
		this.commandList.put("printstats", new PrintStatsCommand(30));
		this.commandList.put("vendorpoints", new VenderPoints(30));
		this.commandList.put("pd", new PartyDuelCommand(0));
		this.commandList.put("showcommands", new ShowCommands(0));
		this.commandList.put("tp", new teleportCommand(20));
		this.commandList.put("bufficon", new bufficon(30));
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

	public boolean parseAndExecuteChatCommand(String msg, Connection con) {
		System.out.println("Parsing a chat command: " + msg);
		String[] commands = msg.split(this.cmdDelimiter);
		for(int i=0;i<commands.length;i++) {
			String[] splat = commands[i].split(paramDelimiter);
			if(this.commandList.containsKey(splat[0])) {
				String[] params = new String[splat.length-1];
				for(int ri=1;ri<splat.length;ri++) {
					params[ri-1] = splat[ri]; 
				}
				this.commandList.get(splat[0]).execute(params, con);
				return true;
			}
		}
		return false;
	}
	
}
