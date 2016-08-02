package br.ufal.ic.wsn.tmcp.simulator;

import java.util.HashMap;
import java.util.Map;

public final class Channels {

	public static <T> Map<Integer, Channel<T>> newChannels(int count) {
		HashMap<Integer, Channel<T>> channels = new HashMap<>();
		
		for(int i = 0; i < count; ++i) {
			channels.put(i, new Channel<T>(i));
		}
		
		return channels;
	}
}
