package Lobby;


import Configuration.ConfigurationManager;
import ServerCore.ServerFacade;

public class StartLobby {
	public static void main(String[] args) {
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
		start = ConfigurationManager.init(file, false);
		if (start) { 
			ConfigurationManager.setProcessName("Lobby");
			ServerFacade.getInstance();
		}
	}
	public static void usage(){
		System.out.println("Usage: java Lobby.StartLobby <options>");
		System.out.println("Available options:");
		System.out.println("-f, --file [filename]    Specify configuration file to use");
		System.out.println("-h, --help               Show help");
	}
}
