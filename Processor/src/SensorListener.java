import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

public class SensorListener implements Runnable {

	private String serverIP;
	private int port;
	private int id;
	private ArrayList<SensorData> buffer;
	private Vector2D lastPos;
	
	public SensorListener(int id,String ip, int p) {
		this.id = id;
		this.serverIP = ip;
		this.port = p;
		buffer = new ArrayList<SensorData>();
		lastPos = new Vector2D();
	}
	
	private void setLastPosition(Vector2D intersection) {
		synchronized (lastPos) {
			lastPos.setX(intersection.getX());
			lastPos.setY(intersection.getY());
		}	
	}
	
	public Vector2D getLastPosition() {
		synchronized (lastPos) {
			return new Vector2D(lastPos);
		}	
	}
	
	private void AddSensorData(SensorData sd)
	{
		synchronized (buffer) {
			buffer.add(new SensorData(sd) );
		}
	}
	
	public void Dequeue()
	{
		synchronized (buffer) {
			if(!buffer.isEmpty()) {
				buffer.remove(0);
			}
		}
	} 
	
	public SensorData TopSensorData()
	{
		SensorData sd = null;
		synchronized (buffer) {
			if(!buffer.isEmpty()) {
				sd = new SensorData(buffer.get(0));
			}
		}
		return sd;
	}
	
	
	@Override
	public void run() {
		try (Socket socket = new Socket(serverIP, port)) {
			
			while(true) 
			{
				InputStream input = socket.getInputStream();
            	BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            	String msg = reader.readLine();
            	
            	try {
            	     JSONObject jsonObject = new JSONObject(msg);
            	     SensorData sd = new SensorData();
            	     
            	     sd.setTs( jsonObject.getLong("ts") );
            	     sd.setAngle( jsonObject.getDouble("theta") );
            	     Vector2D sensorPos =  new Vector2D( jsonObject.getJSONObject("pos").getDouble("x"), jsonObject.getJSONObject("pos").getDouble("y"));
            	     sd.setPos( sensorPos );
            	     setLastPosition(sensorPos);
            	     AddSensorData(sd);
            	     TimeUnit.MILLISECONDS.sleep(1);
            	     
            	}catch (Exception err){
            		System.out.println(err.getMessage());
            	}
			}
 
        } catch (UnknownHostException ex) {
 
            System.out.println("Server not found: " + ex.getMessage());
 
        } catch (IOException ex) {
 
            System.out.println("I/O error: " + ex.getMessage());
        }
		
	}

}
