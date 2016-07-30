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
public class Environment<T> {
	
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
	public Environment(int numberOfChannels) throws Exception {
		if (numberOfChannels < 1) {
			throw new Exception("The number of channel must be > 0.");
		}
		
		channels = new HashMap<>();
		sensors = new HashSet<>();
		threadPool = Executors.newCachedThreadPool();
		
		for(int i = 0; i < numberOfChannels; ++i) {
			channels.put(i, new Channel<T>(i));
		}
	}
	
	public void addSensor(Sensor<T> s) {
		sensors.add(s);
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
