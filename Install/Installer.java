package Install;

import java.io.Console;
import java.io.File;
import java.sql.Driver;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import logging.ServerLogger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Configuration.Configuration;
import Configuration.ConfigurationManager;
import Database.InstallDAO;
import Database.RuntimeDriverLoader;
import Database.SQLconnection;
import Parser.ItemParser;
import Parser.ItemParserSQL;
import Parser.ItemsetParser;
import Parser.ItemsetParserSQL;
import Parser.LvlexpParser;
import Parser.LvlexpParserSQL;
import Parser.MacroParser;
import Parser.MacroParserSQL;
import Parser.MapParser;
import Parser.MapParserSQL;
import Parser.MobParser;
import Parser.MobParserSQL;
import Parser.MobSpawnsParser;
import Parser.MobSpawnsParserSQL;
import Parser.NpcSpawnsParser;
import Parser.NpcSpawnsParserSQL;
import Parser.SkillParser;
import Parser.SkillParserSQL;

public class Installer {
        private Configuration conf;
        private Console con;
        private boolean confOnly;
        private boolean noConf;
        private String filename;
        private InstallDAO dao;
        
        public Installer(boolean only, boolean no, String file){
                ConfigurationManager.initInstall();
                this.conf = ConfigurationManager.getConf("GameServer");
                this.con = System.console();
                this.confOnly = only;
                this.noConf = no;
                this.filename = file;
                ServerLogger.getInstance().info(this, "Installation starting");
                this.dao = InstallDAO.getInstance();
                this.start();
                 
        }
        private void start(){
                String in = this.conf.getVar("driver");
                System.out.println("Starting OpenHeroes install process");
                System.out.print("Testing MySQL driver.. ");
                        while(!this.driverTest(in)){ 
                                System.out.print("Enter location of driver: ");
                                in = this.con.readLine();
                                this.conf.setVar("driver", in);
                        }
                System.out.println("Success");
                boolean b = false;
                while(!b){
                        System.out.print("Enter your MySQL server ip address:");
                        in = this.con.readLine();
                        this.conf.setVar("host", in);
                        System.out.print("Enter your MySQL username:");
                        in = this.con.readLine();
                        this.conf.setVar("username", in);
                        System.out.print("Enter your MySQL password:");
                        in = new String(this.con.readPassword());
                        this.conf.setVar("password", in);
                        System.out.print("Enter name of your database:");
                        in = this.con.readLine();
                        this.conf.setVar("db", in);
        
                        b = this.dao.isEmpty();
                        if (!b){
                                System.out.println("Try again? [Y/N]");
                                in = this.con.readLine();
                                if (!in.toLowerCase().trim().contentEquals("y")){ System.out.println("Terminating, install failed"); return; }
                        }
                }
                System.out.println("Connection successfully established");
                if (!this.confOnly){
                        System.out.println("Checking for previous installs...");
                        
                        boolean bol[] = this.checkTables();
                
                        System.out.print("Creating tables..");
                        if (bol[0]) if (!this.dao.createAccountTable()) { System.out.println("Failed to create table \"accounts\"... terminating, install failed"); return; }
                        if (bol[3]) if (!this.dao.createMapTable()){ System.out.println("Failed to create table \"maps\".. terminating, install failed"); return; }
                        if (bol[2]) if (!this.dao.createItemsTable()){ System.out.println("Failed to create table \"items\".. terminating, install failed"); return; }
                        if (bol[1]) if (!this.dao.createCharacterTable()) { System.out.println("Failed to create table \"character\".. terminating, install failed"); return; }
                        if (bol[4]) if (!this.dao.createMobDataTable()) { System.out.println("Failed to create table \"mobData\".. terminating, install failed"); return; }
                        if (bol[5]) if (!this.dao.createMobsTable()) { System.out.println("Failed to create table \"mobs\".. terminating, install failed"); return; }
                        if (bol[6]) if (!this.dao.createEquipmentTable()) { System.out.println("Failed to create table \"equipments\".. terminating, install failed"); return; }
                        if (bol[7]) if (!this.dao.createInventoryTable()) { System.out.println("Failed to create table \"inventories\".. terminating, install failed"); return; }
                        if (bol[8]) if (!this.dao.createSkillsTable()){ System.out.println("Failed to create table \"skills\".. terminating, install failed"); return; }
                        if (bol[9]) if (!this.dao.createCharSkillsTable()){ System.out.println("Failed to create table \"charskills\".. terminating, install failed"); return; }
                        if (bol[10]) if (!this.dao.createCharSkillbarsTable()){ System.out.println("Failed to create table \"charskillbars\".. terminating, install failed"); return; }
                        if (bol[11]) if (!this.dao.createLvlsTable()){ System.out.println("Failed to create table \"lvls\".. terminating, install failed"); return; }
                        if (bol[12]) if (!this.dao.createGamemasterTable()){ System.out.println("Failed to create table \"gamemaster\".. terminating, install failed"); return; }
                        if (bol[13]) if (!this.dao.createNpcSpawnsTable()) { System.out.println("Failed to create table \"npcSpawns\".. terminating, install failed"); return; }
                        if (bol[14]) if (!this.dao.createCharBuffTable()) { System.out.println("Failed to create table \"activebuffs\".. terminating, install failed"); return; }
                        if (bol[15]) if (!this.dao.createItemsetsTable()) { System.out.println("Failed to create table \"itemset\".. terminating, install failed"); return; }
                        if (bol[16]) if (!this.dao.createMacroTable()) { System.out.println("Failed to create table \"macro\".. terminating, install failed"); return; }
                        System.out.println("Done");
                
                        if(bol[2]){
                        	System.out.print("Creating item entries. ");
                        	this.createItems();
                        	System.out.println("Done");
                        }
                        if(bol[8]){
                        	System.out.print("Creating skill entries. ");
                        	this.createSkills();
                        	System.out.println("Done");
                        }
                        if(bol[3]){
                        	System.out.print("Creating map entries. ");
                        	this.createMaps();
                        	System.out.println("Done");
                        }
                        if(bol[0]){
                        	System.out.print("Creating the default user account..");
                        	this.createDefaultAccount();
                        	System.out.println("Done");
                        }
                        if(bol[4]){
                        	System.out.println("Creating mobData entries");
                        	this.createMobData();
                        	System.out.println("Done");
                        }
                        if(bol[5]){
                        	System.out.println("Creating mobSpawn entries");
                        	this.createMobSpawns();
                        	System.out.println("Done");
                        }
                        if(bol[11]){
                        	System.out.println("Creating lvl entries");
                        	this.createLvlData();
                        	System.out.println("Done");
                        }
                        if(bol[12]){
                        	System.out.println("Creating gamemaster entries");
                        	this.createGamemasterData();
                        	System.out.println("Done");
                        }
                        if(bol[13]){
                        	System.out.println("Creating npcSpawn entries");
                        	this.createNpcSpawns();
                        	System.out.println("Done");
                        }
                        if(bol[15]){
                        	System.out.println("Creating itemset entries");
                        	this.createItemsets();
                        	System.out.println("Done");
                        }
                        if(bol[16]){
                        	System.out.println("Creating macro entries");
                        	this.createMacro();
                        	System.out.println("Done");
                        }
    
                        
                        System.out.println("ALL DONE");
                }
                if (!this.noConf)
                {
                        System.out.print("Generation configuration file..");
                        this.generateServerConf();
                }
                System.out.println("Installation complete");
        }
        private void createMobData() {
        	
        	MobParserSQL.parseMobsToSQL(dao, MobParser.getMoblistFromScr("Data/mobs.scr", 456), MobParser.getDroplistFromScr("Data/mobsitem.scr",1012,800));
        	
		}
        
        private void createMobSpawns() {
        	
        	MobSpawnsParserSQL.parseMobspawnsToSQL(dao, MobSpawnsParser.getMobspawnlistFromArr("Data/MobSpawnsMaps.txt", 20));
        	
		}
        
        private void createNpcSpawns() {
        	
        	NpcSpawnsParserSQL.parseNpcspawnsToSQL(dao, NpcSpawnsParser.getNpcspawnlistFromArr("Data/NpcSpawnsMaps.txt", 28));
        	
		}
        
        private void createLvlData() {
        	LvlexpParserSQL.parseLvlexpsToSQL(dao, LvlexpParser.getLvlexplistFromTxt("Data/lvls.txt"));
        }
        
        private void createGamemasterData() {
        	
        	int[] rank={1,2,3,4,10};
        	String[] prename={"[HERO]","GMH@","GM@","DEV@","ADMIN@"};
        	boolean[] gotGMname={false,false,true,true,true};
        	int[] commandpower={0,10,20,30,100};
        	int[] allocateGMrank={0,0,2,2,4};
        	boolean[] isPlayer={true,true,false,false,false};
        	
        	for(int i=0;i<rank.length;i++){
        		InstallDAO.getInstance().addGamemasterRank(new SQLconnection().getConnection(), rank[i], prename[i], gotGMname[i], commandpower[i], allocateGMrank[i], isPlayer[i]);
        	}
        	
        }
        
        private void createItemsets() {
        	ItemsetParserSQL.parseItemsetToSQL(dao, ItemsetParser.structurize(ItemsetParser.getItemsetlistFromTxt("Data/Itemset.txt")));
        }
        
        private void createMacro() {
        	MacroParserSQL.parseMacroToSQL(dao, MacroParser.getMacrolistFromTxt("Data/Macro.txt"));
        }
        
		private void createDefaultAccount() {
        	this.dao.CreateAccount(new SQLconnection().getConnection(),1, "127.0.0.1", "localhost", "localhost", 1);
			
		}
		private void createMaps() {
			MapParserSQL.parseMapsToSQL(dao, MapParser.getMaplistFromTxt("Data/maps.txt"));
			/*XMLParser par = new XMLParser("Data/Maps.xml");
            Element root = par.getRoot();
			Element el = null;
			if (par.getElementName(root) == "Maps"){
				int id, x,y, asize,gsize, pool;
				String name;
				
				int entries = par.getNodeCount(root, "Map");
				System.out.print("Found " + entries + " entries, processing");
				
				Connection con=new SQLconnection().getConnection();
				
				for (int i=0; i< entries; i++){
					el = par.getElement(root, "Map", i);
					id = par.getIntValue(el, "id");
					name = par.getTextValue(el, "name");
					x = par.getIntValue(el, "mapx");
					y = par.getIntValue(el, "mapy");
					asize = par.getIntValue(el, "areaSize");
					gsize = par.getIntValue(el, "gridSize");
					pool = par.getIntValue(el, "mobPool");
					this.dao.addMap(con, id, name, gsize, asize, x, y , pool);
					
					System.out.print(".");
				}
			}*/
		}
		
		private void createItems() {
			
			//gmh client: 404 and 2
			ItemParserSQL.parseItemsToSQL(dao, ItemParser.getItemlistFromScr("Data/items.scr", 456), 1);
			
			/*XMLParser par;
			Element root,el;
			
			int itemid,baseid,category,againsttype,bonustype,typedmg,bonustypedmg,
			price,isconsumable,ispermanent,equipslot,width,height,minlvl,maxlvl,
			reqstr,reqdex,reqvit,reqint,reqagi,warusable,sinusable,mageusable,
			monkusable,faction,upgradelvl,str,bonusstr,dex,bonusdex,vit,bonusvit,
			intl,bonusintl,agi,bonusagi,life,bonuslife,mana,bonusmana,stam,bonusstam,
			critdmg,bonuscritdmg,mindmg,maxdmg,offpower,bonusoffpower,defpower,bonusdefpower,
			pvpdmginc,timetoexpire,seteffectid,amountsetpieces,movespeed;
			float atkrange,atkscs,bonusatkscs,defscs,bonusdefscs,critchance,bonuscritchance;
			
			int entries;
			
			Connection con=new SQLconnection().getConnection();
			
			for(int j=0;j<5;j++){
			System.out.println("Itemlist "+(j+1)+" of 5");
			par = new XMLParser("Data/Items"+(j+1)+".xml");
            root = par.getRoot();
			el = null;
			if (par.getElementName(root) == "Items"){
				
				entries = par.getNodeCount(root, "record");
				System.out.print("Found " + entries + " entries, processing");
				
				for (int i=0; i< entries; i++){
					
					el = par.getElement(root, "record", i);
					
					itemid=par.getIntValue(el, "ItemID");
					baseid=par.getIntValue(el, "ItemIDOfBaseItem");
					category=par.getIntValue(el, "Category");
					againsttype=par.getIntValue(el, "AgainstType");
					bonustype=par.getIntValue(el, "BonusType");
					typedmg=par.getIntValue(el, "TypeDmg");
					bonustypedmg=par.getIntValue(el, "BonusTypeDmg");
					atkrange=Float.parseFloat(par.getTextValue(el, "AtkRange"));
					price=par.getIntValue(el, "Price");
					isconsumable=par.getIntValue(el, "IsConsumable");
					ispermanent=par.getIntValue(el, "IsPermanent");
					equipslot=par.getIntValue(el, "EquipSlot");
					width=par.getIntValue(el, "Width");
					height=par.getIntValue(el, "Height");
					minlvl=par.getIntValue(el, "MinLvl");
					maxlvl=par.getIntValue(el, "MaxLvl");
					reqstr=par.getIntValue(el, "ReqStr");
					reqdex=par.getIntValue(el, "ReqDex");
					reqvit=par.getIntValue(el, "ReqVit");
					reqint=par.getIntValue(el, "ReqInt");
					reqagi=par.getIntValue(el, "ReqAgi");
					warusable=par.getIntValue(el, "WarUsable");
					sinusable=par.getIntValue(el, "SinUsable");
					mageusable=par.getIntValue(el, "MageUsable");
					monkusable=par.getIntValue(el, "MonkUsable");
					faction=par.getIntValue(el, "Faction");
					upgradelvl=par.getIntValue(el, "UpgradeLvl");
					str=par.getIntValue(el, "Str");
					bonusstr=par.getIntValue(el, "BonusStr");
					dex=par.getIntValue(el, "Dex");
					bonusdex=par.getIntValue(el, "BonusDex");
					vit=par.getIntValue(el, "Vit");
					bonusvit=par.getIntValue(el, "BonusVit");
					intl=par.getIntValue(el, "Int");
					bonusintl=par.getIntValue(el, "BonusInt");
					agi=par.getIntValue(el, "Agi");
					bonusagi=par.getIntValue(el, "BonusAgi");
					life=par.getIntValue(el, "Life");
					bonuslife=par.getIntValue(el, "BonusLife");
					mana=par.getIntValue(el, "Mana");
					bonusmana=par.getIntValue(el, "BonusMana");
					stam=par.getIntValue(el, "Stam");
					bonusstam=par.getIntValue(el, "BonusStam");
					atkscs=Float.parseFloat(par.getTextValue(el, "AtkScs"));
					bonusatkscs=Float.parseFloat(par.getTextValue(el, "BonusAtkScs"));
					defscs=Float.parseFloat(par.getTextValue(el, "DefScs"));
					bonusdefscs=Float.parseFloat(par.getTextValue(el, "BonusDefScs"));
					critchance=Float.parseFloat(par.getTextValue(el, "CritChance"));
					bonuscritchance=Float.parseFloat(par.getTextValue(el, "BonusCritChance"));
					critdmg=par.getIntValue(el, "CritDmg");
					bonuscritdmg=par.getIntValue(el, "BonusCritDmg");
					mindmg=par.getIntValue(el, "MinDmg");
					maxdmg=par.getIntValue(el, "MaxDmg");
					offpower=par.getIntValue(el, "OffPower");
					bonusoffpower=par.getIntValue(el, "BonusOffPower");
					defpower=par.getIntValue(el, "DefPower");
					bonusdefpower=par.getIntValue(el, "BonusDefPower");
					pvpdmginc=par.getIntValue(el, "PvpDmgInc");
					timetoexpire=par.getIntValue(el, "TimeToExpire");
					seteffectid=par.getIntValue(el, "SetEffectID");
					amountsetpieces=par.getIntValue(el, "AmountSetPieces");
					movespeed=par.getIntValue(el, "MoveSpeed");
					
					this.dao.addItem(con,itemid,baseid,category,againsttype,bonustype,typedmg,bonustypedmg,atkrange,
							price,isconsumable,ispermanent,equipslot,width,height,minlvl,maxlvl,reqstr,reqdex,reqvit,reqint,reqagi,
							warusable,sinusable,mageusable,monkusable,faction,upgradelvl,str,bonusstr,dex,bonusdex,vit,bonusvit,
							intl,bonusintl,agi,bonusagi,life,bonuslife,mana,bonusmana,stam,bonusstam,atkscs,bonusatkscs,defscs,
							bonusdefscs,critchance,bonuscritchance,critdmg,bonuscritdmg,mindmg,maxdmg,offpower,bonusoffpower,
							defpower,bonusdefpower,pvpdmginc,timetoexpire,seteffectid,amountsetpieces,movespeed);
					
					if(i%100==0)
						System.out.print("["+(i)+"]");
					
				}
			}
			}*/
		}
		
		private void createSkills() {
			
			SkillParserSQL.parseSkillsToSQL(dao, SkillParser.getSkilllistFromScr("Data/skills.scr", 1500));
			
		}
		
		private boolean[] checkTables(){
        	boolean b[] = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
        	String []tables = new String[]{"accounts", "characters", "items", "maps", "mobdata", "mobs", "equipments", "inventories", "skills", "charskills", "charskillbars", "lvls", "gamemaster", "npcspawns", "charbuffs", "itemset", "macro"};
        	String in;
        	
        	for (int i =0; i < tables.length; i++){
        		if (this.dao.tableExists(tables[i])){
        			System.out.println("Table \""+tables[i]+"\" already exists");
        			System.out.println("Do you wish to overwrite?[Y/N] (Notice: All previous data will be lost)");
        			in = this.con.readLine();
        			if (in.toLowerCase().trim().contentEquals("y")){ this.dao.dropTable(i); b[i] = true;}
        			else { System.out.println("Table \""+tables[i]+"\" is unchanged"); }
            	}
        		else{
        			b[i] = true;
        		}
        	}
        	return b;
        }
        private boolean driverTest(String path){
                Driver t = new RuntimeDriverLoader().loadDriver(path, false);
                if (t != null) return true;
                return false;
        }
        private void generateServerConf(){
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = null;
                try {
                        docBuilder = docFactory.newDocumentBuilder();
                } catch (ParserConfigurationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
 
                // root element
                Document doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("GameServer");
                doc.appendChild(rootElement);
                
                //database elements
                Element database = doc.createElement("database");
                rootElement.appendChild(database);
 
                Element user = doc.createElement("username");
                user.appendChild(doc.createTextNode(this.conf.getVar("username")));
                database.appendChild(user);
                
                Element password = doc.createElement("password");
                password.appendChild(doc.createTextNode(this.conf.getVar("password")));
                database.appendChild(password);
                
                Element host = doc.createElement("host");
                host.appendChild(doc.createTextNode(this.conf.getVar("host")));
                database.appendChild(host);
                
                Element db = doc.createElement("db");
                db.appendChild(doc.createTextNode(this.conf.getVar("db")));
                database.appendChild(db);
                
                Element driver = doc.createElement("driver");
                driver.appendChild(doc.createTextNode(this.conf.getVar("driver")));
                database.appendChild(driver);
                
                
                //server elements
                Element server = doc.createElement("server");
                rootElement.appendChild(server);
                
                Element port = doc.createElement("port");
                port.appendChild(doc.createTextNode(this.conf.getVar("port")));
                server.appendChild(port);
                
                // World elements
                Element world = doc.createElement("world");
                rootElement.appendChild(server);
                
                Element mobuid = doc.createElement("MobUIDPool");
                mobuid.appendChild(doc.createTextNode("50000"));
                world.appendChild(mobuid);
                
                
                // lobby elements
                Element lobby = doc.createElement("lobby");
                rootElement.appendChild(lobby);
                
                Element cport = doc.createElement("cdpPort");
                cport.appendChild(doc.createTextNode(this.conf.getVar("cdpPort")));
                lobby.appendChild(cport);
                
                Element lport = doc.createElement("lobbyPort");
                lport.appendChild(doc.createTextNode(this.conf.getVar("lobbyPort")));
                lobby.appendChild(lport);
                
                // logging elements
                Element log = doc.createElement("Logging");
                rootElement.appendChild(log);
                
                Element loglevel = doc.createElement("logWriteLevel");
                loglevel.appendChild(doc.createTextNode("warning"));
                log.appendChild(loglevel);
                
                Element logout = doc.createElement("logOutputLevel");
                logout.appendChild(doc.createTextNode("info"));
                log.appendChild(logout);
                
                Element logfile = doc.createElement("logFile");
                logfile.appendChild(doc.createTextNode("Error.log"));
                log.appendChild(logfile);
                
                Element loglob = doc.createElement("logLobby");
                loglob.appendChild(doc.createTextNode("no"));
                log.appendChild(loglob);
                
                
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer;
                try {
                        transformer = transformerFactory.newTransformer();
                
                        DOMSource source = new DOMSource(doc);
                        StreamResult result = new StreamResult(new File(this.filename));
 
                        // Output to console for testing
                        // StreamResult result = new StreamResult(System.out);
 
                        transformer.transform(source, result);
 
                } catch (TransformerConfigurationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (TransformerException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                System.out.println("Done!");
                                
        }

}