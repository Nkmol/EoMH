package timer;

import java.util.TimerTask;

import Player.Character;
import World.WMap;
import World.Waypoint;

public class MoveSyncTimer extends TimerTask{

	private Waypoint target;
	private long timeUntilTarget;
	private Character owner;
	private long oldTime;
	
	public MoveSyncTimer(Character owner){
		this.owner=owner;
	}
	
	@Override
	public void run() {
		
		if(target!=null){
			refreshWaypoint();
		}
		
	}
	
	public synchronized void newTarget(Waypoint target){
		
		refreshWaypoint();
		this.target=target;
		calcTimeUntilTarget(owner.getLocation(), target, owner.getSpeed());
		
	}
	
	public void newSpeed(float speed){
		if(target!=null)
			refreshWaypoint();
		owner.setSpeed(speed);
		if(target!=null)
			calcTimeUntilTarget(owner.getLocation(), target, owner.getSpeed());
	}
	
	public void refreshWaypoint(){
		
		if(target!=null){
			long newTime=System.currentTimeMillis();
			long timeDif=newTime-oldTime;
			timeUntilTarget-=timeDif;
			if(timeUntilTarget>0){
				Waypoint oldWp=owner.getLocation();
				float newX,newY;
				float movedDistance=timeDif*owner.getSpeed()/1000f;
				float distanceLeft=WMap.distance(oldWp.getX(), oldWp.getY(), target.getX(), target.getY());
				float distanceFactor=movedDistance/distanceLeft;
				
				//System.out.println("movedDistance: "+movedDistance);
				//System.out.println("distanceLeft: "+distanceLeft);
				//System.out.println("distanceFactor: "+distanceFactor);
				//System.out.println("timeDif: "+timeDif);
				//System.out.println("timeUntilTarget: "+timeUntilTarget);
				
				newX=oldWp.getX()+(target.getX()-oldWp.getX())*distanceFactor;
				newY=oldWp.getY()+(target.getY()-oldWp.getY())*distanceFactor;
				
				owner.updateLocation(newX, newY);
				oldTime=newTime;
			}else{
				owner.updateLocation(target.getX(), target.getY());
				target=null;
			}
		}
		
	}
	
	public void calcTimeUntilTarget(Waypoint wp1, Waypoint wp2, float speed){
		
		float distance=WMap.distance(wp1.getX(), wp1.getY(), wp2.getX(), wp2.getY());
		timeUntilTarget=(long)(distance/speed*1000);
		oldTime=System.currentTimeMillis();
		
	}
	
	public void stopMovement(){
		refreshWaypoint();
		target=null;
	}
	
	public Waypoint getTarget(){
		return target;
	}
	
}
