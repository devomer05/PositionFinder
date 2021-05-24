import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

public class LocationRetriever implements Runnable {

	private Vector2D targetPos;
	private int port;
	private String ip;
	private boolean dataReady;
	
	public LocationRetriever( int port, String ip )
	{
		this.ip = ip;
		this.port = port;
		this.targetPos = new Vector2D();
		this.dataReady = false;
	}
	
	private void SetTargetPos(Vector2D p)
	{
		synchronized (targetPos) {
			targetPos.setX(p.getX());
			targetPos.setY(p.getY());
			dataReady = true;
		}
	}
	
	public Vector2D GetTargetPos()
	{
		synchronized (targetPos) {
			if(dataReady)
			{
				dataReady = false;
				return new Vector2D(targetPos);
			}
			else
			{
				return null;
			}
		}
	}
	
	@Override
	public void run()
	{
		try {
			MulticastSocket dsock = new MulticastSocket( port );
			dsock.joinGroup(InetAddress.getByName( ip ) );
			byte[] byteBuffer = new byte[2048];
			DatagramPacket data = new DatagramPacket( byteBuffer , 2048 );
			System.out.println("Started to listen from port " + port );
			while(true)
			{
				dsock.receive( data );
				ByteArrayInputStream byteIn = new ByteArrayInputStream(byteBuffer);
			    DataInputStream dataIn = new DataInputStream(byteIn);
				double x = dataIn.readDouble();
			    double y = dataIn.readDouble();
			    Vector2D p = new Vector2D(x,y);
				//System.out.println("x: " + x + " y: " + y);
				SetTargetPos(p);
				dataIn.close();
				byteIn.close();
				
				TimeUnit.MILLISECONDS.sleep(1);
			}
		} catch( SocketException ex ) {
			System.out.println(ex.getMessage());
		} catch( Exception ex ) {
			System.out.println(ex.getMessage());
		}
	}
	
}