package br.ufal.ic.wsn.tmcp.simulator;

import org.graphstream.graph.Node;
import static br.ufal.ic.wsn.tmcp.simulator.Attributes.*;

public class Nodes {

	public static void setUiLabel(Node n, String label) {
		n.setAttribute(UI_LABEL, label);
	}
	
	public static void setUiLabel(Node n) {
		n.setAttribute(UI_LABEL, n.getId());
	}
	
	public static void setUiClass(Node n, String c) {
		n.setAttribute(UI_CLASS, c);
	}

}
