package br.ufal.ic.wsn.tmcp.simulator;


import org.graphstream.graph.Node;

public abstract class AbstractController<T> implements Runnable{
	
	public AbstractController(Node node){
		
		node.setAttribute("controller", this);
	}
	
	public void receive(Node node, T message){
		node.setAttribute("message", message);
	}	
}
