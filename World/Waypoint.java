package World;

public class Waypoint {
	private float x,y;
	
	public Waypoint(float tx, float ty){
		this.x = tx;
		this.y = ty;
	}
	public float getX(){
		return this.x;
	}
	public float getY(){
		return this.y;
	}
	public void setX(float x) {
		this.x = x;
	}
	public void setY(float y) {
		this.y = y;
	}
	public boolean equals(Waypoint wp){
		if(wp==null)
			return false;
		if(wp.getX()!=x || wp.getY()!=y)
			return false;
		return true;
	}

}
