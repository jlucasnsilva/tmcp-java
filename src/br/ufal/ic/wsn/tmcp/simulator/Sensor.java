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
	 * The "software" running in the sensor.
	 */
	private ISensorController<T> controller;

	public Sensor(
			long id,
			double x,
			double y,
			double radius
	) throws Exception {
		this.id = id;
		this.x  = x;
		this.y  = y;
		this.radius = radius;
		
		if (radius <= 0) {
			throw new Exception("The radius must be > 0.");
		}
	}

	public void setController(ISensorController<T> controller) {
		this.controller = controller;
	}

	/**
	 * Receives a message from a channel.
	 * 
	 * @param sender the sensor the sent the message.
	 * @param message the payload.
	 */
	public void receive(Sensor<T> sender, T message) {
		if (controller != null) {
			controller.receive(sender, message);
		}
	}

	public boolean equals(Sensor<T> s) {
		return s.id == id;
	}

	@Override
	public void run() {
		if (controller != null) {
			// TODO
			controller.execute(null);
		}
	}
}
