package item.upgrades;

import java.util.LinkedList;
import java.util.Map;

import item.ItemCache;
import item.ItemFrame;
import item.ItemInInv;
import item.inventory.Inventory;
import item.inventory.InventoryException;
import item.inventory.InventoryPackets;
import Database.UpgradeDAO;
import GameServer.ServerPackets.ServerMessage;
import Player.Character;
import ServerCore.ServerFacade;
import Skills.SkillMaster;
import Tools.BitTools;

public class UpgradeMaster {
	
	private static enum Result{SUCCESS,FAIL,BREAK};

	public static byte[] upgradeItem(Character ch, int olditemIndex, int upgraderIndex) throws InventoryException{
		
		Inventory inv=ch.getInventory();
		inv.updateInv();
		
		LinkedList<Integer> seq=inv.getSeqSaved();
		Map<Integer,ItemInInv> items=inv.getInvSaved();
		
		int olditemHash=seq.get(olditemIndex);
		int upgraderHash=seq.get(upgraderIndex);
		if(olditemHash==-1 || upgraderHash==-1)
			throw new InventoryException("Cannot upgrade item [items do not exist]");
		
		ItemInInv olditem=items.get(olditemHash);
		ItemInInv upgrader=items.get(upgraderHash);
		if(olditem==null || upgrader==null)
			throw new InventoryException("Cannot upgrade item [items do not exist]");
		
		Upgrade upgrade=UpgradeDAO.getInstance().getUpgrade(olditem.getItem().getId(), upgrader.getItem().getId());
		
		if(!SkillMaster.canUpgrade(ch, upgrade.getUpgradeskill())){
			throw new InventoryException("Cannot upgrade item [your upgrade lvl is too low]");
		}
		
		int newItem=0;
		int newitemIndex=0;
		int newitemHash=-1;
		Result res=getResult(upgrade);
		
		if(res==Result.SUCCESS){
			//change item
			newItem=upgrade.getNewit();
			ItemFrame itams=((ItemFrame)(ItemCache.getInstance().getItem(newItem)));
			if(itams==null)
				throw new InventoryException("Cannot upgrade item [resulting item do not exist]");
			olditem.setItem(itams);
			//position of new item
			newitemIndex=olditemIndex;
			newitemHash=olditemHash;
		}else{
			if(res==Result.BREAK){
				//remove item
				inv.removeItem(olditemIndex,1);
				if(inv.getSeq().get(olditemIndex)==-1)
					ServerFacade.getInstance().addWriteByChannel(ch.GetChannel(), InventoryPackets.getInventoryDeletePacket(ch, olditemIndex, 1));
				new ServerMessage().execute("Sorry, item broke!", ServerFacade.getInstance().getConnectionByChannel(ch.GetChannel()));
			}
		}
		
		//remove upgrader
		inv.removeItem(upgraderIndex,1);
		if(inv.getSeq().get(upgraderIndex)==-1)
			ServerFacade.getInstance().addWriteByChannel(ch.GetChannel(), InventoryPackets.getInventoryDeletePacket(ch, upgraderIndex, 1));
		
		inv.saveInv(ch);
		
		return getUpgradePacket(ch, olditem, newitemIndex, newitemHash);
		
	}
	
	private static Result getResult(Upgrade upgrade){
		int stage=(int)upgrade.getItstage();
		float failrate=upgrade.getFailrate();
		int breakoption=(int)upgrade.getBreakoption();
		
		//weird stage
		if(stage==800000 || stage==0){
			stage=1;
		}
		
		double successchance=1;
		if((int)(failrate)!=2 && failrate!=0)
			successchance=1-Math.pow(2, -(200/(30+stage))*failrate);
		System.out.println("FAIL:"+failrate);
		System.out.println("SUC:"+successchance);
		int breakrate=-1;
		
		//there are 3 different options, 1 = no breaks
		if(breakoption==0){
			breakrate=10;
		}else if(breakoption==2){
			breakrate=3;
		}
		
		//calculate Result
		if(successchance==1 || (successchance!=0 && (int)(Math.random()/successchance)==0)){
			return Result.SUCCESS;
		}
		if(breakrate!=-1 && (int)(Math.random()*breakrate)==0){
			return Result.BREAK;
		}
		return Result.FAIL;
		
	}
	
	private static byte[] getUpgradePacket(Character ch, ItemInInv newItem, int newitemIndex, int newitemHash){
		
		byte[] chid = BitTools.intToByteArray(ch.getCharID());
		byte[] newItemB=BitTools.intToByteArray(newItem.getItem().getId());
		byte[] newAmount=BitTools.intToByteArray(newItem.getAmount());
		byte x,y,suc;
		if(newitemHash!=-1){
			x=(byte)(newitemHash%100);
			y=(byte)(newitemHash/100);
			suc=1;
		}else{
			x=(byte)0xFF;
			y=(byte)0xFF;
			suc=0;
		}
		
		byte[] upgr=new byte[36];
		
		upgr[0] = (byte)0x24;
		upgr[4] = (byte)0x04;
		upgr[6] = (byte)0x32;
		upgr[8] = (byte)0x01;
		for(int i=0;i<4;i++) {
			upgr[12+i] = chid[i];
			upgr[28+i] = newItemB[i];
			upgr[32+i] = newAmount[i];
		}
		
		upgr[16] = suc;

		upgr[18] = (byte)0x00; 
		upgr[19] = (byte)newitemIndex;
		
		upgr[20] = (byte)0x44;
		upgr[21] = (byte)0x06;
		upgr[22] = (byte)0x06;
		upgr[23] = (byte)0x08;
		
		upgr[26] = y;
		upgr[27] = x;
		
		return upgr;
		
	}
	
}
