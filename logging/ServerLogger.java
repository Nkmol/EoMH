package logging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import Configuration.Configuration;
import Configuration.ConfigurationManager;

public class ServerLogger {
	
	private Logger log;
	private String file;
	private static ServerLogger instance;
	private List<Level> outputLevel;
	private boolean logAble;
	
	private ServerLogger(){
		this.logAble = true;
		this.log = Logger.getLogger("GameServer");
		Configuration conf = ConfigurationManager.getConf("Logging");
		this.file = conf.getVar("logFile");
		if (conf.getVar("logLobby").contentEquals("no") && !ConfigurationManager.getProcessName().contentEquals("GameServer")){
			this.logAble = false;
		}
		Level lvl = Level.WARNING;
		Level out = Level.INFO;
		
		if (conf.getVar("logLevel").contentEquals("severe")) lvl = Level.SEVERE;
		if (conf.getVar("logLevel").contentEquals("info")) lvl = Level.INFO;
		if (conf.getVar("logLevel").contentEquals("fine")) lvl = Level.FINE;
		if (conf.getVar("logLevel").contentEquals("off")) out = Level.OFF;
		
		if (conf.getVar("logOutputLevel").contentEquals("severe")) out = Level.SEVERE;
		if (conf.getVar("logOutputLevel").contentEquals("warning")) out = Level.WARNING;
		if (conf.getVar("logOutputLevel").contentEquals("fine")) out = Level.FINE;
		if (conf.getVar("logOutputLevel").contentEquals("off")) out = Level.OFF;
		
		try {
			if (this.logAble){
				FileHandler fh = new FileHandler(this.file, true);
				this.log.addHandler(fh);
				this.log.setLevel(lvl); //only WARNING/SEVERE messages get logged
				fh.setFormatter(new SimpleFormatter());
			}
			this.outputLevel = new ArrayList<Level>();
			this.outputLevel.add(out); //output INFO level messages in console, but do not log them
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ERROR: Failed to open log file:" + this.file);
			e.printStackTrace();
		}
		
	}
	public static ServerLogger getInstance(){
		if (instance == null){
			instance = new ServerLogger();
		}
		return instance;
	}
	public Logger getLogger(){
		return this.log;
	}
	public void info(Object ob, String msg){
		this.logMessage(Level.INFO, ob, msg);
	}
	public void warning(Object ob, String msg){
		this.logMessage(Level.WARNING, ob, msg);
	}
	public void severe(Object ob, String msg){
		this.logMessage(Level.SEVERE, ob, msg);
	}
	
	public void logMessage(Level lvl, Object obj, String msg) {
		if(this.log.isLoggable(lvl) && this.logAble) {
			this.log.log(lvl, "Message level: [" + lvl.toString() + "] " + msg + " - in class [" + obj.getClass().getName() + "]");
		} else if(this.outputLevel.contains(lvl)){
			System.out.println("Message level: [" + lvl.toString() + "] " + msg + " - in class [" + obj.getClass().getName() + "]");
		}
		
		this.closeHandlers();
	}
	
	private void closeHandlers() {
		for(Handler h : this.log.getHandlers()) {
		    h.close();   //must call h.close or a .LCK file will remain.
		}
	}

}
