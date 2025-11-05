package btl.ballgame.shared.libs;

public class Location {
	private IWorld world;
	private int x, y, rot;
	
	public Location(int x, int y, int rot) {
		setWorld(null); setX(x); setY(y); setRotation(rot);
	}
	
	public Location(IWorld world, int x, int y, int rot) {
		setWorld(world); setX(x); setY(y); setRotation(rot);
	}
	
	public Location(IWorld world, int x, int y, Vector2f rot) {
		setWorld(world); setX(x); setY(y); setDirection(rot);
	}
	
	public Location setWorld(IWorld world) {
		this.world = world;
		return this;
	}
	
	public IWorld getWorld() {
		return world;
	}
	
	public int getX() {
		return x;
	}
	
	public Location setX(int x) {
		this.x = x;
		return this;
	}
	
	public int getY() {
		return y;
	}
	
	public Location setY(int y) {
		this.y = y;
		return this;
	}
	
	public int getRotation() {
		return rot;
	}
	
	public Location setDirection(Vector2f vec) {
		if (vec.getX() == 0 && vec.getY() == 0) {
			return this;
		}
		double angleRad = Math.atan2(vec.getY(), vec.getX());
		this.rot = (int) Math.toDegrees(angleRad);
		if (rot < 0) {
			rot += 360;
		}
		return this;
	}
	
	public Location add(Vector2f vec) {
		return add((int)vec.getX(), (int)vec.getY());
	}
	
	public Location add(Location loc) {
		return add(loc.x, loc.y);
	}
	
	public Location add(int x, int y) {
		this.x += x;
		this.y += y;
		return this;
	}
	
	public Location setRotation(int rot) {
		this.rot = Math.max(0, Math.min(360, rot));
		return this;
	}
	
	public Vector2f getDirection() {
		return Vector2f.fromTheta(Math.toRadians(rot));
	}
	
	public Vector2f toVector() {
		return new Vector2f(x, y);
	}
	
	public double distanceSquared(Location loc) {
		double dx = loc.x - this.x;
		double dy = loc.y - this.y;
		return dx * dx + dy * dy;
	}
	
	public double distance(Location loc) {
		return Math.sqrt(distanceSquared(loc));
	}
	
	@Override
	public Location clone() {
		return new Location(world, x, y, rot);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Location comp)) return false;
		return comp.world.equals(this.world) 
			&& comp.x == this.x 
			&& comp.y == this.y 
			&& comp.rot == this.rot
		;
	}
	
	@Override
	public int hashCode() {
		int hash = (x * 92821) ^ (y * 68927) ^ (rot * 31);
		hash ^= world != null ? world.hashCode() : 0;
		return hash;
	}
	
	@Override
	public String toString() {
		return "ArkaLocation{world=" + (world == null ? "NONE" : world.toString()) 
			+ ",x=" + getX() 
			+ ",y=" + getY() 
			+ ",rot=" + getRotation() 
		+ "}";
	}
}
