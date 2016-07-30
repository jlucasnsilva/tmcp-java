package br.ufal.ic.wsn.tmcp.simulator;

public class Sensors {

	/**
	 * The interference radius of a sensor transmission is equal to:
	 * <code>	interferenceRadiusCoefficient * communicationRadius</code>
	 */
	private static double interferenceRadiusCoefficient = 1.5;

	public static void setInterferenceRadiusCoefficient(double coefficient) throws Exception {
		if (coefficient >= 1.0) {
			throw new Exception("The interference radius coefficient must be >= 1.0");
		}

		interferenceRadiusCoefficient = coefficient;
	}

	public static double getInterferenceRadiusCoefficient() {
		return interferenceRadiusCoefficient;
	}

	/**
	 * A simple unique id generator.
	 */
	static long nextID = 0;

	/**
	 * A simple unique id generator.
	 */
	static long getNextID() {
		return nextID++;
	}
}
