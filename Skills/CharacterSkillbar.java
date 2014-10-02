package Skills;

import java.util.HashMap;
import java.util.Map;

public class CharacterSkillbar {

	private Map<Integer, Integer> skillBarValue;
	
	public CharacterSkillbar(){
		skillBarInit();
	}
	
	private void skillBarInit(){
		skillBarValue=new HashMap<Integer, Integer>(21);
	}
	
	public Map<Integer, Integer> getSkillBar(){
		return skillBarValue;
	}
	
}
