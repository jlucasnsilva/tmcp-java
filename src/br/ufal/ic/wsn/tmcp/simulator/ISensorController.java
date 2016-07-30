package br.ufal.ic.wsn.tmcp.simulator;

public interface ISensorController<T> extends Runnable {
	void receive(Sensor<T> sender, T message);
	void run();
}
