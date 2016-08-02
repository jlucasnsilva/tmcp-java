package br.ufal.ic.wsn.tmcp.simulator;

import java.util.HashSet;
import java.util.Set;

public class Sensors {

	/**
	 * The interference radius of a sensor transmission is equal to:
	 * <code>	interferenceRadiusCoefficient * communicationRadius</code>
	 */
	private static double interferenceRadiusCoefficient = 1.5;

	public static void setInterferenceRadiusCoefficient(double coefficient) throws Exception {
		if (coefficient <= 1.0) {
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

	public static <T> Set<Sensor<T>> newSensors(int worldSize, int numberOfSensors, double radius) {
		HashSet<Sensor<T>> s = new HashSet<>();
		
		try {
			double inc = ((double) worldSize - 1.0) / numberOfSensors;

			for (double x = 1.0; x < worldSize; x += inc) {
				for (double y = 1.0; y < worldSize; y += inc) {
					s.add( new Sensor<T>(getNextID(), x, y, radius) );
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			s = null;
		}
		
		return s;
	}
}
