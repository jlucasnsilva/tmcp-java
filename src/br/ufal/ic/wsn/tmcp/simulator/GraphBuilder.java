package br.ufal.ic.wsn.tmcp.simulator;

import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.GridGenerator;
import org.graphstream.algorithm.generator.RandomEuclideanGenerator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

final class GraphBuilder {

	public static Graph newGrid(String name, int nOfNodes, double worldSize) {
		return newGrid(name, nOfNodes, worldSize, false);
	}

	public static Graph newCrossGrid(String name, int nOfNodes, double worldSize) {
		return newGrid(name, nOfNodes, worldSize, true);
	}

	public static Graph newGrid(String name, int nOfNodes, double worldSize, boolean cross) {
		Graph graph = new SingleGraph(name);
		Generator gen = new GridGenerator(cross, false, true);

		gen.addSink(graph);
		gen.begin();
		for(int i=0; i<Math.sqrt(nOfNodes); i++) {
			gen.nextEvents();
		}
		gen.end();
		
		for (Node n : graph) {
			Double[] xy = n.getAttribute("xy");
			n.setAttribute("x", xy[0] / worldSize);
			n.setAttribute("y", xy[1] / worldSize);
		}
		
		return graph;
	}

	public static Graph newRandomEuclidean(String name, int nOfNodes, double commRadius) {
		Graph graph = new SingleGraph(name);
		RandomEuclideanGenerator gen = new RandomEuclideanGenerator();
		
		gen.setThreshold(commRadius);
		
		gen.addSink(graph);
		gen.begin();
		for (int i = 0; i < nOfNodes; i++) {
			gen.nextEvents();
		}
		gen.end();
		
		return graph;
	}

}
