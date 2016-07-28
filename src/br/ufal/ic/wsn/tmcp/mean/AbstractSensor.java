package br.ufal.ic.wsn.tmcp.mean;

public abstract class AbstractSensor<T> {
	private static long nextID = 0;
	
	public static long getNextID() {
		return nextID++;
	}
	
	private final long id;
	private final int  x;
	private final int  y;
	private final int  radius;
	
	public AbstractSensor(int x, int y, int radius) throws Exception {
		this.id = getNextID();
		this.x  = x;
		this.y  = y;
		this.radius = radius;
		
		if (radius == 0) {
			throw new Exception("The radius must be != 0.");
		}
	}
	
	abstract void receive(long senderID, T message);
	
	public long getID() {
		return id;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getRadius() {
		return radius;
	}
}
