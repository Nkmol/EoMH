package GameServer;

import logging.ServerLogger;
import Configuration.ConfigurationManager;
import ServerCore.ServerFacade;
import Skills.SkillMaster;
import Database.CharacterDAO;
import Database.GamemasterDAO;
import Database.MobDAO;
import Database.NpcDAO;
import Database.StartupDAO;


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
			StartupDAO.loadMaps();
			SkillMaster.loadAllSkills();
			GamemasterDAO.loadGamemasterRanks();
			CharacterDAO.loadCharacterLvl(81);
			MobDAO.initMobs();
			NpcDAO.initNpcs();
			ServerFacade.getInstance(); 
			
		}

}
