package Player;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Player {
	private List<Character> characters;
	private List<Integer> charactersIndex;
	private int accountID;
	private int flag;
	private SocketChannel sc;
	private Character activeCharacter;
	
	public Player(int id) {
		this.accountID = id;
		this.characters = new ArrayList<Character>();
		this.charactersIndex = new ArrayList<Integer>();
	}
	
	public List<Character> getCharacters() {
		return characters;
	}
	public void setCharacters(ArrayList<Character> characters) {
		this.characters.addAll(characters);
		for(int i=0;i<characters.size();i++){
			charactersIndex.add(i);
		}
	}
	public int getAccountID() {
		return accountID;
	}
	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}

	public SocketChannel getSc() {
		return sc;
	}
	public void setChannel(SocketChannel chan){
		this.sc = chan;
	}

	public Character getActiveCharacter() {
		return activeCharacter;
	}

	public void setActiveCharacter(Character activeCharacter) {
		this.activeCharacter = activeCharacter;
	}
	
	public void addCharacter(Character ch) {
		//keep the characters in the right order else deleting char fcks it up
		int[] indexes={-1,-1,-1,-1,-1};
		for(Iterator<Integer> i=charactersIndex.iterator();i.hasNext();){
			int j=i.next();
			indexes[j]=charactersIndex.indexOf(j);
		}
		int i=0;
		boolean foundindex=false;
		while(i<5 && foundindex==false){
			if(indexes[i]==-1){
				foundindex=true;
			}else{
				i++;
			}
		}
		charactersIndex.add(i);
		characters.add(ch);
	}
	
	public void removeCharacter(Character ch) {
		int i=characters.indexOf(ch);
		characters.remove(i);
		charactersIndex.remove(i);
	}
	
	public boolean hasActiveCharacter() {
		return (this.activeCharacter == null) ? false : true;
	}
	
	public Character getCharacterByIndex(int index){
		return characters.get(charactersIndex.indexOf(index));
	}
	
	public void refreshCharacterOrder(){
		for(int i=0;i<charactersIndex.size();i++){
			charactersIndex.set(i,i);
		}
	}
}
