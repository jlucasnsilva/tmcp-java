package br.ufal.ic.wsn.tmcp.simulator;

import java.util.Map;

public interface ISensorController<T> {
	public void receive(Sensor<T> sender, T message);
	void execute(Map<Integer, Channel<T>> channels);
}
