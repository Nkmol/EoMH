package ExperimentalStuff;

import Mob.Mob;
import Player.Character;
import Tools.StringTools;

public class PuzzleMaster {

	public static boolean isPuzzleCorrect(Character ch, Mob puzzlemob, int puzzleType, String answer){
		switch(puzzleType){
			//question about hp of mob
			case 2:{
				if(!StringTools.isInteger(answer))
					return false;
				float answerInt=Integer.parseInt(answer);
				if(answerInt==0)
					return false;
				float div=puzzlemob.getHp()/answerInt;
				if(div>0.8 && div<1.2)
					return true;
				return false;
			}
			//difference of lvls
			case 3:{
				if(!StringTools.isInteger(answer))
					return false;
				int answerInt=Integer.parseInt(answer);
				int correctAnswer=Math.abs(ch.getLevel()-puzzlemob.getLevel());
				if(answerInt==correctAnswer)
					return true;
				return false;
			}
			//maths
			case 4:{
				if(!StringTools.isInteger(answer))
					return false;
				int answerInt=Integer.parseInt(answer);
				int correctAnswer=(int)Math.pow(puzzlemob.getLevel(),2);
				if(answerInt==correctAnswer)
					return true;
				return false;
			}
			//total statpoints
			case 5:{
				if(!StringTools.isInteger(answer))
					return false;
				int answerInt=Integer.parseInt(answer);
				short[] stats=ch.getStats();
				int statpoints=stats[0]+stats[1]+stats[2]+stats[4]+stats[5];
				if(answerInt==statpoints)
					return true;
				return false;
			}
			default:{
				break;
			}
		}
		return false;
	}
	
}
