package br.ufal.ic.wsn.tmcp;

import br.ufal.ic.wsn.tmcp.simulator.EGraphType;
import br.ufal.ic.wsn.tmcp.simulator.TmcpSimulation;

public class Main {

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 1; i++) {
			TmcpSimulation<Object> s = new TmcpSimulation<>("Simulation 1", EGraphType.SMALL_WORLD, 200, 3, 250, 15, 1.5, 100);
			
			s.build();
			s.algorithm();
		}
	}

}
