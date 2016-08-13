package br.ufal.ic.wsn.tmcp.simulator;

import java.util.HashSet;
import java.util.Set;

import org.graphstream.graph.Node;

public class Sensor<T> implements Runnable {

	/**
	 * Sensor's unique id.
	 */
	public final String id;

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
	private AbstractController<T> controller;
	
	private Node node;
	
	private int chan;
	
	private Set<Sensor<T>> neighborhood;
	
	private Set<Sensor<T>> intNeighborhood;
	
	private Set<Sensor<T>> parents;
	
	private int height;

	Sensor(String id, double x, double y, double radius, Node node, Set<Sensor<T>> neighborhood, Set<Sensor<T>> intNeighborhood) throws Exception {
		// TODO check arguments
		this.id = id;
		this.x  = x;
		this.y  = y;
		this.radius = radius;
		this.node = node;
		this.neighborhood = neighborhood;
		this.intNeighborhood = intNeighborhood;
		this.parents = new HashSet<>();
		this.height = Integer.MAX_VALUE;
	}
	
	public void addParent(Sensor<T> p) {
		parents.add(p);
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Set<Sensor<T>> getNeighbors() {
		return neighborhood;
	}

	public void setController(AbstractController<T> controller) {
		this.controller = controller;
	}

	/**
	 * Receives a message from a channel.
	 * 
	 * @param sender the sensor the sent the message.
	 * @param message the payload.
	 */
	public void receive(Node sender, T message) {
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
			//controller.execute(null);
		}
	}
}
