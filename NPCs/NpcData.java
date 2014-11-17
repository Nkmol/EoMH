package NPCs;

public class NpcData {

	private int id;
	private int module;
	private int items[];
	
	public NpcData(int id, int module, int[] items){
		this.id=id;
		this.module=module;
		this.items=items;
	}
	
	public int getId(){
		return id;
	}
	
	public int getModule(){
		return module;
	}
	
	public int getItem(int index){
		return items[index];
	}
	
}
