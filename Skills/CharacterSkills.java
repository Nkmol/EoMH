package Skills;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import Database.CharacterDAO;
import Player.Character;

public class CharacterSkills {

	private HashMap<Integer, Integer> learnedSkills;
	private HashMap<Integer, Integer> loadedSkills;
	private HashMap<Integer, CastableSkillTimer> castableSkills;
	private int basicSkill=0;
	private Character owner;
	private int[] highestDmgSkills;
	
	public CharacterSkills(Character owner){
		loadedSkills=new HashMap<Integer, Integer>(61);
		castableSkills=new HashMap<Integer, CastableSkillTimer>(61);
		learnedSkills=new HashMap<Integer, Integer>(201);
		this.setOwner(owner);
		owner.setSkillPoints((owner.getLevel()-1)*2);
		highestDmgSkills=new int[5];
		for(int i=0;i<5;i++){
			highestDmgSkills[i]=0;
		}
	}
	
	public void sortHighestDmgSkills(){
		int[] highestDmg=new int[5];
		for(int i=0;i<5;i++){
			highestDmgSkills[i]=0;
			highestDmg[i]=-1;
		}
		for(int i=0;i<61;i++){
			if(castableSkills.containsKey(i)){
				int j=0;
				boolean found=false;
				while(j<5 && found==false){
					SkillFrame skill=SkillMaster.getSkill(castableSkills.get(i).getId());
					if(skill instanceof CastableSkill){
						int dmg=((CastableSkill)(skill)).getDmg();
						if(dmg>highestDmg[j]){
							found=true;
							int[] tmp1=highestDmgSkills.clone();
							int[] tmp2=highestDmg.clone();
							highestDmgSkills[j]=castableSkills.get(i).getId();
							highestDmg[j]=dmg;
							for(int k=j;k<4;k++){
								highestDmgSkills[k+1]=tmp1[k];
								highestDmg[k+1]=tmp2[k];
							}
						}
					}
					j++;
				}
			}
		}
	}
	
	public int[] getHighestSkills(){
		return highestDmgSkills;
	}
	
	public void learnSkill(int id, boolean updateSp) throws SkillException{
		
		//loaded skills are the skills in the spawn packet
		SkillFrame skill=SkillMaster.getSkill(id);
		int reqSkill=skill.getReqSkill1();
		if(reqSkill!=0 && (skill.getTypeGeneral()==27 || skill.getTypeSpecific()==0 || skill.getTypeSpecific()==7) && loadedSkills.containsValue(reqSkill)){
			for(int i=0;i<loadedSkills.size();i++){
				if (loadedSkills.get(i)==reqSkill){
					loadedSkills.put(i, id);
					if(skill.getTypeSpecific()!=0)
						castableSkills.put(i, new CastableSkillTimer(id));
				}
			}
		}else{
			loadedSkills.put(loadedSkills.size(), id);
			castableSkills.put(castableSkills.size(), new CastableSkillTimer(id));
		}
		
		if(loadedSkills.size()==61 || learnedSkills.size()==200){
			loadedSkills.remove(60);
			if(castableSkills.containsKey(60))
				castableSkills.remove(60);
			throw new SkillException("Cannot learn the skill [too many skills learned]");
		}
		
		learnedSkills.put(learnedSkills.size(), id);
		
		//add new basic skill
		if(skill.getTypeSpecific()==5)
			basicSkill=id;
		
		owner.setSkillPoints(owner.getSkillPoints()-skill.getSkillpoints());
		if(updateSp && !owner.isBot())
			CharacterDAO.saveCharacterSkillpoints(owner);
		
		System.out.println("Learned the skill with id "+id+" with number "+(loadedSkills.size()-1));
	}
	
	public CastableSkillTimer getCastableSkill(int number){
		if(castableSkills.containsKey(number))
			return null;
		return castableSkills.get(number);
	}
	
	public HashMap<Integer, Integer> getLearnedSkills(){
		return learnedSkills;
	}
	
	public HashMap<Integer, Integer> getLoadedSkills(){
		return loadedSkills;
	}
	
	public HashMap<Integer, CastableSkillTimer> getCastableSkills(){
		return castableSkills;
	}
	
	public int getBasicSkill(){
		if(basicSkill!=0)
			return basicSkill;
		else
			return SkillMaster.getWoodenSkillId(owner.getCharacterClass());
	}

	public Character getOwner() {
		return owner;
	}

	public void setOwner(Character owner) {
		this.owner = owner;
	}
	
	public int getUsedSkillpoints(){
		int sp=0;
		Set<Integer> ks=learnedSkills.keySet();
		for(Iterator<Integer> i=ks.iterator();i.hasNext();){
			sp+=SkillMaster.getSkill(learnedSkills.get(i.next())).getSkillpoints();
		}
		return sp;
	}
	
}
