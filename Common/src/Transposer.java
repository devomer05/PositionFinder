import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Transposer implements Runnable {

	private volatile Vector2D currentPosition;
	private ArrayList<Vector2D> patrolPoints;
	private double speed;
	private int updatePeriod;
	
	public Transposer()
	{
		patrolPoints = new ArrayList<Vector2D>();
		currentPosition = new Vector2D(0,0);
	}
	
	@Override
	public void run() {
		
		if( patrolPoints.size() == 0 ) {
			return;
		}
		long lastUpdate = System.currentTimeMillis();
		Vector2D nextPatrolPoint = null;
		Vector2D vel = null;
		int pointCounter = 0;
		//System.out.println("Starting patrol");
		while(true)
		{
			long now = System.currentTimeMillis();
			long diff = now - lastUpdate;
			if( diff >  updatePeriod )
			{
				// update;
				if(nextPatrolPoint == null)
				{
					nextPatrolPoint = patrolPoints.get(pointCounter);
					pointCounter = (pointCounter + 1) % patrolPoints.size();
					Vector2D diffVec = nextPatrolPoint.Sub( getCurrentPosition() );
					vel = diffVec.Normalized().Mul(speed);
					//System.out.println("Calculated next point: " + nextPoint);
					//System.out.println("Calculated vel: " + vel);
				}
				Vector2D increment = vel.Mul((double)diff / 1000);
				Vector2D nextPos = getCurrentPosition().Add( increment );
				if( getCurrentPosition().DistanceSquared(nextPatrolPoint) > nextPos.DistanceSquared(nextPatrolPoint) )
				{
					addCurrentPosition( increment );
				}
				else
				{
					//System.out.println("Arrived next patrol point");
					setCurrentPosition(nextPatrolPoint);
					nextPatrolPoint = null;
				}
				lastUpdate = now;
			}
			try {
				TimeUnit.MILLISECONDS.sleep(updatePeriod);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	private  void addCurrentPosition(Vector2D p) {
		synchronized( this ) {
			this.currentPosition.setX(this.currentPosition.getX() + p.getX());
			this.currentPosition.setY(this.currentPosition.getY() + p.getY());
		}
	}

	public Vector2D getCurrentPosition() {
		synchronized (this) {
			return currentPosition;
		}
	}

	public void setCurrentPosition(Vector2D p) {
		synchronized (this) {
			this.currentPosition.setX(p.getX());
			this.currentPosition.setY(p.getY());
		}
	}

	public ArrayList<Vector2D> getPatrolPoints() {
		return patrolPoints;
	}

	public void addPatrolPoint(Vector2D patrolPoint) {
		this.patrolPoints.add(patrolPoint);
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public int getUpdatePeriod() {
		return updatePeriod;
	}

	public void setUpdatePeriod(int updatePeriod) {
		this.updatePeriod = updatePeriod;
	}
}
