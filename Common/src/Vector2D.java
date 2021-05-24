public class Vector2D {
	private double x;
	private double y;
	
	public Vector2D()
	{
		x = 0;
		y = 0;
	}
	
	public Vector2D(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Vector2D( Vector2D p )
	{
		this.x = p.x;
		this.y = p.y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public double LengthSq()
	{
		return ( x*x ) + (y*y);
	}
	
	public double Length()
	{
		return Math.sqrt(( x*x ) + (y*y));
	}
	
	public double DistanceSquared( Vector2D v2)
	{
		return ((this.x - v2.x)*(this.x - v2.x)) + ((this.y - v2.y)*(this.y - v2.y));
	}
	
	public Vector2D Normalized()
	{
		Vector2D v = new Vector2D(this);
		double l = this.Length();
		v.x /= l;
		v.y /= l;
		return v;
	}
	
	public Vector2D Add( Vector2D v2)
	{
		Vector2D v3 = new Vector2D(this.x + v2.x, this.y + v2.y);
		return v3;
	}
	
	public Vector2D Sub(Vector2D v2)
	{
		Vector2D v3 = new Vector2D(this.x - v2.x, this.y - v2.y);
		return v3;
	}
	
	public Vector2D Mul(double val)
	{
		Vector2D v2 = new Vector2D(this.x * val, this.y * val);
		return v2;
	}
	
	public Vector2D Div(double val)
	{
		Vector2D v2 = new Vector2D(this.x / val, this.y / val);
		return v2;
	}
	
	public String toString()
	{
		return "(" + this.x + ", "+ this.y + " )";
	}
}
