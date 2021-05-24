public class SensorMain {
	
	public static void main(String[] args) {
		
		int sensorId = Integer.parseInt(args[1]);
		Sensor s = new Sensor(sensorId);
		String configFileName = args[0];
		s.startRunning(configFileName);
		
	}
}
