package Database;

import item.upgrades.Upgrade;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpgradeDAO {
	private final Connection sqlConnection = new SQLconnection().getConnection(); 
	private static UpgradeDAO instance;
	
	private UpgradeDAO() {
		instance = this;
	}
	
	public static UpgradeDAO getInstance() {
		return (instance == null) ? new UpgradeDAO() : instance;
	}
	
	public Connection getSqlConnection(){
		return sqlConnection;
	}
	
	public Upgrade getUpgrade(int oldItem, int upgrader) {
		Upgrade upgrade=null;
		try{
			ResultSet rs=Queries.getUpgradeByItemAndUpgrader(sqlConnection, oldItem, upgrader).executeQuery();
			if(rs.next()){
				int upgradeId=rs.getInt("upgradeId");
				int newItem=rs.getInt("newItem");
				float itemStage=rs.getFloat("itemStage");
				float upgradeLvl=rs.getFloat("upgradeLvl");
				float failRate=rs.getFloat("failRate");
				float breakOption=rs.getFloat("breakOption");
				int upgradeSkill=rs.getInt("upgradeSkill");
				upgrade=new Upgrade(upgradeId, oldItem, upgrader, newItem, itemStage, upgradeLvl, failRate, breakOption, upgradeSkill);
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
		return upgrade;
	}
	
}
