import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.Properties;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.TimeUnit;


public class Target {

	
	private Thread streamerThread = null;
	private Thread transposerThread;
	private LocationStreamer streamer = null;
	private Transposer transposer;
	private int interval;
	
	public boolean ReadConfig(String configFileStr)
	{
		File configFile = new File(configFileStr);
		try {
		    FileReader reader = new FileReader(configFile);
		    Properties props = new Properties();
		    props.load(reader);
		    
		    int PORT = Integer.parseInt(props.getProperty("multicast_port"));
		    System.out.println("Multicast port is: " + PORT);
		    
		    String multicastIP = props.getProperty("multicast_ip");
		    interval = Integer.parseInt(props.getProperty("interval"));
		    streamer = new LocationStreamer( multicastIP, PORT, interval );
		    
		    double speed = Integer.parseInt(props.getProperty("target_speed")); 
		    
		    double x = Double.parseDouble(props.getProperty("target_x"));
		    double y = Double.parseDouble(props.getProperty("target_y"));
		   
		    Vector2D selfPos = new Vector2D(x,y);
		    transposer = new Transposer();
		    transposer.setSpeed(speed);
			transposer.setCurrentPosition( selfPos );
			transposer.setUpdatePeriod(interval);
			File file = new File("patrolTarget.txt");
			Scanner sc = new Scanner(file);
		    while (sc.hasNextLine())
		    {
		    	Vector2D point = new Vector2D(sc.nextInt(), sc.nextInt());
		    	transposer.addPatrolPoint(point);
		    }
			transposer.addPatrolPoint(selfPos); // To go starting point
			sc.close();
		    reader.close();
		    return true;
		} catch (FileNotFoundException ex) {
		    return false;
		} catch (IOException ex) {
		    return false;
		}
	}
	
	public void startRunning(String configFile)
	{
		if(!ReadConfig(configFile))
		{
			System.out.print("Can not read config file");
		}
		
		streamerThread = new Thread( streamer );
		transposerThread = new Thread(transposer);
		transposerThread.start();
		streamerThread.start();
		
		while(true)
		{
			streamer.setPosition( transposer.getCurrentPosition() );
			try {
				TimeUnit.MILLISECONDS.sleep(interval);
			} catch (InterruptedException e) {
			}
		}
		
	}
}
