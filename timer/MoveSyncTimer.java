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
			byte run=(byte)1;
			if(owner.isWalking())
				run=(byte)0;
			if(timeUntilTarget>0){
				int bonustime=500;
				if (timeUntilTarget<bonustime)
					bonustime=(int)timeUntilTarget;
				float newX,newY,targetX,targetY;
				float movedDistance=timeDif*owner.getSpeed()/1000f;
				float movedDistanceTarget=(timeDif+bonustime)*owner.getSpeed()/1000f;
				float distanceLeft=WMap.distance(owner.getlastknownX(), owner.getlastknownY(), target.getX(), target.getY());
				float distanceFactor=movedDistance/distanceLeft;
				float distanceFactorTarget=movedDistanceTarget/distanceLeft;
				
				//System.out.println("movedDistance: "+movedDistance);
				//System.out.println("distanceLeft: "+distanceLeft);
				//System.out.println("distanceFactor: "+distanceFactor);
				//System.out.println("timeDif: "+timeDif);
				//System.out.println("timeUntilTarget: "+timeUntilTarget);
				
				newX=owner.getlastknownX()+(target.getX()-owner.getlastknownX())*distanceFactor;
				newY=owner.getlastknownY()+(target.getY()-owner.getlastknownY())*distanceFactor;
				targetX=owner.getlastknownX()+(target.getX()-owner.getlastknownX())*distanceFactorTarget;
				targetY=owner.getlastknownY()+(target.getY()-owner.getlastknownY())*distanceFactorTarget;
				
				owner.updateLocation(newX, newY, targetX, targetY, run, true);
				oldTime=newTime;
				
				//packet
				//owner.sendMovementPackets(targetX, targetY, run);
			}else{
				owner.updateLocation(target.getX(), target.getY(), target.getX(), target.getY(), run, true);
				
				//packet
				//owner.sendMovementPackets(target.getX(), target.getY(), run);
				
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
