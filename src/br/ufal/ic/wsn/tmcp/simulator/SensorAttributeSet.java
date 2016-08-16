package br.ufal.ic.wsn.tmcp.simulator;

import static br.ufal.ic.wsn.tmcp.simulator.Attributes.UI_LABEL;
import static br.ufal.ic.wsn.tmcp.simulator.Attributes.SENSOR_ATTR_SET;

import java.util.HashSet;
import java.util.Set;

import org.graphstream.graph.Node;

class SensorAttributeSet {

	public static void bindAttrSet(Node n, SensorAttributeSet s) {
		n.setAttribute(SENSOR_ATTR_SET, s);
		n.setAttribute(UI_LABEL, n.getId());
	}

	public static SensorAttributeSet gatt(Node n) {
		return n.getAttribute(SENSOR_ATTR_SET);
	}

	public final double x;

	public final double y;

	public int treeHeight;
	
	public int channel;

	public Node father;

	public final Set<Node> parents;

	public final Set<Node> interferenceSet;

	public SensorAttributeSet(double x, double y) {
		this.x = x;
		this.y = y;
		this.treeHeight = Integer.MAX_VALUE;
		this.channel = 0;
		this.father  = null;
		this.parents = new HashSet<>();
		this.interferenceSet = new HashSet<>();
	}

}
