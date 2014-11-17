package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class FilterDAO {
	private final Connection sqlConnection = new SQLconnection().getConnection(); 
	private static FilterDAO instance;
	
	private FilterDAO() {
		instance = this;
	}
	
	public static FilterDAO getInstance() {
		return (instance == null) ? new FilterDAO() : instance;
	}
	
	public Connection getSqlConnection(){
		return sqlConnection;
	}
	
	public boolean deleteFilter(String category, String command) {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.deleteFilter(sqlConnection, category, command);
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
	
	public LinkedList<Object> getFilter(String category, String command) {
		LinkedList<Object> filter=null;
		try{
			ResultSet rs=Queries.getFilter(sqlConnection, category, command).executeQuery();
			if(rs.next()){
				filter=new LinkedList<Object>();
				filter.add(rs.getString("commandName"));
				filter.add(rs.getInt("minValue"));
				filter.add(rs.getInt("maxValue"));
				filter.add(rs.getInt("standardValue"));
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
		return filter;
	}
	
	public ResultSet fetchFilters() {
		ResultSet rs = null;
		try {
			rs = Queries.getAllFilters(this.sqlConnection).executeQuery();
		} catch (SQLException e){
			e.printStackTrace();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		return rs;
	}
	
	public boolean deleteAllFilters() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.deleteAllFilters(sqlConnection);
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
	
	public boolean updateAllFilters(LinkedList<LinkedList<Object>> lines, boolean canOverwrite){
		boolean b=false;
		LinkedList<Object> word;
		String category,command,sqlName;
		int minValue,maxValue,standardValue;
		while(!lines.isEmpty()){
			word=lines.removeFirst();
			category=(String)word.removeFirst();
			command=(String)word.removeFirst();
			sqlName=(String)word.removeFirst();
			minValue=(Integer)word.removeFirst();
			maxValue=(Integer)word.removeFirst();
			standardValue=(Integer)word.removeFirst();
			if(getFilter(category,command)!=null){
				if(canOverwrite){
					try{
						PreparedStatement ps=Queries.changeFilter(sqlConnection, category, command, sqlName, minValue, maxValue, standardValue);
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
					PreparedStatement ps=Queries.addFilter(sqlConnection, category, command, sqlName, minValue, maxValue, standardValue);
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
