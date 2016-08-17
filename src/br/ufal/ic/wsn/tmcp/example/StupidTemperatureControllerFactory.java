package br.ufal.ic.wsn.tmcp.example;

import br.ufal.ic.wsn.tmcp.simulator.IController;
import br.ufal.ic.wsn.tmcp.simulator.IControllerFactory;
import br.ufal.ic.wsn.tmcp.simulator.ISinkController;

public class StupidTemperatureControllerFactory implements IControllerFactory<Double> {

	@Override
	public IController<Double> newSensorController(IController<Double> parent) {
		return new StupidTemperatureController(parent);
	}

	@Override
	public ISinkController<Double> newSinkController(int numberOfChannels) {
		return new StupidSinkTemperatureController(numberOfChannels);
	}

}
