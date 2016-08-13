package br.ufal.ic.wsn.tmcp;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import br.ufal.ic.wsn.tmcp.simulator.Channels;

public class Test1 {
	
	public static void main(String[] args) throws Exception {
		int numOfChans = 3; // Channels.MAX;
		Graph graph1 = new SingleGraph("Tutorial");
		String nodes = "0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15";
		
		graph1.addAttribute("ui.stylesheet", Channels.STYLES);
		graph1.setStrict(false);
		graph1.setAutoCreate(true);
		
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
		
		graph1.addEdge("1-4", "1", "4").setAttribute("ui.class", "channel_1");
		graph1.addEdge("4-7", "4", "7").setAttribute("ui.class", "channel_1");
		graph1.addEdge("7-10", "7", "10").setAttribute("ui.class", "channel_1");
		
		graph1.addEdge("2-5", "2", "5").setAttribute("ui.class", "channel_2");
		graph1.addEdge("5-8", "5", "8").setAttribute("ui.class", "channel_2");
		graph1.addEdge("8-11", "8", "11").setAttribute("ui.class", "channel_2");
		
		graph1.addEdge("3-6", "3", "6").setAttribute("ui.class", "channel_3");
		graph1.addEdge("6-9", "6", "9").setAttribute("ui.class", "channel_3");
		graph1.addEdge("9-12", "9", "12").setAttribute("ui.class", "channel_3");
		
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
