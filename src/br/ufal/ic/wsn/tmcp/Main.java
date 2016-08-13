package br.ufal.ic.wsn.tmcp;

import br.ufal.ic.wsn.tmcp.simulator.EGraphType;
import br.ufal.ic.wsn.tmcp.simulator.TmcpSimulation;

public class Main {

	public static void main(String[] args) throws Exception {
		TmcpSimulation<Object> s = new TmcpSimulation<>("Simulation 1", EGraphType.RANDOM_EUCLIDEAN, 200, 4, 250, 15, 1.5, 100);
		
		s.build();
		s.algorithm();
	}

}
