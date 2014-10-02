package Parties;

import Player.Character;

public class PartyMaster {

	private final static float[] expFactorByMemberAmount={1,1,0.65f,0.5f,0.4f,0.35f,0.32f,0.3f,0.28f};
	private final static int ptLvlCap=21;
	private final static float[] expFactorByPtLvl=
	{1,1,1.01f,1.02f,1.03f,1.04f,1.05f,1.07f,1.09f,1.11f,1.13f,1.15f,1.18f,1.21f,1.24f,1.27f,1.3f,1.34f,1.38f,1.42f,1.46f,1.5f};
	private final static int[] ptLvlExp=
	{0,10,20,30,40,50,70,90,110,130,150,180,210,240,270,300,350,400,450,500,600,700};
	
	public static float getExpFactorByParty(Party pt, Character ch){
		float expFactor=expFactorByMemberAmount[pt.getMemberAmount()];
		int lvlDif=pt.getHighestLvl()-ch.getLevel();
		if(lvlDif>20)
			expFactor=0;
		else if(lvlDif>15)
			expFactor*=0.2;
		else if(lvlDif>10)
			expFactor*=0.5;
		return expFactor;
	}
	
	public static int getLvlCap(){
		return ptLvlCap;
	}
	
	public static float getExpFactorByPtLvl(Party pt){
		int lvl=pt.getPtlvl();
		if(lvl>ptLvlCap)
			lvl=ptLvlCap;
		return expFactorByPtLvl[lvl];
	}
	
	public static int getPtLvlExp(int lvl){
		if(lvl>ptLvlCap)
			lvl=ptLvlCap;
		return ptLvlExp[lvl];
	}
	
}
