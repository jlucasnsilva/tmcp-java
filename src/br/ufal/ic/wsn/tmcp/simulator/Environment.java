package br.ufal.ic.wsn.tmcp.simulator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The mean by which the message is transmitted. Mean
 * is channel hub.
 * 
 * @author João Lucas Nunes e Silva
 * 
 * @param <T> the type the message that the sensors send.
 */
public final class Environment<T> {

	private Map<Integer, Channel<T>> channels;
	private Set<Sensor<T>> sensors;
	private ExecutorService threadPool;

	/**
	 * Creates a new Mean with <code>numberOfChannels</code> channels
	 * enumerated from 0 to <code>numberOfChannels - 1</code>.
	 * 
	 * @param numberOfChannels number of available channels for use.
	 * @throws Exception thrown if numberOfChannels < 1.
	 */
	public Environment(
			int worldSize,
			int numberOfChannels,
			int numberOfSensors,
			double commRadius,
			Sensor<T> s
	) throws Exception {
		if (numberOfChannels < 1) {
			throw new Exception("The number of channel must be > 0.");
		}

		this.channels   = new HashMap<>();
		this.sensors    = new HashSet<>();
		this.threadPool = Executors.newCachedThreadPool();
		this.channels   = Channels.<T>newChannels(numberOfChannels);
		this.sensors    = Sensors.newSensors(worldSize, numberOfSensors, commRadius);
	}

	public final Channel<T> getChannel(int channelID) {
		return channels.get(channelID);
	}

	public void simulate() {
		for (Sensor<T> s : sensors) {
			threadPool.execute(s);
		}

		while(true) {}
	}
} // end of the class
