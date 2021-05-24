import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Sensor {

	private int id;
	
	private Thread retrieverThread;
	private Thread senderThread;
	private Thread transposerThread;
	private Transposer transposer;
	private Sender sender;
	private LocationRetriever retriever;
	
	
	public Sensor(int id)
	{
		this.id = id;
		this.retriever = null;
		this.transposer = null;
		this.sender = null;
		this.retrieverThread = null;
		this.transposerThread = null;
		this.senderThread = null;
	}

	public boolean ReadConfig(String configFileStr)
	{
		File configFile = new File(configFileStr);
		try {
		    FileReader reader = new FileReader(configFile);
		    Properties props = new Properties();
		    props.load(reader);
		    
		    int multicastPort = Integer.parseInt(props.getProperty("multicast_port"));
		    System.out.println("Multicast port is: " + multicastPort);
		    
		    String multicastIP = props.getProperty("multicast_ip");
		    System.out.println("Multicast ip is: " + multicastIP);

			retriever =  new LocationRetriever( multicastPort, multicastIP );
			
		    int senderPort = Integer.parseInt(props.getProperty("sensor" + id + "_port"));
		    System.out.println("Sensor send port is: " + senderPort);
		    
		    int errorMargin = Integer.parseInt(props.getProperty("error_margin"));
		    System.out.println("Error margin: " + errorMargin);
		    
		    int interval = Integer.parseInt(props.getProperty("interval"));
		    sender = new Sender( this, senderPort );
		    sender.setErrorMargin(errorMargin);

		    Vector2D selfPos = new Vector2D();
		    selfPos.setX(Double.parseDouble(props.getProperty("sensor" + id + "_pos_x")));
		    selfPos.setY(Double.parseDouble(props.getProperty("sensor" + id + "_pos_y")));
		    System.out.println("Pos is: ( " + selfPos.getX() + ", " + selfPos.getY() + " )");
		    
		    double speed = Integer.parseInt(props.getProperty("sensor_speed"));
		    
		    transposer = new Transposer();
		    transposer.setSpeed(speed);
			transposer.setCurrentPosition(selfPos);
			transposer.setUpdatePeriod(interval);
			
			File file = new File("./patrol"+ id + ".txt");
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
	
	public void startRunning(String configFileStr)
	{
		if(!ReadConfig(configFileStr))
		{
			System.out.print("Can not read config file");
		}
		
		retrieverThread = new Thread( retriever );
		senderThread = new Thread( sender );
		transposerThread = new Thread( transposer );
		
		retrieverThread.start();
		senderThread.start();
		transposerThread.start();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Vector2D getSelfPos() {
		return transposer.getCurrentPosition();
	}

	public Vector2D getTargetPos() {
		return retriever.GetTargetPos();
	}

}
