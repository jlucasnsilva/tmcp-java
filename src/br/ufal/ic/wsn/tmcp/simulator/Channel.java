package br.ufal.ic.wsn.tmcp.simulator;

import static java.awt.geom.Point2D.distance;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A communication channel through sensor send messages.
 * 
 * @author João Lucas Nunes e Silva
 *
 * @param <T> the type the message to be sent.
 */
public class Channel<T> {
	
	/**
	 * The channel unique id. It is a higher abstraction
	 * to a channel's frequency.
	 */
	public final int id;

	private Map<Sensor<T>, Set<Sensor<T>>> neighborhoods;
	private Map<Sensor<T>, Set<Sensor<T>>> intNeighborhoods;

	public Channel(int id) {
		this.id = id;

		this.neighborhoods = new HashMap<>();
		this.intNeighborhoods = new HashMap<>();
	}

	/**
	 * Subscribes a sensor to this channel and creates its
	 * communication neighborhood and interference neighborhood.
	 * 
	 * @param s a sensor.
	 */
	public void subscribe(Sensor<T> s) {
		if (s != null) {
			Sensor<T> o;
			double c = Sensors.getInterferenceCoefficient();
			Set<Sensor<T>> ns = new HashSet<>();
			Set<Sensor<T>> ins = new HashSet<>();

			/*
			 * Registers all neighbors of the new node.
			 */
			for(Map.Entry<Sensor<T>, Set<Sensor<T>>> e : neighborhoods.entrySet()) {
				o = e.getKey();

				/*
				 * Calculates all neighbors within the interference
				 * radius.
				 */
				if (distance(s.x, s.y, o.x, o.y) <= s.radius * c) {
					ins.add( o );
					intNeighborhoods.get(o).add( s );

					/*
					 * Calculates all neighbors within the communication
					 * radius.
					 */
					if (distance(s.x, s.y, o.x, o.y) <= s.radius) {
						ns.add( o );
						neighborhoods.get(o).add( s );
					}
				}
			} // end of for
			
			neighborhoods.put(s, ns);
			intNeighborhoods.put(s, ins);
		} // end of if
	} // end of subscribe

	/**
	 * Removes a sensor from this channel of communications.
	 * 
	 * @param s a sensor.
	 */
	public void unsubscribe(Sensor<T> s) {
		for(Sensor<T> x : intNeighborhoods.get(s)) {
			intNeighborhoods.get(x).remove(s);
		}

		for(Sensor<T> x : intNeighborhoods.get(s)) {
			intNeighborhoods.get(x).remove(s);
		}

		neighborhoods.remove(s);
		intNeighborhoods.remove(s);
	}

	/**
	 * Transmits a message from <code>sensor</code> to all of its
	 * neighbors and generates interference in all of its interference
	 * neighbors (including the ones within the communication radius).
	 * 
	 * @param sensor the sender.
	 * @param message the payload.
	 */
	public synchronized void transmit(Sensor<T> sensor, T message) {
		for(Sensor<T> x : neighborhoods.get(sensor)) {
			x.receive(sensor, message);
			// TODO interference
		}
	}
	
	public int countNeighbors(Sensor<T> s) {
		return neighborhoods.get(s).size();
	}
	
	public int countInterferenceNeighbors(Sensor<T> s) {
		return intNeighborhoods.get(s).size();
	}
	
	public boolean equals(Channel<T> c) {
		return id == c.id;
	}
}
