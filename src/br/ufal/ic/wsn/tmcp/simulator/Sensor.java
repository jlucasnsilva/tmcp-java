package br.ufal.ic.wsn.tmcp.simulator;

public class Sensor<T> implements Runnable {
		
	/**
	 * Sensor's unique id.
	 */
	public final long  id;
	/**
	 * X position on the environment.
	 */
	public final double x;
	/**
	 * Y position on the environment.
	 */
	public final double y;
	/**
	 * Transmission radius.
	 */
	public final double radius;
	/**
	 * The application that runs in the sensor.
	 */
	private ISensorController<T> controller; 
	
	public Sensor(int x, int y, int radius) throws Exception {
		this(null, x, y, radius);
	}
	
	public Sensor(ISensorController<T> controller, int x, int y, int radius) throws Exception {
		this.id = Sensors.getNextID();
		this.x  = x;
		this.y  = y;
		this.radius = radius;
		this.controller = controller;
		
		if (radius == 0) {
			throw new Exception("The radius must be != 0.");
		}
	}
	
	/**
	 * Receives a message and passes it to he controller. If
	 * the controller is null it is considered a do-nothing-controller.
	 * 
	 * @param sender the sensor the sent the message.
	 * @param message the payload.
	 */
	void receive(Sensor<T> sender, T message) {
		if(sender != null && message != null) {
			controller.receive(sender, message);
		}
	}

	/**
	 * Executes the controller. If the controller is null it is
	 * considered a do-nothing-controller.
	 */
	@Override
	public void run() {
		if (controller != null) {
			controller.run();
		}
	}
	
	public boolean equals(Sensor<T> s) {
		return s.id == id;
	}
}
