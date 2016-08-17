package br.ufal.ic.wsn.tmcp.example;

import java.util.ArrayList;
import java.util.List;

import br.ufal.ic.wsn.tmcp.simulator.IController;
import br.ufal.ic.wsn.tmcp.simulator.ISinkController;

public class StupidSinkTemperatureController implements ISinkController<Double> {

	private class ChannelController implements IController<Double> {
		public int channel;
		public ChannelController(int channel) {
			this.channel = channel;
		}
		public void run() {}
		public synchronized void receive(Double message) {
			//System.out.printf("Channel: %i received -> %.2lf", channel, message);
		}
	}

	private List<IController<Double>> channels;

	public StupidSinkTemperatureController(int numberOfChannels) {
		this.channels = new ArrayList<>(numberOfChannels);
		
		for (int i = 0; i < numberOfChannels; i++) {
			this.channels.add( new ChannelController(i) );
		}
	}

	@Override
	public void run() {
		while (true) ;
	}

	@Override
	public IController<Double> getChannelController(int channel) {
		return channels.get(channel);
	}

}
