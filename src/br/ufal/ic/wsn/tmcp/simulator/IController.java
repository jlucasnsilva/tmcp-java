package br.ufal.ic.wsn.tmcp.simulator;

public interface IController<T> extends Runnable {

	abstract public void receive(T message);

}
