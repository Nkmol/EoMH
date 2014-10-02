package Database;


import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import Configuration.Configuration;
import Configuration.ConfigurationManager;
import logging.ServerLogger;


public class SQLconnection {
	  private Configuration conf;
	  private boolean trace = true;
	  private ServerLogger logging;
	  
	  public SQLconnection(){
		  this.conf = ConfigurationManager.getConf("GameServer");
		  this.logging =  ServerLogger.getInstance();
	  }
	  public SQLconnection(boolean trace){
		  this.trace = trace;
		  this.logging =  ServerLogger.getInstance();
		  this.conf = ConfigurationManager.getConf("GameServer");
	  }

	  public Connection getConnection() {
	    String url = "jdbc:mysql://" + conf.getVar("host")  + "/" + conf.getVar("db");
	    
	    Driver driver = new RuntimeDriverLoader().loadDriver(conf.getVar("driver"));
	    Connection conn = null;
	    
		try {
			DriverWrapper dm=new DriverWrapper(driver);
			DriverManager.registerDriver(dm);
			conn = DriverManager.getConnection(url, conf.getVar("username"), conf.getVar("password"));
			DriverManager.deregisterDriver(dm);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			if (!trace){
				int i = e.getErrorCode();
				if (i == 0) { logging.logMessage(Level.SEVERE, this, "Fatal error: Database connection failed"); }
				else if (i == 1045){ logging.logMessage(Level.SEVERE, this, "Fatal error("+i+"): Access denied"); }
				else if (i == 1044){ logging.logMessage(Level.SEVERE, this, "Fatal error("+i+"): Access denied"); }
				else if (i == 1049){ logging.logMessage(Level.SEVERE, this, "Fatal error: Database doesnt exist"); }
				else { logging.logMessage(Level.SEVERE, this, "Fatal error: " + i);}
			}
			else logging.logMessage(Level.SEVERE, this, "Fatal error: " + e.getMessage());		
		} 
		catch (Exception e){
			logging.logMessage(Level.SEVERE, this, "Fatal error: " + e.getMessage());
		}
	    return conn;
	  }
}
