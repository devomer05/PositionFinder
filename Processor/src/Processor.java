import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Processor {
	
	private Thread sensor1ListenerThread;
	private Thread sensor2ListenerThread;
	private Thread positionCalculaterThread;
	private Thread retrieverThread;
	
	private LocationRetriever retriever;
	private PositionCalculater pc;
	private SensorListener s1;
	private SensorListener s2;
	private int interval;
	
	public boolean ReadConfig(String configFileStr)
	{
		File configFile = new File(configFileStr);
		try {
		    FileReader reader = new FileReader(configFile);
		    Properties props = new Properties();
		    props.load(reader);
		    
		    interval = Integer.parseInt(props.getProperty("interval"));
		    
		    int multicastPort = Integer.parseInt(props.getProperty("multicast_port"));
		    System.out.println("Multicast port is: " + multicastPort);
		    
		    String multicastIP = props.getProperty("multicast_ip");
		    System.out.println("Multicast ip is: " + multicastIP);

			retriever =  new LocationRetriever( multicastPort, multicastIP );
		    
			String ip1 = props.getProperty("sensor1_ip");
		    int sensor1Port = Integer.parseInt(props.getProperty("sensor1_port"));
		    System.out.println("Sensor1: ip= " + ip1 + " port= " + sensor1Port);
		   
		    String ip2 = props.getProperty("sensor2_ip");
		    int sensor2Port = Integer.parseInt(props.getProperty("sensor2_port"));
		    System.out.println("Sensor2: ip= " + ip2 + " port= " + sensor2Port);
		    
		    s1 = new SensorListener(1, ip1, sensor1Port);
			s2 = new SensorListener(2, ip2, sensor2Port);
			pc = new PositionCalculater(s1, s2);
			
		    reader.close();
		    return true;
		} catch (FileNotFoundException ex) {
		    return false;
		} catch (IOException ex) {
		    return false;
		}
	}
	
	public void startRunning( String configFileStr)
	{
		if(!ReadConfig(configFileStr))
		{
			System.out.print("Can not read config file");
		}
		
		sensor1ListenerThread = new Thread( s1 );
		sensor2ListenerThread = new Thread( s2 );
		positionCalculaterThread = new Thread(pc);
		retrieverThread = new Thread(retriever);
		
		sensor1ListenerThread.start();
		sensor2ListenerThread.start();
		
		positionCalculaterThread.start();
		retrieverThread.start();
		Vector2D realPos = new Vector2D();
		while(true) 
		{
			try {
				Vector2D calculatedTargetPos, s1Pos, s2Pos, lastRealPos;
				calculatedTargetPos = pc.getCalculatedPosition();
				s1Pos  = s1.getLastPosition();
				s2Pos = s2.getLastPosition();
				lastRealPos = retriever.GetTargetPos();
				if(lastRealPos != null)
				{
					realPos = lastRealPos;
				}
				//System.out.println("Calculated: " + pc.getCalculatedPosition() + " Real: " + retriever.GetTargetPos() + " S1: " + s1.getLastPosition() + " s2: " + s2.getLastPosition());
				
				String map = "";
				
				for(int i = 0; i < 50; i++)
					map += "\n";
				
				
				for( int i = 0; i < 100; i++ ) {
					map += "-";
				}
				map += "\n";
				for(int j = 25; j > -25; j--)
				{
					map += "|";
					for(int i = -50; i < 50; i++)
					{
						int calculatedX = (int)((calculatedTargetPos.getX() + 0.5) / 10);
						int calculatedY = (int)((calculatedTargetPos.getY() + 0.5) / 20);
						int realX = (int)((realPos.getX() + 0.5) / 10);
						int realY = (int)((realPos.getY() + 0.5) / 20);
						int s1X = (int) ((s1Pos.getX() + 0.5) / 10); 
						int s1Y = (int) ((s1Pos.getY() + 0.5) / 20); 
						int s2X = (int) ((s2Pos.getX() + 0.5) / 10); 
						int s2Y = (int) ((s2Pos.getY() + 0.5) / 20); 
						
						if( i == calculatedX && j == calculatedY  )
							map += "X";
						else if( i == realX && j == realY  )
							map += "O";
						else if( i == s1X && j == s1Y )
							map += "1";
						else if( i == s2X && j == s2Y )
							map += "2";
						else if(i == 0)
							map += "|";
						else if(j == 0)
							map += "-";
						else
							map += " ";
					}
					map += "|\n";
				}
				for( int i = 0; i < 100; i++ ) {
					map += "-";
				}
				System.out.println(map);
				TimeUnit.MILLISECONDS.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
