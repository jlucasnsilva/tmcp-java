package br.ufal.ic.wsn.tmcp.simulator;

import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.GridGenerator;
import org.graphstream.algorithm.generator.RandomEuclideanGenerator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.algorithm.generator.WattsStrogatzGenerator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

final class GraphBuilder {
	private static Graph addPosToNodes(Graph g) {
		for (Node n : g) {
			n.setAttribute("x", Math.random());
			n.setAttribute("y", Math.random());
		}
		
		return g;
	}
	
	public static Graph newRandom(int nOfNodes, int nOfChannels) {
		Graph graph = new SingleGraph("grid");
		Generator gen = new RandomGenerator();

		gen.addSink(graph);
		gen.begin();
		for(int i=0; i < nOfNodes; i++) {
			gen.nextEvents();
		}
		gen.end();
		
		return addPosToNodes(graph);
	}
	
	public static Graph newDorogvtsevMendes(int nOfNodes, int nOfChannels) {
		Graph graph = new SingleGraph("Dorogovtsev mendes");
		Generator gen = new DorogovtsevMendesGenerator();
		
		gen.addSink(graph);
		gen.begin();

		for(int i = 0; i < nOfNodes; i++) {
			gen.nextEvents();
		}

		gen.end();
		
		return addPosToNodes(graph);
	}
	
	public static Graph newBarabasiAlbert(int nOfNodes, int nOfChannels) {
		Graph graph = new SingleGraph("Barabàsi-Albert");
		Generator gen = new BarabasiAlbertGenerator(nOfChannels);

		gen.addSink(graph); 
		gen.begin();

		for(int i=0; i<nOfNodes; i++) {
			gen.nextEvents();
		}

		gen.end();
		graph.display();
		
		return addPosToNodes(graph);
	}
	
	public static Graph newGrid(int nOfNodes, int nOfChannels) {
		Graph graph = new SingleGraph("grid");
		Generator gen = new GridGenerator();

		gen.addSink(graph);
		gen.begin();

		for(int i=0; i<nOfNodes; i++) {
			gen.nextEvents();
		}

		gen.end();
		
		return addPosToNodes(graph);
	}
	
	public static Graph newSmallWorld(int nOfNodes, int nOfChannels) {
		Graph graph = new SingleGraph("This is a small world!");
		Generator gen = new WattsStrogatzGenerator(nOfNodes, 4, 0.5);

		gen.addSink(graph);
		gen.begin();
		while(gen.nextEvents()) {}
		gen.end();
		
		return addPosToNodes(graph);
	}
	
	public static Graph newRandomEuclidean(int nOfNodes, int nOfChannels) {
		Graph graph = new SingleGraph("grid");
		Generator gen = new RandomEuclideanGenerator();
		
		gen.addSink(graph);
		gen.begin();
		for (int i = 0; i < nOfNodes; i++) {
			gen.nextEvents();
		}
		gen.end();
		
		return graph;
	}
}
