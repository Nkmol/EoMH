package item.inventory;

public class InventoryException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 172361190605745270L;

	public InventoryException(){}
	
	public InventoryException(String message){
		
		super(message);
		
	}
}
