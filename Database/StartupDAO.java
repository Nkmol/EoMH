package Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import logging.ServerLogger;

import World.Grid;
import World.WMap;

public class StartupDAO {

	
	public static void loadMaps(){
		int x,y,gridsize,areasize, pool, id;
		ServerLogger log = ServerLogger.getInstance();
		String name;
		Grid grid;
			try{
				ResultSet rs = Queries.getMaps(new SQLconnection(false).getConnection()).executeQuery();
				while( rs.next()){
					
					id = rs.getInt("id");
					x = rs.getInt("mapx");
					y = rs.getInt("mapy");
					gridsize = rs.getInt("gridSize");
					areasize = rs.getInt("areaSize");
					name = rs.getString("name");
					pool = rs.getInt("poolSize");
					grid = new Grid(id, gridsize, areasize, name, x, y, pool);
					WMap.getInstance().addGrid(grid);
					
					log.info(StartupDAO.class, "Map id:" + id + " name:" +name +" gridsize:" + gridsize + " areasize:" +areasize);
					log.info(StartupDAO.class, " x:" +x + " y:" +y + " poolsize:" +pool);
				}
				rs.close();
				
			}catch (SQLException e) {
				// e.printStackTrace();
				log.severe(StartupDAO.class, "Database error: " + e.getMessage());
			}
			catch (Exception e) {
				// e.printStackTrace();
				log.severe(StartupDAO.class, e.getMessage());
			}
		
	}
	/*
	public static void setZones(){
		int x,y, width, length, map;
		int zone = 0;
		String type;
		Grid g = null;
		ServerLogger log = ServerLogger.getInstance();
		try{
			ResultSet rs = Queries.getZones(new SQLconnection(false).getConnection()).executeQuery();
			while(rs.next()){
				map = rs.getInt("map");
				x = rs.getInt("startx");
				y = rs.getInt("starty");
				width = rs.getInt("width");
				length = rs.getInt("length");
				type = rs.getString("type");
				
				if (type.contentEquals("neutral")) zone = 0;
				if (type.contentEquals("safe")) zone = 1;
				if (type.contentEquals("faction")) zone = 2;
				if (type.contentEquals("restricted")) zone = 3;
				
				log.info(StartupDAO.class, "Setting zones in map " + map + " to type " + type);
				
				if (WMap.gridExist(map)){
					g = WMap.getGrid(map);
					for (int i=0; i< width; i++){
						for (int u=0; u< length; u++){
							if (g.areaExists((x + i), (y + u))){
								g.getArea(new int[]{(x + i),(y + u)}).setZone(zone);
							}
						}
					}
					
				}
			}
		} catch (SQLException e){
			log.severe(StartupDAO.class, "Database error:" + e.getMessage());
		} catch (Exception e){
			log.severe(StartupDAO.class, e.getMessage());
		}
		
	}
	*/
}
