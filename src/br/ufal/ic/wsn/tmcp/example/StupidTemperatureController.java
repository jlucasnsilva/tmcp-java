package br.ufal.ic.wsn.tmcp.example;

import java.util.Random;

import br.ufal.ic.wsn.tmcp.simulator.IController;

public class StupidTemperatureController implements IController<Double> {

	private final int MIN_TEMPERATURE = 19;

	private final int MAX_TEMP_VARIATION = 12;

	private Random rgen;

	private IController<Double> parent;
	
	private Double temperature;

	private boolean received;

	public StupidTemperatureController(IController<Double> parent) {
		this.parent = parent;
		this.rgen   = new Random();
		this.temperature = randomTemperature();
		this.received = false;
	}

	@Override
	public void run() {
		while (true) {
			synchronized (temperature) {
				if ( !received ) {
					temperature = randomTemperature();
				} else {
					temperature += randomTemperature();
					temperature	/= 2;
				}
				
				parent.receive(temperature);
			}
			
			received = false;
		}
	}

	@Override
	public void receive(Double message) {
		synchronized (temperature) {
			temperature = message;
			received = true;
		}
	}

	private double randomTemperature() {
		return rgen.nextInt(MIN_TEMPERATURE) + rgen.nextInt(MAX_TEMP_VARIATION);
	}
}
