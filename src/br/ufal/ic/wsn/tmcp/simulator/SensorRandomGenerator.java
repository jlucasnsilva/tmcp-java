package br.ufal.ic.wsn.tmcp.simulator;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.ui.geom.Point2;

public class SensorRandomGenerator extends BaseGenerator {

	private int id;
	private int numberOfSensors;
	private int numberOfChannels;
	private List<Sensor> sensors;
	private int worldSize;
	private double commRadius;
	Random rgen;

	public SensorRandomGenerator(int numberOfSensors, int numberOfChannels, int worldSize, double commRadius) {
		this.numberOfChannels = numberOfChannels;
		this.numberOfSensors  = numberOfSensors;
		this.sensors = new ArrayList<>();
		this.id = 0;
		this.commRadius = commRadius;
		this.rgen = new Random();
	}

	private class Sensor {
		public final String id;
		public final double x;
		public final double y;
		public int maxConn;
		public Sensor(String id, double x, double y, int maxConn) {
			this.id = id;
			this.x  = x;
			this.y  = y;
			this.maxConn = maxConn;
		}
	}

	private Sensor addSensor() {
		return addSensor("" + this.id, Math.random(), Math.random());
	}
	
	private Sensor addSensor(String id, double x, double y) {
		Sensor ns = new Sensor(id, x, y, rgen.nextInt(5 + numberOfChannels));
		addNode(id, x, y);
		sensors.add( ns );
		sendNodeAttributeAdded(sourceId, "" + this.id, "x", x);
		sendNodeAttributeAdded(sourceId, "" + this.id, "y", y);
		
		this.id++;
		return ns;
	}
	
	@Override
	public void begin() {
		addSensor("" + this.id, 0, 0);
		
		this.id++;
	}

	@Override
	public boolean nextEvents() {
		for (int count = 1; count < numberOfSensors; count++) {
			Sensor newSensor   = addSensor();
			
			for (int i = 0; i < (sensors.size() - 1) && newSensor.maxConn > 0; i++) {
				Sensor s = sensors.get(i);
				boolean shouldConnect = true;
				
				if (sensors.size() > (numberOfSensors / 4)) {
					shouldConnect = rgen.nextBoolean();
				}
				
				if (shouldConnect && withinRange(newSensor, s) && s.maxConn > 0) {
					addEdge(s.id + "-" + newSensor.id, s.id, newSensor.id);
					s.maxConn--;
					newSensor.maxConn--;
				}
			}
		}
		
		return false;
	}
	
	private boolean withinRange(Sensor a, Sensor b) {
		double w = worldSize;
		return Point2D.distance(a.x * w, a.y * w, b.x * w, b.y * w) <= commRadius;
	}

}
