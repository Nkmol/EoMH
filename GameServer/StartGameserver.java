package GameServer;

import java.util.LinkedList;

import logging.ServerLogger;
import Configuration.ConfigurationManager;
import ServerCore.ServerFacade;
import Skills.SkillMaster;
import Buffs.BuffMaster;
import Database.CharacterDAO;
import Database.GamemasterDAO;
import Database.MobDAO;
import Database.NpcDAO;
import Database.StartupDAO;
import ExperimentalStuff.IntelligentCommands;


public class StartGameserver {
		public static void main(String[] args) {
			ConfigurationManager.setProcessName("GameServer");
			boolean start = true;
			String file = new String("server.xml");
			for (int i=0; i< args.length; i++){
				if (args[i].contentEquals("-f") || args[i].contentEquals("--file")){
					file = args[i+1];
					i++;
				}
				else if (args[i].contentEquals("-h") || args[i].contentEquals("--help")){
					usage();
					return;
				}
				else {
					System.out.println("option " + args[i] + " not supported, exiting");
					usage();
					return;
				}
			}
			start = ConfigurationManager.init(file, true);
			if (start) start();
		}
		public static void usage(){
			System.out.println("Usage: java GameServer.StartGamerserver <options>");
			System.out.println("Available options:");
			System.out.println("-f, --file [filename]    Specify configuration file to use");
			System.out.println("-h, --help               Show help");
		}
		public static void start(){
			ServerLogger.getInstance().info(StartGameserver.class, "Server started");
			ServerMaster.init("ServerDD");
			StartupDAO.loadMaps();
			SkillMaster.loadAllSkills();
			BuffMaster.getAllBuffs();
			GamemasterDAO.loadGamemasterRanks();
			CharacterDAO.loadCharacterLvl(167);
			MobDAO.loadMobpuzzles();
			MobDAO.loadRandomnames();
			MobDAO.loadRandomsentences();
			MobDAO.initMobs();
			NpcDAO.initNpcs();
			LinkedList<String> categories=new LinkedList<String>();
			categories.add("item");
			categories.add("mob");
			IntelligentCommands.init(categories);
			ServerFacade.getInstance(); 
			
		}

}
