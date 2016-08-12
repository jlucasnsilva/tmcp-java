package br.ufal.ic.wsn.tmcp;

import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomEuclideanGenerator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import br.ufal.ic.wsn.tmcp.simulator.Channels;

public class Main {
	private static void sleep() {
        try { Thread.sleep(1000); } catch (Exception e) {}
    }
	
	public static void main(String[] args) {
		int numOfChans = Channels.MAX_AMOUNT;
		Graph graph1 = new SingleGraph("Tutorial");
		String nodes = "0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15";
		
		graph1.addAttribute("ui.stylesheet", Channels.COLORS);
		graph1.setStrict(false);
		graph1.setAutoCreate(true);
		
		graph1.display();
		
		int k = 1;
		for (String nname : nodes.split(" ")) {
			Node n = graph1.addNode(nname);
			Edge e = null;
			
			if (nname.equals("0")) {
				n.addAttribute("ui.label", "SINK");
			} else {
				e = graph1.addEdge("0-" + nname, "0", nname);
				
				n.setAttribute("ui.label", nname);
				e.setAttribute("ui.label", "chan=" + k);
				
				n.setAttribute("ui.class", "channel_" + k);
				e.setAttribute("ui.class", "channel_" + k);
				
				k++; if (k > numOfChans) k = 1;
			}
			
			//sleep();
		}
		
		graph1.display();
		
		/*
		Graph graph2 = new SingleGraph("Tutorial");
		Generator g = new RandomEuclideanGenerator();
		
		g.addSink(graph2);
		g.begin();
		for (int i = 0; i < 100; i++) {
			g.nextEvents();
		}
		g.end();
		
		graph2.display(false);
		*/
	}
}
