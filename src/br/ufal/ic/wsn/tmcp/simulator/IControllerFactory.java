package br.ufal.ic.wsn.tmcp.simulator;

public interface IControllerFactory<T> {

	IController<T> newSensorController(IController<T> parent);

	ISinkController<T> newSinkController(int numberOfChannels);

}
