package Player.Dolls;

import java.util.LinkedList;

public class DollMaster {

	private static LinkedList<Doll> dolls=new LinkedList<Doll>();
	private static int latestUid=13371337;
	
	public static LinkedList<Doll> getDolls(){
		return dolls;
	}
	
	public static int getUid(){
		latestUid++;
		return latestUid;
	}
	
}
