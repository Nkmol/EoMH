package Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


import Skills.CastableSkill;
import Skills.SkillFrame;

public class SkillDAO {
	private final Connection sqlConnection = new SQLconnection().getConnection(); 
	private static SkillDAO instance;
	
	private SkillDAO() {
		instance = this;
	}
	
	public static SkillDAO getInstance() {
		return (instance == null) ? new SkillDAO() : instance;
	}
	
	//get all skills from db
	public Map<Integer, SkillFrame> getAllSkills(){
		
		ResultSet rs = null;
		Map<Integer, SkillFrame> skillMap=new HashMap<Integer, SkillFrame>();
		try {
			rs = Queries.getAllSkills(this.sqlConnection).executeQuery();
			while(rs.next()){
				 SkillFrame skill=processSkillTable(rs);
				 skillMap.put(skill.getId(), skill);
			}
			rs.close();
			return skillMap;
		} catch (SQLException e){
			e.printStackTrace();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static SkillFrame processSkillTable(ResultSet rs) {
		
		SkillFrame skill=null;
		try{
			
			if(rs.getInt("isSpecialCast")==1 || rs.getInt("specificType")==5 || rs.getInt("isCastable")==1 || rs.getInt("skillgroup")==255 || (rs.getInt("skillgroup")>149 && rs.getInt("skillgroup")<158)){
				skill = new CastableSkill(rs.getInt("skillid"));
				((CastableSkill)skill).setDmg(rs.getInt("dmg"));
				((CastableSkill)skill).setSpeed(rs.getFloat("speed"));
				((CastableSkill)skill).setHealCost(rs.getInt("healCost"));
				((CastableSkill)skill).setManaCost(rs.getInt("manaCost"));
				((CastableSkill)skill).setStaminaCost(rs.getInt("staminaCost"));
				((CastableSkill)skill).setTargets(rs.getInt("targets"));
				((CastableSkill)skill).setNeedsWepToCast(rs.getInt("needsWepToCast"));
				((CastableSkill)skill).setUltiSetId(rs.getInt("ultiSetId"));
				((CastableSkill)skill).setSpecial(rs.getInt("isSpecialCast") == 1);
			}else{
				skill = new SkillFrame(rs.getInt("skillid"));
			}
			
			if(rs.getShort("effId1") > 0 || rs.getShort("effId2") > 0 || rs.getShort("effId3") > 0) {
				skill.setEffectsId(rs.getShort("effId1"), rs.getShort("effId2"), rs.getShort("effId3"));
				skill.setEffectsDuration(rs.getShort("effDuration1"), rs.getShort("effDuration2"), rs.getShort("effDuration3"));
				skill.setEffectsValue(rs.getShort("effValue1"), rs.getShort("effValue2"), rs.getShort("effValue3"));
			}
			
			skill.setTypeGeneral(rs.getInt("generalType"));
			skill.setTypeSpecific(rs.getInt("specificType"));
			skill.setStage(rs.getInt("stage"));
			skill.setChclass(rs.getInt("chclass"));
			skill.setFaction(rs.getInt("faction"));
			skill.setReqSkill1(rs.getInt("reqSkill1"));
			skill.setReqSkill2(rs.getInt("reqSkill2"));
			skill.setReqSkill3(rs.getInt("reqSkill3"));
			skill.setSkillpoints(rs.getInt("skillpoints"));
			skill.setLvl(rs.getInt("lvl"));
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		}
		return skill;
		
	}

}
