package Configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;



public class ConfigurationManager {
        private static final Map<String, Configuration> conf = Collections.synchronizedMap(new HashMap<String, Configuration>());

        public static boolean initAll(String confFile) {
                try {
                        conf.put("GameServer", ConfigurationManager.parseServerConf(confFile));
                        conf.put("Lobby", ConfigurationManager.parseLobbyConf(confFile));
                        conf.put("Logging",ConfigurationManager.parseLogging(confFile));
                } catch (NullPointerException e){
                        return false;
                }
                return true;
        }
        public static boolean init(String confFile, boolean gameserver){
                try{
                        if (gameserver){ 
                        	conf.put("GameServer", ConfigurationManager.parseServerConf(confFile));
                        	Configuration t = new Configuration();
                        	t.setVar("mobUIDPool", "50000");
                        	ConfigurationManager.conf.put("world", t);
                        }
                        else { 
                        	conf.put("Lobby", ConfigurationManager.parseLobbyConf(confFile));
                        }
                        
                        conf.put("Logging",ConfigurationManager.parseLogging(confFile));
                } catch (NullPointerException e){
                        return false;
                }
                return true;
        }
        private static Configuration parseLogging(String filename) {
        	Configuration conf = new Configuration();
        	XMLParser par = new XMLParser(filename);
            Element root = par.getRoot();
            if (root == null){ return null; }
            if (par.getElementName(root) == "GameServer"){
            	Element el = par.getFirstElement(root, "Logging");
                if (el != null){
                	conf.setVar("logFile", par.getTextValue(el, "logFile"));
                	conf.setVar("logLevel", par.getTextValue(el, "logWriteLevel"));
                	conf.setVar("logOutputLevel", par.getTextValue(el, "logOutputLevel"));
                	conf.setVar("logLobby", par.getTextValue(el, "logLobby"));
                }
            
            }
			return conf;
		}
		public static Configuration getConf(String name) {
                return conf.get(name);
        }
        public static Configuration parseServerConf(String filename)
        {
                Configuration conf = new Configuration();
        
                XMLParser par = new XMLParser(filename);
                Element root = par.getRoot();
                if (root == null){ return null; }
                if (par.getElementName(root) == "GameServer")
                {
                        Element el = par.getFirstElement(root, "database");
                        if (el != null){
                                conf.setVar("db", par.getTextValue(el, "db"));
                                conf.setVar("host", par.getTextValue(el, "host"));
                                conf.setVar("username", par.getTextValue(el, "username"));
                                conf.setVar("password", par.getTextValue(el, "password"));
                                conf.setVar("driver", par.getTextValue(el, "driver"));
                        }
                        else { System.out.println("Failed to load dabatase info from configuration file"); return null; }
                        el = par.getFirstElement(root, "server");
                        if (el != null){
                                conf.setVar("port", par.getTextValue(el, "port"));
                        }
                        else { System.out.println("Failed to load server info from configuration file"); return null;}
                }
                return conf;    
        }
        public static void setProcessName(String name){
        	if (!conf.containsKey("run")){
        		Configuration c = new Configuration();
        		c.setVar("running", name);
        		conf.put("run", c);
        	}
        }
        public static String getProcessName(){
        	return conf.get("run").getVar("running");
        }
        public static Configuration parseLobbyConf(String filename){
                Configuration conf = new Configuration();
                
                XMLParser par = new XMLParser(filename);
                Element root = par.getRoot();
                if (par.getElementName(root) == "GameServer")
                {
                        Element el = par.getFirstElement(root, "lobby");
                        if (el != null){
                                conf.setVar("lobbyPort", par.getTextValue(el, "lobbyPort"));
                                conf.setVar("cdpPort", par.getTextValue(el, "cdpPort"));
                        }
                        else { 
                                System.out.println("Failed to load lobby information from configuration file");
                                return null;
                        }
                }
                return conf;
        }
        public static void initInstall() {
                Configuration con = new Configuration(true);
                
                con.setVar("driver", "mysql-connector-java-5.0.8-bin.jar");
                con.setVar("port", "11000");
                con.setVar("lobbyPort", "10000");
                con.setVar("cdpPort", "10002");
                conf.put("GameServer", con);
                ConfigurationManager.setProcessName("install");
                
                Configuration config = new Configuration();
                config.setVar("logFile", "install.log");
                config.setVar("logLevel", "warning");
                config.setVar("logOutputLevel", "info");
                config.setVar("logLobby", "yes");
                conf.put("Logging", config);
                
        }

}