package chat.chatCommandHandlers;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import Connections.Connection;
import Player.Character;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import chat.ChatCommandExecutor;

public class ThreadDebug implements ChatCommandExecutor {
	private ThreadMXBean tbean = ManagementFactory.getThreadMXBean();
	
	private int needsCommandPower;
	
	public ThreadDebug(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	@Override
	public void execute(String[] parameters, Connection source) {
		
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		long[] tdlock = this.tbean.findDeadlockedThreads();
		if(tdlock != null) {
			System.out.println("[!] FOUND DEADLOCK [!]");
			ThreadInfo[] threads = tbean.getThreadInfo(tdlock, true, true);
			for (ThreadInfo t : threads) {
				System.out.println(t.toString());
			}
		} else {
			System.out.println("[!] No threads in deadlock state [!]");
		}
		
	}

}
