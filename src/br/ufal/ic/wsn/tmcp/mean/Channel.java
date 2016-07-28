package br.ufal.ic.wsn.tmcp.mean;

import java.util.HashSet;
import java.util.Set;

public class Channel<T> {
	public final int id;
	
	private Set<AbstractSensor<T>> sensors;
	
	public Channel(int id) {
		this.id = id;
		
		sensors = new HashSet<>();
	}
	
	public void subscribe(AbstractSensor<T> s) {
		// TODO
	}
	
	public void unsubscribe(AbstractSensor<T> s) {
		// TODO
	}
	
	public synchronized void transmit(AbstractSensor sensor, T message) {
		for (AbstractSensor<T> s : sensors) {
			if() {
				
			}
		}
	}
}
