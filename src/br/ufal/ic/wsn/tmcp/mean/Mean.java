package br.ufal.ic.wsn.tmcp.mean;

import java.util.ArrayList;
import java.util.List;

/**
 * The mean by which the message is transmitted. Mean
 * is channel hub.
 * 
 * @author João Lucas Nunes e Silva
 */
public class Mean<T> {
	private List<Channel<T>> channels;
	
	/**
	 * Creates a new Mean with <code>numberOfChannels</code> channels
	 * enumerated from 0 to <code>numberOfChannels - 1</code>.
	 * 
	 * @param numberOfChannels number of available channels for use.
	 * @throws Exception thrown if numberOfChannels < 1.
	 */
	public Mean(int numberOfChannels) throws Exception {
		if (numberOfChannels < 1) {
			throw new Exception("The number of channel must be > 0.");
		}
		
		channels = new ArrayList<>(numberOfChannels);
		
		for(int i = 0; i < numberOfChannels; ++i) {
			channels.add(new Channel<T>(i));
		}
	}
	
	public final Channel<T> getChannel(int channelID) {
		return channels.get(channelID);
	}
} // end of the class
