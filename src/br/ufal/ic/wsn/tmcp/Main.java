package br.ufal.ic.wsn.tmcp;

import br.ufal.ic.wsn.tmcp.simulator.Channels;
import br.ufal.ic.wsn.tmcp.simulator.TmcpSimulation;

public class Main {

	public static void main(String[] args) throws Exception {
		TmcpSimulation<Object> s = new TmcpSimulation<>("Simulation 1", 200, 4, 250, 15, 1.5);
		
		s.build();
		s.getGraph().setAttribute("ui.stylesheet", Channels.STYLES);
		s.getGraph().display();
		
//		n.setAttribute("ui.label", nname);
//		e.setAttribute("ui.label", "chan=" + k);
//		n.setAttribute("ui.class", "channel_" + k);
//		e.setAttribute("ui.class", "channel_" + k);
	}

}
