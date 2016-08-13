package br.ufal.ic.wsn.tmcp.simulator;

import java.util.HashMap;
import java.util.Map;

import br.ufal.ic.wsn.tmcp.Util;

public final class Channels {
	
	/**
	 * The maximum number of channels.
	 */
	public final static int MAX = 9;
	
	/**
	 * Styles for the GraphStream UI.
	 */
	public final static String STYLES = Util.readFile("resources/channels.colors");

	/**
	 * Creates {@code count} channels. {@code count} must be
	 * {@code 0 < count <= Channels.MAX}.
	 * 
	 * @param  count the number of channels to be created
	 * @return A map<chanid, chan>.
	 * @throws Exception thrown if {@code 0 > count > Channels.MAX}.
	 */
	public static <T> Map<Integer, Channel<T>> newChannels(int count) throws Exception {
		if (count < 1 || count > MAX) {
			throw new Exception("'count' must be in (0," + MAX + "].");
		}
		
		HashMap<Integer, Channel<T>> channels = new HashMap<>();
		
		for(int i = 0; i < count; ++i) {
			channels.put(i, new Channel<T>(i));
		}
		
		return channels;
	}
}
