package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import logging.ServerLogger;
import Player.Player;


public class AccountDAO {
	private static ServerLogger log = ServerLogger.getInstance();
	private static final Connection sqlConnection = new SQLconnection().getConnection();
	/*
	 * Use this for authentication
	 * Return: Player instance if auth successful, null if failed
	 */
	public static Player authenticate(String username, String pass) {
		try {
			ResultSet rs = Queries.auth(sqlConnection, username, pass).executeQuery();
			if(rs.next()) {
				Player p=new Player(rs.getInt(1));
				rs.close();
				return p;
			} else {
				return null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.severe(AccountDAO.class, "Database error: " + e.getMessage());
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.severe(AccountDAO.class, e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean createAccount(String ip, String username, String password, int flags) {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.CreateUserAccount(sqlConnection, ip, username, password, flags);
			ps.execute();
			ps.close();
		}catch (SQLException e) {
			e.printStackTrace();
			b = false;
		}
		catch (Exception e) {
			e.printStackTrace();
			b = false;
		}
		return b;
	}
	
	public static Player getPlayerByIp(String ip){
		
		try {
			ResultSet rs = Queries.getAccountByIp(sqlConnection, ip).executeQuery();
			if(rs.next()) {
				Player p=new Player(rs.getInt(1));
				rs.close();
				return p;
			} else {
				return null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
}
