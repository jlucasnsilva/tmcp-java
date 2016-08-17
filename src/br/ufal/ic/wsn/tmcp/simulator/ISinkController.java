package br.ufal.ic.wsn.tmcp.simulator;

public interface ISinkController<T> extends Runnable {

	IController<T> getChannelController(int channel);

}
