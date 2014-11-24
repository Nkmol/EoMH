package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import GameServer.ServerEvent;
import GameServer.ServerMaster;

public class ServerControlDAO {
	private final Connection sqlConnection = new SQLconnection().getConnection(); 
	private static ServerControlDAO instance;
	
	private ServerControlDAO() {
		instance = this;
	}
	
	public static ServerControlDAO getInstance() {
		return (instance == null) ? new ServerControlDAO() : instance;
	}
	
	public Connection getSqlConnection(){
		return sqlConnection;
	}
	
	public boolean deleteEvent(String eventName) {
		boolean b = true;
		try{
			ResultSet rs=fetchEvent(eventName);
			if(!rs.next())
				return false;
			PreparedStatement ps=Queries.deleteEvent(sqlConnection, eventName);
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
	
	public ResultSet fetchEvents() {
		ResultSet rs = null;
		try {
			rs = Queries.getAllEvents(this.sqlConnection).executeQuery();
		} catch (SQLException e){
			e.printStackTrace();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		return rs;
	}
	
	private ResultSet fetchEvent(String eventName) {
		ResultSet rs = null;
		try {
			rs = Queries.getEvent(this.sqlConnection, eventName).executeQuery();
		} catch (SQLException e){
			e.printStackTrace();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		return rs;
	}
	
	public ServerEvent getCurrentEvent(){
		ResultSet rs=fetchEvent(getCurrentEventName());
		ServerEvent se=null;
		try{
			if(rs.next()){
				se=new ServerEvent(rs.getString("eventName"), rs.getFloat("exp"), rs.getFloat("dropr"), rs.getFloat("coin"), rs.getFloat("fame"),
						rs.getFloat("generalStarrate"), rs.getInt("starrate"), rs.getInt("superstarrate"), rs.getInt("multihitmobrate"), rs.getFloat("mobhp"), rs.getString("description"));
			}
			rs.close();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return se;
	}
	
	public boolean addEvent(String eventName, float exp, float drop, float coin,
			float fame, float generalStarrate, int starrate, int superstarrate, int multihitmobrate, float mobhp, String desc){
		boolean b=false;
		try{
			PreparedStatement ps=Queries.addEvent(sqlConnection, eventName, exp, drop, coin, fame, generalStarrate,
					starrate, superstarrate, multihitmobrate, mobhp,desc);
			ps.execute();
			ps.close();
			b=true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return b;
	}
	
	public boolean updateEvent(String eventName, float exp, float drop, float coin,
			float fame, float generalStarrate, int starrate, int superstarrate, int multihitmobrate, float mobhp, String desc){
		boolean b=false;
		try{
			PreparedStatement ps=Queries.updateEvent(sqlConnection, eventName, exp, drop, coin, fame, generalStarrate,
					starrate, superstarrate, multihitmobrate, mobhp,desc);
			ps.execute();
			ps.close();
			b=true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return b;
	}
	
	public boolean changeServerControlEvent(String eventName){
		boolean b=false;
		try{
			ResultSet rs=fetchEvent(eventName);
			if(!rs.next())
				return false;
			rs.close();
			PreparedStatement ps=Queries.changeServerControlEvent(sqlConnection, ServerMaster.getServerName(), eventName);
			ps.execute();
			ps.close();
			b=true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return b;
	}
	
	private String getCurrentEventName() {
		String s=null;
		try{
			ResultSet rs=Queries.getCurrentEvent(sqlConnection, ServerMaster.getServerName()).executeQuery();
			if(rs.next()){
				s=rs.getString("actualEvent");
			}
			rs.close();
			
		}catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return s;
	}
	
}
