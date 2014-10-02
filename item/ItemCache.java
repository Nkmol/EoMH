package item;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

import Database.ItemDAO;


public class ItemCache {
	private final ItemDAO itemDao = ItemDAO.getInstance();
	private volatile static ItemCache instance;
	private ConcurrentHashMap<Integer, WeakReference<ItemFrame>> itemsCache;
	
	private ItemCache()	{
		this.itemsCache = new ConcurrentHashMap<Integer, WeakReference<ItemFrame>>();
	}
	
	public static synchronized ItemCache getInstance(){
		if (instance == null){
			instance = new ItemCache();
		}
		return instance;
	}
	
	public Item getItem(int itemId) {
		if(!this.itemsCache.containsKey(Integer.valueOf(itemId))) {
			//System.out.println("Requested item id: " + itemId + " does not exist in cache");
			this.instantiateNewItem(itemId);
		}
		else {
			WeakReference<ItemFrame> it = this.itemsCache.get(itemId);
			if (it.get() == null) {
				this.itemsCache.remove(itemId);
				this.instantiateNewItem(itemId);
				//System.out.println("Requested item id: " + itemId + " exists, but has no active references to it");
			}
		}
		//System.out.println("Returning item id: " + itemId + ":" + this.itemsCache.containsKey(itemId));
		if(itemsCache.containsKey(Integer.valueOf(itemId)))
			return this.itemsCache.get(Integer.valueOf(itemId)).get();
		else
			return null;
	}
		
	private void instantiateNewItem(int itemId) {
		WeakReference<ItemFrame> it = new WeakReference<ItemFrame>(this.itemDao.getItem(itemId));
		if(it.get()!=null)
			this.itemsCache.put(itemId, it);
	}
	

}
