package br.ufal.ic.wsn.tmcp.simulator;

import java.util.HashMap;
import java.util.Map;

import br.ufal.ic.wsn.tmcp.Util;

public final class Channels {
	
	public final static int    MAX_AMOUNT = 9;
	public final static String COLORS = Util.readFile("resources/channels.colors");

	public static <T> Map<Integer, Channel<T>> newChannels(int count) throws Exception {
		if (count < 1 || count > MAX_AMOUNT) {
			HashMap<Integer, Channel<T>> channels = new HashMap<>();
			
			for(int i = 0; i < count; ++i) {
				channels.put(i, new Channel<T>(i));
			}
			
			return channels;
		} else {
			throw new Exception("'count' must be in (0,16].");
		}
	}
}
