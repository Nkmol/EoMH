package Skills;

import java.util.HashMap;

import Player.Dolls.Doll;

public class DollSkills {

	private HashMap<Integer, CastableSkill> castableSkills;
	private int basicSkill=0;
	private Doll owner;
	private int[] highestDmgSkills;
	
	public DollSkills(Doll owner){
		castableSkills=new HashMap<Integer, CastableSkill>();
		this.owner=owner;
		highestDmgSkills=new int[5];
		for(int i=0;i<5;i++){
			highestDmgSkills[i]=0;
		}
	}
	
	public DollSkills(Doll owner, HashMap<Integer, CastableSkillTimer> castableSkills, int basicSkill){
		this.castableSkills=new HashMap<Integer, CastableSkill>();
		for(int i=0;i<61;i++){
			if(castableSkills.containsKey(i)){
				SkillFrame skill=SkillMaster.getSkill(castableSkills.get(i).getId());
				if(skill instanceof CastableSkill)
					this.castableSkills.put(i, (CastableSkill)skill);
			}
		}
		this.basicSkill=basicSkill;
		this.owner=owner;
		highestDmgSkills=new int[5];
		for(int i=0;i<5;i++){
			highestDmgSkills[i]=0;
		}
		sortHighestDmgSkills();
	}
	
	public void sortHighestDmgSkills(){
		int[] highestDmg=new int[5];
		for(int i=0;i<5;i++){
			highestDmgSkills[i]=0;
			highestDmg[i]=0;
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
	
	public void learnSkill(int id){
		
		SkillFrame skill=SkillMaster.getSkill(id);
		if(skill==null)
			return;
		if(!(skill instanceof CastableSkill))
			return;
		if(castableSkills.containsValue(id))
			return;
		if(skill.getTypeSpecific()!=5)
			castableSkills.put(castableSkills.size(),(CastableSkill)skill);
		else
			basicSkill=id;
		sortHighestDmgSkills();
		
	}
	
	public int getBasicSkill(){
		return basicSkill;
	}
	
	public Doll getOwner(){
		return owner;
	}
	
}
