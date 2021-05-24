import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class LocationStreamer implements Runnable {
	private int PORT;
	private Vector2D position;
	private int interval;
	private String multicastIP;
	
	public LocationStreamer( String mip, int port, int t) {
		this.PORT = port;
		position = new Vector2D();
		this.interval = t;
		this.multicastIP = mip;
		
	}
	
	public void setPosition( Vector2D p )
	{
		synchronized (position) {
			this.position.setX(p.getX());
			this.position.setY(p.getY());
		}
	}
	
	@Override
	public void run()
	{
		try {
			InetAddress addr =  InetAddress.getByName(multicastIP);// getBroadcastAddrs().get(0);
			DatagramSocket dsock = new DatagramSocket();
			dsock.setBroadcast(true);
			
			while(true) 
			{
				ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		        DataOutputStream dataOut = new DataOutputStream(byteOut);
		        synchronized (position) {
			        dataOut.writeDouble(this.position.getX());
			        dataOut.writeDouble(this.position.getY());
		        }
		        dataOut.close();
		        byte[] send = byteOut.toByteArray();
				DatagramPacket data = new DatagramPacket( send, send.length, addr, PORT );
				System.out.println("Sending data." + this.position.getX() + " " + this.position.getY());
				dsock.send( data );
				dataOut.close();
				byteOut.close();
				TimeUnit.MILLISECONDS.sleep(interval);
			}
		} catch( SocketException ex ) {
			System.out.println(ex.getMessage());
		} catch( IOException ex ) {
			System.out.println(ex.getMessage());
		}catch( Exception ex ) {
			System.out.println(ex.getMessage());
		}
	}
	
	public static List<InetAddress> getBroadcastAddrs() throws SocketException {
	      Set<InetAddress> set = new LinkedHashSet<>();
	      Enumeration<NetworkInterface> nicList = NetworkInterface.
	              getNetworkInterfaces();
	      for( ; nicList.hasMoreElements(); ) {
	         NetworkInterface nic = nicList.nextElement();
	         if( nic.isUp() && !nic.isLoopback() )  {
	            for( InterfaceAddress ia : nic.getInterfaceAddresses() )
	               set.add( ia.getBroadcast() );
	         }
	      }
	      return Arrays.asList( set.toArray( new InetAddress[0] ) );

	   }
}
