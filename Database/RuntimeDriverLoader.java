package Database;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.logging.Level;

import logging.ServerLogger;

public class RuntimeDriverLoader {
	private ServerLogger logging = ServerLogger.getInstance();
	
	public Driver loadDriver(String path, boolean trace) {
		URL u = null;
		Driver d = null;
		try {
			u = new URL("jar:file:" + path + "!/");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String classname = "com.mysql.jdbc.Driver";
		//URLClassLoader ucl = new URLClassLoader(new URL[] { u });
		URLClassLoader ucl = URLClassLoaderHolder.getUcl(u);

		try {
			d = (Driver)Class.forName(classname, true, ucl).newInstance();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			if (trace) e1.printStackTrace();
			else this.logging.logMessage(Level.SEVERE, this, "Fatal error: Failed to initialize database driver");
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			if (trace) e1.printStackTrace();
			else this.logging.logMessage(Level.SEVERE, this, "Fatal error: Failed to initialize database driver");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			if (trace) e1.printStackTrace();
			else this.logging.logMessage(Level.SEVERE, this, "Fatal error: Database driver not found. Check your configuration for path");
			return null;
		}
		
		return d;
	}
	
	public Driver loadDriver(String path){
		return this.loadDriver(path, true);
	}
}
