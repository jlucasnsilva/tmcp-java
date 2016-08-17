package br.ufal.ic.wsn.tmcp.example;

import br.ufal.ic.wsn.tmcp.simulator.EGraphType;
import br.ufal.ic.wsn.tmcp.simulator.TmcpSimulation;

public class Main {

	public static void main(String[] args) throws Exception {
		TmcpSimulation<Double> s;
		TmcpSimulation.Args a = new TmcpSimulation.Args();
		
		a.name = "Simulation 1";
		a.graphType   = EGraphType.CROSS_GRID;
		a.worldSize   = 200;
		a.nOfChannels = 3;
		a.nOfSensors  = 250;
		a.commRadius  = 15.0;
		a.intCoefficient = 1.5;
		a.cycles = 100;
		a.sleep  = 0;//150;
		
		s = new TmcpSimulation<>(a, new StupidTemperatureControllerFactory());

		s.init();
		s.execute();
	}

}
