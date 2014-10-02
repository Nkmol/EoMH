package Database;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * 
 * A little hack which will cause DriverManager to think that the Driver was loaded by system ClassLoader
 * while in fact the driver is being loaded dynamically run time. 
 *
 */

class DriverWrapper implements Driver {
	private Driver driver;

	public DriverWrapper(Driver d) {
		this.driver = d;
	}

	public boolean acceptsURL(String url) throws SQLException {
		return this.driver.acceptsURL(url);
	}

	public Connection connect(String url, Properties info) throws SQLException {
		return this.driver.connect(url, info);
	}

	public int getMajorVersion() {
		return this.driver.getMajorVersion();
	}

	public int getMinorVersion() {
		return this.driver.getMinorVersion();
	}

	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return this.driver.getPropertyInfo(url, info);
	}

	public boolean jdbcCompliant() {
		return this.driver.jdbcCompliant();
	}


	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

}
