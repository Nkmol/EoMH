package item.inventory;

import item.EquipableItem;
import item.EquipableSetItem;
import item.ItemFrame;
import item.ItemInInv;
import Player.Character;

import java.util.HashMap;
import java.util.Map;

public class Equipments {
	private int hp, mana, stamina;
	private short atk, deff, minDmg, maxDmg, critDmg;
	private short []stats =  new short[]{0,0,0,0,0};
	private short []setStats =  new short[]{0,0,0,0,0};
	private float []typeDmg = new float[]{0,0,0,0};
	private float atkSucMul, defSucMul, critRateMul;
	private Map<Integer, ItemInInv> equipments = new HashMap<Integer, ItemInInv>(17);
	private Map<Integer, Integer> sets;
	private Map<Integer, Integer> fullSets;
	private Character owner;
	private float speed;
	//saved list
	private Map<Integer, ItemInInv> equipmentsSaved = new HashMap<Integer, ItemInInv>(17);
	
	public Equipments(Character owner) {
		this.owner = owner;
		this.setToZero();
	}
	// NOTE use only for testing purposes
	public Equipments(){
		this.owner = null;
		this.setToZero();
		
	}
	
	//save seq and inv
	public void saveEquip(){
			
		equipmentsSaved.clear();
		equipmentsSaved.putAll(equipments);
			
	}
		
	//update seq and inv
	public void updateEquip(){
			
		equipments.clear();
		equipments.putAll(equipmentsSaved);
			
	}
	
	//print the ids
	public void printEquip(){
		
		//equip
		for(int i=0;i<17;i++){
			if(equipments.containsKey(i)){
				System.out.print("["+equipments.get(i).getItem().getId()+"]");
			}
		}
		
	}
	
	//swap two items in equip (weapon1 and weapon2)
	public void swapEquips(int index1, int index2) throws InventoryException{
		
		if(!equipments.containsKey(index1) || !equipments.containsKey(index2)){
			throw new InventoryException("Cannot swap items in equip [missing item(s)]");
		}
		ItemInInv item1=equipments.get(index1);
		equipments.put(index1, equipments.get(index2));
		equipments.put(index2, item1);
		
	}
	
	//calculate all stats of the items
	public void calculateEquipStats(){
		
		setToZero();
		sets = new HashMap<Integer, Integer>();
		fullSets = new HashMap<Integer, Integer>();
		
		for(int i=0;i<18;i++){
			if(i!=8 && equipmentsSaved.containsKey(i)){
				
				ItemFrame item;
				//EQUIPABLE ITEM
				item=(ItemFrame)equipmentsSaved.get(i).getItem();
				hp+=((EquipableItem)item).getHp();
				mana+=((EquipableItem)item).getMana();
				stamina+=((EquipableItem)item).getStamina();
				minDmg+=((EquipableItem)item).getMinDmg();
				maxDmg+=((EquipableItem)item).getMaxDmg();
				atk+=((EquipableItem)item).getAtk();
				deff+=((EquipableItem)item).getDef();
				atkSucMul+=((EquipableItem)item).getAttSucc();
				defSucMul+=((EquipableItem)item).getDefSucc();
				critRateMul+=((EquipableItem)item).getCritSucc();
				short[] itstatbonuses=((EquipableItem)item).getStatBonuses();
				for(int j=0;j<5;j++)
					stats[j]+=itstatbonuses[j];
				if(i==15)
					setSpeed(getSpeed() + ((EquipableItem)item).getSpeed());
					
				//EQUIPABLE SET ITEM
				if(item instanceof EquipableSetItem){
					
					int setHash=((EquipableSetItem)item).getSetHash();
					if(sets.containsKey(setHash)){
						sets.put(setHash, sets.get(setHash)+1);
						
						//full set
						if(sets.get(setHash)==((EquipableSetItem)item).getSetPieces()){
							
							fullSets.put(setHash, 1);
							ItemFrame tmp;
							for(int k=0;k<18;k++){
								ItemInInv eS=equipmentsSaved.get(k);
								if(eS!=null){
									tmp=(ItemFrame)eS.getItem();
									if(tmp instanceof EquipableSetItem && tmp.getSetHash()==setHash){
										hp+=((EquipableSetItem)tmp).getSetBonusHp();
										mana+=((EquipableSetItem)tmp).getSetBonusMana();
										stamina+=((EquipableSetItem)tmp).getSetBonusStamina();
										atk+=((EquipableSetItem)tmp).getSetBonusAtk();
										deff+=((EquipableSetItem)tmp).getSetBonusDef();
										atkSucMul+=((EquipableSetItem)tmp).getSetBonusAttSucc();
										defSucMul+=((EquipableSetItem)tmp).getSetBonusDefSucc();
										critRateMul+=((EquipableSetItem)tmp).getSetBonusCritSucc();
										short[] itstatbonuses2=((EquipableSetItem)tmp).getStats();
										for(int j=0;j<5;j++)
											stats[j]+=itstatbonuses2[j];
									}
								}
							}
							
						}
					}else{
						sets.put(setHash, 1);
					}
					
				}
				
			}
		}
		
	}
	
	public Character getOwner() {
		return this.owner;
	}

	public Map<Integer, ItemInInv> getEquipments() {
		return equipments;
	}
	
	public Map<Integer, ItemInInv> getEquipmentsSaved() {
		return equipmentsSaved;
	}
	
	public void setEquipments(Map<Integer, ItemInInv> equipments) {
		this.equipments = equipments;
	}

	public void setEquipmentsSaved(Map<Integer, ItemInInv> equipmentsSaved) {
		this.equipmentsSaved = equipmentsSaved;
	}
	/*  Reset all stats to zero
	 * this should only be called from constructor and calculateEquipmentStats
	 */
	private void setToZero(){
		this.hp = 0;
		this.mana = 0;
		this.stamina = 0;
		this.atk = 0;
		this.minDmg=0;
		this.maxDmg=0;
		this.deff = 0;
		for (int i=0; i <5; i++) this.stats[i] = 0;
		for (int i=0; i <5; i++) this.setStats[i] = 0;
		for (int i=0; i <4; i++){ this.typeDmg[i] = 0;}
		this.atkSucMul = 0;
		this.defSucMul = 0;
		this.critRateMul = 0;
		this.critDmg = 0;
		this.setSpeed(0);
	}
	
	public void printEquipStats(){
		System.out.println("Items:");
		for(int i=0;i<17;i++)
			System.out.print("["+equipments.get(i)+"]");
		System.out.println();
		System.out.println("Total equipment bonus");
		System.out.println("HP: " + this.hp + " Mana: " + this.mana + " stamina: " + this.stamina);
		System.out.println("Attack: " + this.atk + " Defense: " + this.deff);
		System.out.println("Str: " + this.stats[0] + " int: " + this.stats[1] + " vit: " + this.stats[2]);
		System.out.println("Agi: " + this.stats[3] + " Dex: " + this.stats[4]);
	}

	public int getHp() {
		return hp;
	}

	public int getMana() {
		return mana;
	}

	public int getStamina() {
		return stamina;
	}

	public short getAtk() {
		return atk;
	}

	public short getDeff() {
		return deff;
	}

	public short[] getStats() {
		return stats;
	}

	public short[] getSetStats() {
		return setStats;
	}

	public float getAtkSucMul() {
		return atkSucMul;
	}

	public float getDefSucMul() {
		return defSucMul;
	}

	public float getCritRateMul() {
		return critRateMul;
	}
	public float [] getTypeDmg() {
		return typeDmg;
	}
	public short getMinDmg() {
		return minDmg;
	}
	public void setMinDmg(short minDmg) {
		this.minDmg = minDmg;
	}
	public short getMaxDmg() {
		return maxDmg;
	}
	public void setMaxDmg(short maxDmg) {
		this.maxDmg = maxDmg;
	}
	public Map<Integer, Integer> getFullSets(){
		return fullSets;
	}
	public short getCritDmg() {
		return critDmg;
	}
	public void setCritDmg(short critDmg) {
		this.critDmg = critDmg;
	}
	public float getSpeed() {
		return speed;
	}
	public void setSpeed(float speed) {
		this.speed = speed;
	}

}
