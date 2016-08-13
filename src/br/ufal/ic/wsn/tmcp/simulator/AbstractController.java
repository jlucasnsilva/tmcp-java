package br.ufal.ic.wsn.tmcp.simulator;


import org.graphstream.graph.Node;

public abstract class AbstractController<T> implements Runnable {
	
	private Node node;
	
	public AbstractController(Node node){
		this.node = node;
		node.setAttribute("controller", this);
	}
	
	public void receive(T message){
		node.setAttribute("message", message);
	}	
}
