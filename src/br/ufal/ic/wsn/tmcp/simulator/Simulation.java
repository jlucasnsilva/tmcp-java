package br.ufal.ic.wsn.tmcp.simulator;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomEuclideanGenerator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

/**
 * The mean by which the message is transmitted. Mean
 * is channel hub.
 * 
 * @author João Lucas Nunes e Silva
 * 
 * @param <T> the type the message that the sensors send.
 */
public final class Simulation<T> implements Runnable {

	private int    worldSize;
	
	private int    nOfChannels;
	
	private int    nOfSensors;
	
	private double commRadius;
	
	private double intCoefficient;
	
	private String name;
	
	private Graph graph;
	
	private Map<String, Sensor<T>> sensors;
	
	private Map<String, Set<Sensor<T>>> neighborhood;
	
	private Map<String, Set<Sensor<T>>> intNeighborhood;
	
	private ExecutorService executor;
	
	private String rootID;
	
	private Sensor<T> root;
	
	public Simulation(String name, int worldSize, int nOfChannels, int nOfSensors, double commRadius, double intCoefficient) throws Exception {
		if (worldSize < 1) {
			throw new Exception("The world size must be > 0.");
		}
		
		if (nOfChannels < 1 || nOfChannels > Channels.MAX) {
			throw new Exception("The number of channel must be > 0.");
		}
		
		if (nOfSensors < 1) {
			throw new Exception("There must exist at least one sensor.");
		}
		
		if (commRadius < 1) {
			throw new Exception("Communication radius too short.");
		}
		
		if (name == null || name.equals("")) {
			throw new Exception("The name can't be null or the empty string.");
		}
		
		if (intCoefficient < 0.1) {
			throw new Exception("Interference coefficient has to be at least 0.1.");
		}
		
		this.name        = name;
		this.worldSize   = worldSize;
		this.nOfChannels = nOfChannels;
		this.nOfSensors  = nOfSensors;
		this.commRadius  = commRadius;
		this.intCoefficient = intCoefficient;
		this.executor    = Executors.newCachedThreadPool();
		this.neighborhood    = new HashMap<>();
		this.intNeighborhood = new HashMap<>();
	}

	public void build() throws Exception {
		Generator gen = new RandomEuclideanGenerator();
		
		graph   = new SingleGraph(name);
		sensors = new HashMap<>();
		
		gen.addSink(graph);
		gen.begin();
		for (int i = 0; i < nOfSensors; i++) {
			gen.nextEvents();
		}
		gen.end();
		
		rootID = graph.getNode(0);
		root   = graph.getNode(0);
		
		createNeighborhoods();
		createSensors();
		setupNeighborhoods();
		makeFatTree();
	}
	
	private void makeFatTree() {
		root.setHeight(0);
		
		for (Sensor<T> s : sensors.values()) {
			for (Sensor<T> o : s.getNeighbors()) {
				if ( s.getHeight() < o.getHeight() ) {
					o.setHeight(1 + s.getHeight());
				}
			}
		}
	}

	private void setupNeighborhoods() {
		double intRadius = commRadius * intCoefficient;
		
		/*
		 * TODO graph.getNode(0) is the Sink/BaseStation
		 */
		for (Sensor<T> s : sensors.values()) {
			// TODO do the same thing with the sink/base station
			for (Sensor<T> o : sensors.values()) {
				if ( Point2D.distance(s.x, s.y, o.x, o.y) <= intRadius ) {
					Set<Sensor<T>> sins = intNeighborhood.get(s.id);
					Set<Sensor<T>> oins = intNeighborhood.get(o.id);
					
					sins.add( o );
					oins.add( s );
					// if it's within communication range:
					if ( Point2D.distance(s.x, s.y, o.x, o.y) <= commRadius ) {
						Set<Sensor<T>> sns = neighborhood.get(s.id);
						Set<Sensor<T>> ons = neighborhood.get(o.id);
						
						sns.add( o );
						ons.add( s );
					}
				}
			}
		}
	}

	private void createSensors() throws Exception {
		for (Node n : graph) {
			double x = worldSize * (double) n.getAttribute("x");
			double y = worldSize * (double) n.getAttribute("y");
			String k = n.getId();
			Sensor<T> s;
			
			s = new Sensor<>(k, x, y, commRadius, n, neighborhood.get(k), intNeighborhood.get(k));
			sensors.put(k, s);
			n.addAttribute("sensor", s);
		}
	}

	private void createNeighborhoods() {
		for (Node n : graph) {
			neighborhood.put(n.getId(), new HashSet<>());
			intNeighborhood.put(n.getId(), new HashSet<>());
		}
	}

	@Override
	public void run() {
		for (Sensor<T> s : sensors.values()) {
			executor.execute(s);
		}
		
		while (true) ;
	}
} // end of the class
