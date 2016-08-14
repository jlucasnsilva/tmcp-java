package br.ufal.ic.wsn.tmcp.simulator;

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
	 * Width and height of the world.
	 */
	private int    worldSize;
	
	/**
	 * Number of non-overlapping channels.
	 */
	private int    nOfChannels;
	
	/**
	 * Number of sensors.
	 */
	private int    nOfSensors;
	
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
	public TmcpSimulation(String name, EGraphType gtype, int worldSize, int nOfChannels, int nOfSensors, double commRadius, double intCoefficient, int cycles) throws Exception {
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
		
		this.worldSize   = worldSize;
		this.nOfChannels = nOfChannels;
		this.nOfSensors  = nOfSensors;
		this.commRadius  = commRadius;
		this.intCoefficient = intCoefficient;
		this.executor    = Executors.newCachedThreadPool();
		this.graph       = new SingleGraph(name);
		this.treeHeight  = 0;
		this.gtype       = gtype;
		this.treeLevels  = new HashMap<>();
	}

	/**
	 * Set up the simulation.
	 */
	public void build() {
		generateGraph();
		selectRoot();
		setAttributes();
		setIntSets();
	}
	
	public void algorithm() {
		makeFatTree();
		// graph.display();
		greedyPMIT();
		graph.display();
	}
	
	/**
	 * Executes the simulation.
	 */
	@Override
	public void simulate() {
		for (Node n : graph) {
			AbstractController<T> c = n.getAttribute("controller");
			this.executor.execute(c);
		}
		
		while (true);
	}
	
	/**
	 * Creates a graph using the Random Euclidean Generator.
	 */
	private void generateGraph() {
		if (gtype == EGraphType.RANDOM) {
			graph = GraphBuilder.newRandom(nOfSensors, nOfChannels);
		} else if (gtype == EGraphType.DOROGOVTSEV_MENDES) {
			graph = GraphBuilder.newDorogvtsevMendes(nOfSensors, nOfChannels);
		} else if (gtype == EGraphType.BARABASI_ALBERT) {
			graph = GraphBuilder.newBarabasiAlbert(nOfSensors, nOfChannels);
		} else if (gtype == EGraphType.GRID) {
			graph = GraphBuilder.newGrid(nOfSensors, nOfChannels);
		} else if (gtype == EGraphType.SMALL_WORLD) {
			graph = GraphBuilder.newSmallWorld(nOfSensors, nOfChannels);
		} else if (gtype == EGraphType.RANDOM_EUCLIDEAN) {
			graph = GraphBuilder.newRandomEuclidean(nOfSensors, nOfChannels);
		}
	}
	
	/**
	 * Selects the node with the highest degree to
	 * be the root (sink/base station).
	 */
	private void selectRoot() {
		/*
		 * Selects the node with the highest degree to
		 * be the root (sink/base station).
		 */
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
			
			Nodes.setWorldX(n, x * worldSize); // As x and y in the range [0,1] they
			Nodes.setWorldY(n, y * worldSize); // are converted to world positions
			Nodes.setTreeHeight(n, Integer.MAX_VALUE);
			Nodes.setChannel(n);
			Nodes.setUiLabel(n);
		}
		
		/*
		 * At first only the root has all channels. As each
		 * channel represents a tree. It's the only one in
		 * every tree.
		 */
		for (int i = 0; i < nOfChannels; i++) {
			Set<Node> chanIntSet = new HashSet<>();
			chanIntSet.add(root);
			graph.setAttribute("channel_" + i, chanIntSet);
		}
		
		root.setAttribute("SINK");
		Nodes.setTreeHeight(root);
		Nodes.setUiLabel(root, "SINK");
		Nodes.setUiClass(root, "channel_9");
		
		graph.setAttribute("ui.stylesheet", Channels.STYLES);
	}
	
	/**
	 * Calculates the interference sets of each node. 
	 */
	private void setIntSets() {
		double intRadius = commRadius * intCoefficient;
		
		for (int i = 0; i < graph.getNodeCount(); i++) {
			Node n = graph.getNode(i);
			for (int j = i + 1; j < graph.getNodeCount(); j++) {
				Node m = graph.getNode(j);
				if (!n.equals(m) && dist(n, m) <= intRadius) {
					Nodes.addToIntSet(n, m);
					Nodes.addToIntSet(m, n);
				}
			}
		}
	}
	
	/**
	 * Generates the fat tree of the network. A fat tree is a tree
	 * in which every node might have more than one parent.
	 */
	private void makeFatTree() {
		List<Node> verge    = new ArrayList<>(graph.getNodeCount());
		Set<Node>  vergeSet = new HashSet<>();
		List<Node> tLevel   = new ArrayList<>();
		
		verge.add(root);
		vergeSet.add(root);
		treeLevels.put(0, tLevel);
		int len = verge.size();
		for (int i = 0; i < len; i++) {
			Node n = verge.get(i);
			int nh = Nodes.getTreeHeight(n);
			
			for (Edge e : n.getEdgeSet()) {
				Node m = e.getOpposite(n);
				int mh = Nodes.getTreeHeight(m);
				
				// checks if m is already in the verge set so its
				// addition won't cause infinite loops.
				if ( !vergeSet.contains(m) ) {
					verge.add(m);
					vergeSet.add(m);
					len = verge.size();
				}
				
				if (mh > nh) {
					treeHeight = nh + 1;
					Nodes.addParent(m, n);
					Nodes.setTreeHeight(m, treeHeight);
				}
				
				// adds node to the tree level map
				tLevel = treeLevels.get(treeHeight);
				if (tLevel == null) {
					tLevel = new ArrayList<>();
					treeLevels.put(treeHeight, tLevel);
				}
				tLevel.add(m);
			}
		}
	}
	
	/**
	 * Executes the greedy PMIT algorithm to split the nodes
	 * in different channels (trees).
	 */
	private void greedyPMIT() {
		for (int level = 1; level <= treeHeight; level++) {
			List<Node> nodes = treeLevels.get(level);
			nodes.sort(new PmitComparator());
			
			for (Node n : nodes) {
				if ( !n.equals(root) ) {
					ChanNode cn    = findBestChannelAndParent(n);
					int bestChan   = cn.channel;
					Node father    = cn.node;
					Set<Node> tree = graph.getAttribute("channel_" + bestChan);

					tree.add(n);
					n.setAttribute("channel", bestChan);
					n.setAttribute("father", father);

					n.setAttribute("ui.class", "channel_" + bestChan);
					n.getEdgeBetween(father).setAttribute("ui.class", "channel_" + bestChan);
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
		
		Set<Node> parents = Nodes.getParents(n);
		Node      father  = parents.iterator().next();
		int bestChan      = Nodes.getChannel(father);
		int intVal        = getInterferenceValueAfterAdd(n, bestChan);
		
		if ( !parents.contains(root) ) {
			for (Node p : parents) {
				int pchan      = Nodes.getChannel(p);
				int intChanVal = getInterferenceValueAfterAdd(n, pchan);
				
				if (intVal > intChanVal) {
					bestChan = pchan;
					father   = p;
				} else if (intVal == intChanVal) {
					Set<Node> bcis = graph.getAttribute("channel_" + bestChan);
					Set<Node> cis  = graph.getAttribute("channel_" + pchan);
	
					if (bcis.size() > cis.size()) {
						bestChan = pchan;
						father   = p;
					}
				}
			}
		} else {
			// nodes that have root in their parents set, only have root as their
			// only parent.
			father = root;
			
			for (int pchan = 0; pchan < nOfChannels; pchan++) {
				int intChanVal = getInterferenceValueAfterAdd(n, pchan);
				
				if (intVal > intChanVal) {
					bestChan = pchan;
				} else if (intVal == intChanVal) {
					Set<Node> bcis = graph.getAttribute("channel_" + bestChan);
					Set<Node> cis  = graph.getAttribute("channel_" + pchan);
	
					if (bcis.size() > cis.size()) {
						bestChan = pchan;
					}
				}
			}
		}
		
		Set<Node> is = graph.getAttribute("channel_" + bestChan);
		is.add(n);
		
		return new ChanNode(father, bestChan);
	}

	/**
	 * Returns the interference value of a channel in case
	 * node {@code n} is inserted in such tree (channel).
	 * 
	 * @param node the node that might be added.
	 * @param channel the channel's id.
	 * @return the interference value of a channel (tree) if {@code n} is added.
	 */
	private int getInterferenceValueAfterAdd(Node n, int channel) {
		Set<Node> nis = Nodes.getIntSet(n);
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
			Set<Node> ins = Nodes.getIntSet(n);
			
			if (i < ins.size()) {
				i = ins.size();
			}
		}
		
		return i;
	}
	
	private boolean canConnect(Node n, Node m) {
		return dist(n, m) <= commRadius;
	}
	
	/**
	 * @deprecated
	 * Checks if there is any node in the tree/channel that is in
	 * the communication range of {@code n}. Then returns such node.
	 * 
	 * @param n a node.
	 * @param channel the channel's id.
	 * @return the node {@code m} to which {@code n} can connect in {@code channel}.
	 */
	@Deprecated
	private Node canConnect(Node n, int channel) {
		Set<Node> is = graph.getAttribute("channel_" + channel);
		
		for (Node m : is) {
			if (n.getEdgeBetween(m) != null /*&& dist(n, m) <= commRadius*/) {
				return m;
			}
		}
		
		if (n.getEdgeBetween(root) != null) {
			return root;
		}
		
		return null;
	}

	private class PmitComparator implements Comparator<Node> {
		@Override
		public int compare(Node n, Node m) {
			Set<Node> np = Nodes.getParents(n);
			Set<Node> mp = Nodes.getParents(m);
			
			if (np.size() > mp.size()) {
				return 1;
			} else if (np.size() < mp.size()) {
				return -1;
			}
			
			return 0;
		}
	}
	
	private double dist(Node n, Node m) {
		double nx = n.getAttribute("world_x");
		double ny = n.getAttribute("world_y");
		double mx = m.getAttribute("world_x");
		double my = m.getAttribute("world_y");
		
		return Point2D.distance(nx, ny, mx, my);
	}
	
	public Graph getGraph() {
		return graph;
	}
} // end of the class
