package br.ufal.ic.wsn.tmcp.simulator;

import java.util.HashSet;
import java.util.Set;
import org.graphstream.graph.Node;
import static br.ufal.ic.wsn.tmcp.simulator.Attributes.*;

public class Nodes {
	
	public static void addParent(Node n, Node parent) {
		Set<Node> parents = n.getAttribute(PARENTS);
		
		if (parents == null) {
			parents = new HashSet<Node>();
			n.setAttribute(PARENTS, parents);			
		}
		
		parents.add( parent );
	}
	
	public static void addToIntSet(Node n, Node m) {
		Set<Node> intSet = n.getAttribute(INTERFERENCE_SET);
		
		if (intSet == null) {
			intSet = new HashSet<Node>();
			n.setAttribute(INTERFERENCE_SET, intSet);			
		}
		
		intSet.add(m);
	}
	
	public static void setWorldX(Node n, double x) {
		n.setAttribute(WORLD_X, x);
	}
	
	public static void setWorldY(Node n, double y) {
		n.setAttribute(WORLD_Y, y);
	}
	
	public static void setTreeHeight(Node n, int h) {
		n.setAttribute(TREE_HEIGHT, h);
	}
	
	public static void setTreeHeight(Node n) {
		n.setAttribute(TREE_HEIGHT, (int) 0);
	}
	
	public static void setChannel(Node n, int channel) {
		n.setAttribute(CHANNEL, channel);
	}
	
	public static void setChannel(Node n) {
		n.setAttribute(CHANNEL, (int) 0);
	}
	
	public static void setUiLabel(Node n, String label) {
		n.setAttribute(UI_LABEL, label);
	}
	
	public static void setUiLabel(Node n) {
		n.setAttribute(UI_LABEL, n.getId());
	}
	
	public static void setUiClass(Node n, String c) {
		n.setAttribute(UI_CLASS, c);
	}

	public static Set<Node> getParents(Node n) {
		Set<Node> parents = n.getAttribute(PARENTS);
		
		if (parents == null) {
			parents = new HashSet<Node>();
			n.setAttribute(PARENTS, parents);			
		}
		
		return parents;
	}
	
	public static Set<Node> getIntSet(Node n) {
		Set<Node> intSet = n.getAttribute(INTERFERENCE_SET);
		
		if (intSet == null) {
			intSet = new HashSet<Node>();
			n.setAttribute(INTERFERENCE_SET, intSet);			
		}
		
		return intSet;
	}
	
	public static double getWorldX(Node n) {
		return n.getAttribute(WORLD_X);
	}
	
	public static double getWorldY(Node n) {
		return n.getAttribute(WORLD_Y);
	}
	
	public static int getTreeHeight(Node n) {
		return n.getAttribute(TREE_HEIGHT);
	}
	
	public static int getChannel(Node n) {
		return n.getAttribute(CHANNEL);
	}

}
