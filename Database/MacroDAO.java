package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class MacroDAO {
	private final Connection sqlConnection = new SQLconnection().getConnection(); 
	private static MacroDAO instance;
	
	private MacroDAO() {
		instance = this;
	}
	
	public static MacroDAO getInstance() {
		return (instance == null) ? new MacroDAO() : instance;
	}
	
	public Connection getSqlConnection(){
		return sqlConnection;
	}
	
	public String getMacroPassword(String name) {
		String s=null;
		try {
			ResultSet rs = Queries.getMacro(this.sqlConnection, name).executeQuery();
			if(rs.next()){
				s=rs.getString("password");
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
			return null;
			
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
		return s;
	}
	
	public boolean updateMacro(String name, String content) {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.changeMacro(sqlConnection,name,content);
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
	
	public boolean deleteMacro(String name) {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.deleteMacro(sqlConnection, name);
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
	
	public boolean changeMacroPassword(String name, String password) {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.changeMacroPassword(sqlConnection, name, password);
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
	
	public String getMacro(String name) {
		String s=null;
		try{
			ResultSet rs=Queries.getMacro(sqlConnection, name).executeQuery();
			if(rs.next()){
				s=rs.getString(3);
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
	
	public ResultSet fetchMacros() {
		ResultSet rs = null;
		try {
			rs = Queries.getAllMacros(this.sqlConnection).executeQuery();
		} catch (SQLException e){
			e.printStackTrace();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		return rs;
	}
	
	public boolean deleteAllMacros() {
		boolean b = true;
		try{
			PreparedStatement ps=Queries.deleteAllMacros(sqlConnection);
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
	
	public boolean updateAllMacros(LinkedList<LinkedList<String>> lines, boolean canOverwrite){
		boolean b=false;
		LinkedList<String> word;
		String name,password,content;
		while(!lines.isEmpty()){
			word=lines.removeFirst();
			name=word.removeFirst();
			password=word.removeFirst();
			content=word.removeFirst();
			if(getMacroPassword(name)!=null){
				if(canOverwrite){
					try{
						PreparedStatement ps=Queries.changeMacro(sqlConnection, name, content);
						ps.execute();
						ps.close();
						ps=Queries.changeMacroPassword(sqlConnection, name, password);
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
					PreparedStatement ps=Queries.addMacro(sqlConnection, name, password, content);
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
