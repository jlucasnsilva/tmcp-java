package br.ufal.ic.wsn.tmcp.simulator;

import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomEuclideanGenerator;
import org.graphstream.graph.Edge;
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
	
	private ExecutorService executor;
	
	private Node root;
	
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
		this.graph       = new SingleGraph(name);
	}

	public void build() throws Exception {
		Generator gen = new RandomEuclideanGenerator();
		
		gen.addSink(graph);
		gen.begin();
		for (int i = 0; i < nOfSensors; i++) {
			gen.nextEvents();
		}
		gen.end();
		
		root = graph.getNode(0);
		
		setAttributes();
		setIntSets();
		makeFatTree();
		greedyPMIT();
	}
	
	private void setAttributes() {
		for (Node n : graph) {
			double x = n.getAttribute("x");
			double y = n.getAttribute("y");
			Set<Node> ps = new HashSet<>();
			
			n.setAttribute("world_x", x * worldSize);
			n.setAttribute("world_y", y * worldSize);
			n.setAttribute("tree_height", Integer.MAX_VALUE);
			n.setAttribute("parents", ps);
			n.setAttribute("channel", (int) 0);
		}
		
		int interference = 0;
		for (int i = 0; i < nOfChannels; i++) {
			graph.setAttribute("chan_" + i, interference);
		}
	}
	
	private void setIntSets() {
		int count = graph.getNodeCount();
		double intRadius = commRadius * intCoefficient;
		
		// i = 0 is the root.
		for (int i = 1; i < count; i++) {
			Node n = graph.getNode(i);
			double nx = n.getAttribute("world_x");
			double ny = n.getAttribute("world_y");
			
			for (int j = i + 1; j < count; j++) {
				Node m = graph.getNode(j);
				double mx = m.getAttribute("world_x");
				double my = m.getAttribute("world_y");
				
				if (Point2D.distance(nx, ny, mx, my) <= intRadius) {
					Set<Node> sn = n.getAttribute("interference_set");
					Set<Node> sm = m.getAttribute("interference_set");
					
					sn.add(m);
					sm.add(n);
				}
			}
		}
	}
	
	private void makeFatTree() {
		root.setAttribute("tree_height", (int) 0);
		Deque<Node> verge = new ArrayDeque<>(graph.getNodeCount());
		
		verge.addLast(root);
		
		for (Node n : verge) {
			int nh = n.getAttribute("tree_height");
			
			for (Edge e : n.getEdgeSet()) {
				Node m = e.getOpposite(n);
				int mh = m.getAttribute("tree_height");
				Set<Node> mParents = null;
				
				
				if (mh > nh) {
					m.setAttribute("tree_height", nh + 1);
					mParents = m.getAttribute("parents");
					
					mParents.add(n);
					verge.addLast(m);
				}
			}
		}
	}
	
	private void greedyPMIT() {
		
	}

	@Override
	public void run() {
		
	}
} // end of the class
