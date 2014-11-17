package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class ValueDescriptionDAO {
	private final Connection sqlConnection = new SQLconnection().getConnection(); 
	private static ValueDescriptionDAO instance;
	
	private ValueDescriptionDAO() {
		instance = this;
	}
	
	public static ValueDescriptionDAO getInstance() {
		return (instance == null) ? new ValueDescriptionDAO() : instance;
	}
	
	public Connection getSqlConnection(){
		return sqlConnection;
	}
	
	public boolean deleteDescription(String category, int descValue) {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.deleteDescription(sqlConnection, category, descValue);
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
	
	public String getDescription(String category, int descValue) {
		String description=null;
		try{
			ResultSet rs=Queries.getDescription(sqlConnection, category, descValue).executeQuery();
			if(rs.next()){
				description=rs.getString("description");
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
		return description;
	}
	
	public ResultSet fetchDescriptions() {
		ResultSet rs = null;
		try {
			rs = Queries.getAllDescriptions(this.sqlConnection).executeQuery();
		} catch (SQLException e){
			e.printStackTrace();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		return rs;
	}
	
	public boolean deleteAllDescriptions() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.deleteAllDescriptions(sqlConnection);
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
	
	public boolean updateAllDescriptions(LinkedList<LinkedList<Object>> lines, boolean canOverwrite){
		boolean b=false;
		LinkedList<Object> word;
		String category,description;
		int descValue;
		while(!lines.isEmpty()){
			word=lines.removeFirst();
			category=(String)word.removeFirst();
			description=(String)word.removeFirst();
			descValue=(Integer)word.removeFirst();
			if(getDescription(category,descValue)!=null){
				if(canOverwrite){
					try{
						PreparedStatement ps=Queries.changeDescription(sqlConnection, category, descValue, description);
						ps.execute();
						ps.close();
						b=true;
					}catch(Exception e){
						e.printStackTrace();
						return false;
					}
				}
			}else{
				try{
					PreparedStatement ps=Queries.addDescription(sqlConnection, category, descValue, description);
					ps.execute();
					ps.close();
					b=true;
				}catch(Exception e){
					e.printStackTrace();
					return false;
				}
			}
		}
		return b;
	}
	
}
