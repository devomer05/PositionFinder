public class ProcessorMain {

	public static void main(String[] args) {
		
		Processor p = new Processor();
		String configFileName = args[0];
		p.startRunning(configFileName);

	}

}
