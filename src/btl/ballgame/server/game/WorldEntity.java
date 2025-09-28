package btl.ballgame.server.game;

import java.util.HashSet;
import java.util.Set;

import btl.ballgame.shared.libs.AABB;
import btl.ballgame.shared.libs.Location;

public abstract class WorldEntity {
	private final int id;
	protected boolean active = false;
	private Location location;
	private Set<LevelChunk> occupiedChunks = new HashSet<>();
	
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
	
	public AABB getBoundingBox() {
		return AABB.fromCenteredLocWithSize(
			this.location, getWidth(), getHeight()
		);
	}
	
	public void joinChunk(LevelChunk chunk) {
		chunk.entityJoin(this);
		occupiedChunks.add(chunk);
	}
	
	public void leaveChunk(LevelChunk chunk) {
		chunk.entityLeave(this);
		occupiedChunks.remove(chunk);
	}
	
	public boolean insideChunk(LevelChunk chunk) {
		return occupiedChunks.contains(chunk);
	}
	
	public void remove() {
		if (!active) {
			return;
		}
		
		this.active = false;
		new HashSet<>(occupiedChunks).forEach(chunk -> {
			leaveChunk(chunk);
		});
		if (getLocation().getWorld() instanceof WorldServer ws) {
		    ws.removeEntityFromRegistry(this);
		}
	}
	
	public void updateLocation() {
		
	}

	public abstract void tick();
	public abstract int getWidth();
	public abstract int getHeight();
}
