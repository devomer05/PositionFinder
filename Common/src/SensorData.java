public class SensorData
{
	public SensorData() {}

	private int id;
	private long ts;
	private double angle;
	private Vector2D pos;
	
	public SensorData(SensorData sd) {
		this.ts = sd.ts;
		this.angle = sd.angle;
		this.pos = new Vector2D( sd.pos );
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public Vector2D getPos() {
		return pos;
	}

	public void setPos(Vector2D pos) {
		this.pos = pos;
	}
}