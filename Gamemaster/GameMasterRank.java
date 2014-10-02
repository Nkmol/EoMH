package Gamemaster;

public class GameMasterRank {

	private final String prename;
	private final boolean gotGMname;
	private final int commandpower;
	private final int allocateGMrank;
	private final boolean isPlayer;
	
	public GameMasterRank(String prename, boolean gotGMname, int commandpower, int allocateGMrank, boolean isPlayer){
		
		this.prename=prename;
		this.gotGMname=gotGMname;
		this.commandpower=commandpower;
		this.allocateGMrank=allocateGMrank;
		this.isPlayer=isPlayer;
		
	}

	public String getPrename() {
		return prename;
	}

	public boolean gotGMname() {
		return gotGMname;
	}

	public int getCommandpower() {
		return commandpower;
	}

	public int getAllocateGMrank() {
		return allocateGMrank;
	}

	public boolean isPlayer() {
		return isPlayer;
	}
	
}
