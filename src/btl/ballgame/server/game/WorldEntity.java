package btl.ballgame.server.game;

import btl.ballgame.shared.libs.Location;

public abstract class WorldEntity {
	private final int id;
	private Location location;

	public WorldEntity(int id, Location location) {
		this.id = id;
		this.location = location;
	}
	
	public int getId() {
		return id;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public WorldEntity setLocation(Location loc) {
		this.location = loc;
		return this;
	}

	public abstract void tick();

	public abstract int getWidth();
	public abstract int getHeight();
}
