import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.json.*;

public class Sender implements Runnable {

	private int errorMargin; // it will add (+/-ERROR_MARGIN) degrees to angle
	
	private Sensor sensor ;
	private int port;
	public Sender( Sensor s, int port)
	{
		this.port = port;
		sensor = s;
	}
	
	@Override
	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			 
            System.out.println("Server is listening on port " + port);
 
            Socket socket = serverSocket.accept();
            
            System.out.println("New client connected");
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            boolean alreadyLogged = false;
            while (true) 
            {
            	
				try {
					Vector2D targetPos, selfPos;
					targetPos = sensor.getTargetPos();
					if(targetPos == null) {
						if(!alreadyLogged)
						{
							System.out.println("Waiting for new target pos...");
							alreadyLogged = true;
						}
						continue;
					}
					else
					{
						alreadyLogged = false;
					}
					
					selfPos = sensor.getSelfPos();
					
					double angle = ( (180/Math.PI) * Math.atan( (targetPos.getX() - selfPos.getX()) /  (targetPos.getY() - selfPos.getY()) ) );
					//////////////// 	 Simulate error		 /////////
					Random rand = new Random();
					int error = rand.nextInt( 2 * errorMargin + 1 ) - errorMargin;
					angle += error;
					//////////////////////////////////////////////////////////
					if( angle < 0 )
						angle += 360;
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					JSONObject msg = new JSONObject();
					Map<String, Double> posMap = new HashMap<>();
					posMap.put("x", selfPos.getX() );
					posMap.put("y", selfPos.getY());
					
					msg.put("ts",timestamp.getTime() );
					msg.put("pos", posMap);
					msg.put("theta", angle);
					System.out.println( "ID: " + sensor.getId() + "( Target: " + targetPos.getX() + "," + targetPos.getY() + " ) " + msg );
					writer.println( msg );
					
					TimeUnit.MILLISECONDS.sleep(1);
					
				} catch (Exception e) {
					//System.out.println(e.getMessage());
				}
                
            }
 
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
		
	}

	public void setErrorMargin(int e) {
		this.errorMargin = e;
		
	}

}
