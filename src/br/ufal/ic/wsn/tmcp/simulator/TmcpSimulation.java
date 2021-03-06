package br.ufal.ic.wsn.tmcp.simulator;

import static br.ufal.ic.wsn.tmcp.simulator.SensorAttributeSet.bindAttrSet;
import static br.ufal.ic.wsn.tmcp.simulator.SensorAttributeSet.gatt;
import static br.ufal.ic.wsn.tmcp.simulator.Attributes.UI_CLASS;
import static br.ufal.ic.wsn.tmcp.simulator.Attributes.UI_LABEL;
import static br.ufal.ic.wsn.tmcp.simulator.Attributes.UI_STYLESHEET;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import org.graphstream.algorithm.generator.Generator;
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
public final class TmcpSimulation<T> implements ISimulation {

	/**
	 * Name of the simulation/graph.
	 */
	private String name;

	/**
	 * Width and height of the world.
	 */
	private int worldSize;
	
	/**
	 * Number of non-overlapping channels.
	 */
	private int nOfChannels;
	
	/**
	 * Number of sensors.
	 */
	private int nOfSensors;
	
	/**
	 * Radius of communication of each sensor.
	 */
	private double commRadius;
	
	/**
	 * Interference coefficient.
	 * (@code Interference radius = Interference coefficient * Communication Radius}.
	 */
	private double intCoefficient;
	
	/**
	 * The network.
	 */
	private Graph graph;
	
	/**
	 * A thread pool.
	 */
	private ExecutorService executor;
	
	/**
	 * The node used as Sink/Base Station.
	 */
	private Node root;
	
	/**
	 * Total height of the fat tree
	 */
	private int treeHeight;
	
	private EGraphType gtype;
	
	private Map<Integer, List<Node>> treeLevels;
	
	private int sleepTime;

	private IControllerFactory<T> factory;
	
	private ISinkController<T> sinkContoller;

	public static class Args {
		public String name;
		public EGraphType graphType;
		public int    worldSize;
		public int    nOfChannels;
		public int    nOfSensors;
		public int    cycles;
		public double commRadius;
		public double intCoefficient;
		public int    sleep;
	}

	/**
	 * Creates new simulation using the TMCP algorithm.
	 * 
	 * @param name name of the simulation.
	 * @param worldSize world's width and height.
	 * @param nOfChannels number of non-overlapping channels.
	 * @param nOfSensors number of sensors.
	 * @param commRadius radius of communication of each sensor.
	 * @param intCoefficient interference coefficient. (@code Interference radius = Interference coefficient * Communication Radius}.
	 * @throws Exception thrown in case of malformed arguments.
	 */
	public TmcpSimulation(Args a, IControllerFactory<T> factory) throws Exception {
		if (a.worldSize < 1) {
			throw new Exception("The world size must be > 0.");
		}
		
		if (a.nOfChannels < 1 || nOfChannels > Channels.MAX) {
			throw new Exception("The number of channel must be > 0.");
		}
		
		if (a.nOfSensors < 1) {
			throw new Exception("There must exist at least one sensor.");
		}
		
		if (a.commRadius < 1) {
			throw new Exception("Communication radius too short.");
		}
		
		if (a.name == null || a.name.equals("")) {
			throw new Exception("The name can't be null or the empty string.");
		}
		
		if (a.intCoefficient < 0.1) {
			throw new Exception("Interference coefficient has to be at least 0.1.");
		}
		
		if (a.sleep < 0) {
			throw new Exception("Sleep has to be >= 0.");
		}
		
		if (factory == null) {
			throw new Exception("There must be a IControllerFactory");
		}
		
		this.name        = a.name;
		this.worldSize   = a.worldSize;
		this.nOfChannels = a.nOfChannels;
		this.nOfSensors  = a.nOfSensors;
		this.commRadius  = a.commRadius;
		this.intCoefficient = a.intCoefficient;
		this.gtype      = a.graphType;
		this.sleepTime  = a.sleep;
		this.factory    = factory;
		
		this.treeHeight = 0;
		this.treeLevels = new HashMap<>();
		this.executor   = Executors.newCachedThreadPool();
		
		this.sinkContoller = factory.newSinkController(nOfChannels);
		
		build();
	}

	public void init() {
		graph.display();
		
		JOptionPane.showMessageDialog(null, "Creating the fat tree.");
		makeFatTree();
		
		JOptionPane.showMessageDialog(null, "Allocating channels.");
		greedyPMIT();
		
		JOptionPane.showMessageDialog(null, "Job done.");
	}

	/**
	 * Executes the simulation.
	 */
	@Override
	public void execute() {
//		executor.execute(sinkContoller);
//		
//		for (Node n : graph) {
//			executor.execute( n.getAttribute("controller") );
//		}
//		
		while (true);
	}

	/**
	 * Set up the simulation.
	 */
	private void build() {
		generateGraph();
		selectRoot();
		setAttributes();
		setInterferenceSets();
	}
	
	/**
	 * Creates a graph using the Random Euclidean Generator.
	 */
	private void generateGraph() {
		if (gtype == EGraphType.GRID) {
			graph = GraphBuilder.newGrid(name, nOfSensors, worldSize);
		} else if (gtype == EGraphType.RANDOM_EUCLIDEAN) {
			graph = GraphBuilder.newRandomEuclidean(name, nOfSensors, commRadius / worldSize);
		} else if (gtype == EGraphType.CROSS_GRID) {
			graph = GraphBuilder.newCrossGrid(name, nOfSensors, worldSize);
		}
	}
	
	/**
	 * Sets the root (sink/base station) to be the node with the
	 * highest degree.
	 */
	private void selectRoot() {
		root = graph.getNode(0);
		
		for (Node n : graph) {
			if (root.getDegree() < n.getDegree()) {
				root = n;
			}
		}
	}
	
	/**
	 * Set up basic attributes of the nodes, node and the graph.
	 */
	private void setAttributes() {
		for (Node n : graph) {
			double x = n.getAttribute("x");
			double y = n.getAttribute("y");
			
			// The (x, y) positions are given in the [-1, 1] range, so
			// it is multiplied by the world size.
			bindAttrSet(n, new SensorAttributeSet(x * worldSize, y * worldSize));
		}
		
		
		// At first only the root has all channels. As each
		// channel represents a tree.
		for (int i = 0; i < nOfChannels; i++) {
			Set<Node> chanIntSet = new HashSet<>();
			chanIntSet.add(root);
			graph.setAttribute("channel_" + i, chanIntSet);
		}
		
		gatt(root).treeHeight = 0;
		root.setAttribute(UI_LABEL, "SINK");
		root.setAttribute(UI_CLASS, "sink");
		
		graph.setAttribute(UI_STYLESHEET, Channels.STYLES);
	}
	
	/**
	 * Calculates the interference sets of each node. 
	 */
	private void setInterferenceSets() {
		double intRadius = commRadius * intCoefficient;
		
		for (int i = 0; i < graph.getNodeCount(); i++) {
			Node n = graph.getNode(i);
			for (int j = i + 1; j < graph.getNodeCount(); j++) {
				Node m = graph.getNode(j);
				if (!n.equals(m) && dist(n, m) <= intRadius) {
					gatt(n).interferenceSet.add(m);
					gatt(m).interferenceSet.add(n);
				}
			}
		}
	}
	
	/**
	 * Generates the fat tree. A fat tree is a tree in which every
	 * node might have more than one parent.
	 */
	private void makeFatTree() {
		List<Node> verge    = new ArrayList<>(graph.getNodeCount());
		Set<Node>  vergeSet = new HashSet<>();
		List<Node> level   = new ArrayList<>();
		int        len;
		
		verge.add(root);
		vergeSet.add(root);
		
		len = verge.size();
		
		// TODO doesn't need a set o nodes to level 0. In level
		//      0 there is only the root.
		this.treeLevels.put(0, level);
		
		for (int i = 0; i < len; i++) {
			Node n = verge.get(i);
			int nh = gatt(n).treeHeight;
			
			for (Edge e : n.getEdgeSet()) {
				Node m = e.getOpposite(n);
				int mh = gatt(m).treeHeight;
				
				// checks if m is already in the verge set so its
				// addition won't cause infinite loops.
				if ( !vergeSet.contains(m) ) {
					verge.add(m);
					vergeSet.add(m);
					len = verge.size();
				}
				
				if (mh > nh) {
					treeHeight = nh + 1;
					gatt(m).parents.add(n);
					gatt(m).treeHeight = treeHeight;
					
					sleep();
					
					e.setAttribute(UI_CLASS, "fat_tree");
				}
				
				// adds node to the tree level map
				level = treeLevels.get(treeHeight);
				if (level == null) {
					level = new ArrayList<>();
					treeLevels.put(treeHeight, level);
				}
				level.add(m);
			}
		}
	}
	
	/**
	 * Executes the greedy PMIT algorithm to split the nodes
	 * in different channels (trees).
	 */
	private void greedyPMIT() {
		Comparator<Node> comparator = new PmitComparator();
		List<Node>       nodes = null;
		
		for (int level = 1; level <= treeHeight; level++) {
			nodes = treeLevels.get(level);
			nodes.sort(comparator);
			
			for (Node n : nodes) {
				if ( !n.equals(root) && gatt(n).father == null ) {
					ChanNode cn  = findBestChannelAndParent(n);
					Node father  = cn.node;
					int bestChan = cn.channel;
					Set<Node> tree = graph.getAttribute("channel_" + bestChan);

					tree.add(n);
					gatt(n).channel = bestChan;
					gatt(n).father  = father;
					
					sleep();
					
					n.setAttribute(UI_CLASS, "channel_" + bestChan);
					n.getEdgeBetween(father).setAttribute(UI_CLASS, "channel_" + bestChan);
					
//					if ( !father.equals(root) ) {
//						n.setAttribute("controller", factory.newSensorController(father.getAttribute("controller")));
//					} else {
//						n.setAttribute("controller", factory.newSensorController(sinkContoller.getChannelController(bestChan)));
//					}
				}
			}
		}
	}
	
	private class ChanNode {
		public final Node node;
		public final int  channel;
		
		public ChanNode(Node node, int channel) {
			this.node = node;
			this.channel = channel;
		}
	}
	
	/**
	 * Finds the best channel to insert {@node n}. "The criterion is that
	 * the tree must connect to the node and adding brings the least
	 * interference to this tree. If multiple trees tie, the tree with
	 * fewer nodes is chosen." (Yafeng et al, 5.3 The PMIT ALgorithm).
	 * 
	 * @param n
	 * @return
	 */
	private ChanNode findBestChannelAndParent(Node n) {
		if (n.equals(root))
			return null;
		
		Set<Node> parents = gatt(n).parents;
		Node      father  = parents.iterator().next();
		int bestChan      = gatt(father).channel;
		int intVal        = getInterferenceValueAfterAdd(n, bestChan);
		
		if ( !parents.contains(root) ) {
			for (Node p : parents) {
				int pchan      = gatt(p).channel;
				int chanIntVal = getInterferenceValueAfterAdd(n, pchan);
				
				if (intVal > chanIntVal) {
					bestChan = pchan;
					father   = p;
					intVal   = getInterferenceValueAfterAdd(n, bestChan);
				} else if (intVal == chanIntVal) {
					Set<Node> bcis = graph.getAttribute("channel_" + bestChan);
					Set<Node> cis  = graph.getAttribute("channel_" + pchan);
	
					if (bcis.size() > cis.size()) {
						bestChan = pchan;
						father   = p;
						intVal   = getInterferenceValueAfterAdd(n, bestChan);
					}
				}
			}
		} else {
			// nodes that have root in their parents set, only have root
			// as their parent.
			father = root;
			
			for (int pchan = 0; pchan < nOfChannels; pchan++) {
				int chanIntVal = getInterferenceValueAfterAdd(n, pchan);
				
				if (intVal > chanIntVal) {
					bestChan = pchan;
					intVal   = getInterferenceValueAfterAdd(n, bestChan);
				} else if (intVal == chanIntVal) {
					Set<Node> bcis = graph.getAttribute("channel_" + bestChan);
					Set<Node> cis  = graph.getAttribute("channel_" + pchan);
	
					if (bcis.size() > cis.size()) {
						bestChan = pchan;
						intVal   = getInterferenceValueAfterAdd(n, bestChan);
					}
				}
			}
		}
		
		Set<Node> is = graph.getAttribute("channel_" + bestChan);
		is.add(n);
		
		return new ChanNode(father, bestChan);
	}

	/**
	 * Returns the interference value of a channel in case node {@code n} is
	 * inserted in such tree (channel).
	 * 
	 * @param node the node that might be added.
	 * @param channel the channel's id.
	 * @return the interference value of a channel (tree) if {@code n} is added.
	 */
	private int getInterferenceValueAfterAdd(Node n, int channel) {
		Set<Node> nis = gatt(n).interferenceSet;
		return Math.max(nis.size(), getInterferenceValue(channel));
	}
	
	/**
	 * Returns the interference value of a channel (tree).
	 * 
	 * @param channel the channel's id.
	 * @return the interference value of a channel (tree).
	 */
	private int getInterferenceValue(int channel) {
		Set<Node> is = graph.getAttribute("channel_" + channel);
		int i = 0;
		
		for (Node n : is) {
			Set<Node> ins = gatt(n).interferenceSet;
			
			if (i < ins.size()) {
				i = ins.size();
			}
		}
		
		return i;
	}

	/**
	 * Checks if two nodes are within communication range.
	 * 
	 * @param n a node.
	 * @param m another node.
	 * @return true if two nodes are within communication range.
	 */
	private boolean canConnect(Node n, Node m) {
		return dist(n, m) <= commRadius;
	}

	/**
	 * Compares two nodes by the number of parents they have.
	 */
	private class PmitComparator implements Comparator<Node> {
		@Override
		public int compare(Node n, Node m) {
			Set<Node> np = gatt(n).parents;
			Set<Node> mp = gatt(m).parents;
			
			if (np.size() > mp.size()) {
				return 1;
			} else if (np.size() < mp.size()) {
				return -1;
			}
			
			return 0;
		}
	}

	private double dist(Node n, Node m) {
		double nx = gatt(n).x;
		double ny = gatt(n).y;
		double mx = gatt(m).x;
		double my = gatt(m).y;
		
		return Point2D.distance(nx, ny, mx, my);
	}

	private void sleep() {
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Graph getGraph() {
		return graph;
	}

} // end of the class
