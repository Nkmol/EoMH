package Mob;

public class Mobpuzzle {

	private final String question;
	private final String answer;
	private final int type;
	private final float coinrate;
	private final float exprate;
	private final float droprate;
	private final int bonusdrop;
	
	public Mobpuzzle(String question, String answer, int type, float coinrate, float exprate, float droprate, int bonusdrop){
		this.question=question;
		this.answer=answer;
		this.type=type;
		this.coinrate=coinrate;
		this.exprate=exprate;
		this.droprate=droprate;
		this.bonusdrop=bonusdrop;
	}

	public String getQuestion() {
		return question;
	}

	public String getAnswer() {
		return answer;
	}

	public int getType() {
		return type;
	}

	public float getCoinrate() {
		return coinrate;
	}

	public float getExprate() {
		return exprate;
	}

	public float getDroprate() {
		return droprate;
	}

	public int getBonusdrop() {
		return bonusdrop;
	}
	
}
