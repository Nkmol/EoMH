package Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import Gamemaster.GameMaster;
import Gamemaster.GameMasterRank;
import logging.ServerLogger;

public class GamemasterDAO {

	private static final Connection sqlConnection = new SQLconnection().getConnection();
	private static ServerLogger log = ServerLogger.getInstance();
	
	public static void loadGamemasterRanks(){
		ResultSet rs;
		try {
			HashMap<Integer, GameMasterRank> ranks=new HashMap<Integer, GameMasterRank>();
			
			rs = Queries.getAllGamemasterRanks(sqlConnection).executeQuery();
			boolean gotResult=rs.next();
			
			while(gotResult){
				ranks.put(rs.getInt("rank"), new GameMasterRank(rs.getString("prename"),rs.getShort("gotGMname")==1,
						rs.getInt("commandpower"),rs.getInt("allocateGMrank"),rs.getShort("isPlayer")==1));
				
				gotResult=rs.next();
			}
			
			GameMaster.init(ranks);
			rs.close();
		} catch (SQLException e) {
			log.severe(CharacterDAO.class, "Database error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.severe(CharacterDAO.class, e.getMessage());
			e.printStackTrace();
		}
	}
	
}
