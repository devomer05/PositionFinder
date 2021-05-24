import java.util.concurrent.TimeUnit;

public class PositionCalculater implements Runnable {

	private SensorListener sensor1;
	private SensorListener sensor2;
	private Vector2D calculatedPos;
	
	public  PositionCalculater(SensorListener s1, SensorListener s2) {
		this.sensor1 = s1;
		this.sensor2 = s2;
		calculatedPos = new Vector2D(0,0);
	}
	
	@Override
	public void run() {
		while(true)
		{
			SensorData sd1 = sensor1.TopSensorData();
			SensorData sd2 = sensor2.TopSensorData();
			if( sd1 != null && sd2 != null) {
				long diff = sd2.getTs() - sd1.getTs() ;
				if( diff > 10 )// first sensor goes ahead, removing first elements until sync
				{
					sensor1.Dequeue();
					//System.out.println("First sensor goes ahead");
				}
				else if( diff < -10 )// second sensor goes ahead, removing first elements until sync
				{
					sensor2.Dequeue();
					//System.out.println("Second sensor goes ahead");
				}
				else // we can assume sensors are sync
				{
					double a0 = (90 - sd1.getAngle()), a1 = (90 - sd2.getAngle());
					
					try {
						Vector2D intersection = PositionCalculater.findIntersection(sd1.getPos(),a0,sd2.getPos(),a1);
						setCalculatedPosition(intersection);
						//System.out.println("Pos: ( " + intersection.getX() + ", " + intersection.getY() + " )" );
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
					sensor1.Dequeue();
					sensor2.Dequeue();
				}
			}
			else // If we are out of data, we can assume location with old data
			{
				
			}
			try {
				TimeUnit.MILLISECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void setCalculatedPosition(Vector2D intersection) {
		synchronized (calculatedPos) {
			calculatedPos.setX(intersection.getX());
			calculatedPos.setY(intersection.getY());
		}	
	}
	
	public Vector2D getCalculatedPosition() {
		synchronized (calculatedPos) {
			return new Vector2D(calculatedPos);
		}	
	}
	
	private static Vector2D findIntersection(Vector2D v1, double a0, Vector2D v2,  double a1) throws Exception {
		double x0 = v1.getX(),  y0 = v1.getY();
		double x1 = v2.getX(), y1 = v2.getY();
		if ((((a0-a1) % 180) + 180) % 180 == 0) throw new Exception("Lines are parallel");
	    
		double toRad = Math.PI / 180;
		
		Vector2D p = null;
		if (((a0 % 180) + 180) % 180 == 90) {
	        // vertical line at x = x0
			p = new Vector2D(x0, Math.tan(a1*toRad) * (x0-x1) + y1);
			return p;
	    }
	    else if (((a1 % 180) + 180) % 180 == 90) {
	        // vertical line at x = x0
	    	p = new Vector2D(x1, Math.tan(a0*toRad) * (x1-x0) + y0);
	    	return p;
	    }
	    double m0 = Math.tan(a0*toRad); // Line 0: y = m0 (x - x0) + y0
	    double m1 = Math.tan(a1*toRad); // Line 1: y = m1 (x - x1) + y1
	    double x = ((m0 * x0 - m1 * x1) - (y0 - y1)) / (m0 - m1);
	    p = new Vector2D(x, m0 * (x - x0) + y0);
		return p;
	}
}
