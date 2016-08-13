package br.ufal.ic.wsn.tmcp;

import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomEuclideanGenerator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class Test2 {
	public static void main(String[] args) {
		Graph g = new SingleGraph("Test2 Graph");
		Generator gen = new RandomEuclideanGenerator(2);
		
		gen.addSink(g);
		gen.begin();
		for (int i = 0; i < 500; i++) {
			gen.nextEvents();
		}
		gen.end();
		
		for (Node n : g) {
			System.out.println("" + n.getId() + " >> " + (100 * (double) n.getAttribute("x")) + " | " + (100 * (double) n.getAttribute("y")));
		}
		
		g.display();
	}
}
