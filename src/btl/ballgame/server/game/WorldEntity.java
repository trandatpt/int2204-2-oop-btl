package btl.ballgame.server.game;

import java.util.HashSet;
import java.util.Set;

import btl.ballgame.shared.libs.AABB;
import btl.ballgame.shared.libs.Location;

import static btl.ballgame.server.game.LevelChunk.CHUNK_SHIFT;

public abstract class WorldEntity {
	private final int id;
	protected boolean active = false;
	
	private Set<LevelChunk> occupiedChunks = new HashSet<>();
	private int x, y, rot;
	private WorldServer world;	
	
	public WorldEntity(int id, Location location) {
		this.id = id;
		this.world = (WorldServer) location.getWorld();
		this.x = location.getX();
		this.y = location.getY();
		this.rot = location.getRotation();
	}
	
	public int getId() {
		return id;
	}
	
	public Location getLocation() {
		return new Location(world, x, y, rot);
	}
	
	public WorldServer getWorld() {
		return this.world;
	}
	
	public WorldEntity setLocation(Location loc) {
		int oldX = this.x, oldY = this.y, oldRot = this.rot;
		
		this.x = loc.getX();
		this.y = loc.getY();
		this.rot = loc.getRotation();

		if (oldX != x || oldY != y) {
			this.computeOccupiedChunks();
		}
		
		// TODO send location update packet
		
		
		return this;
	}
	
	protected void computeOccupiedChunks() {
		Set<LevelChunk> newOccupiedChunks = new HashSet<>();
		AABB aabb = getBoundingBox();
		
		int minChunkX = aabb.minX >> CHUNK_SHIFT;
		int maxChunkX = aabb.maxX >> CHUNK_SHIFT;
		
		int minChunkY = aabb.minY >> CHUNK_SHIFT;
		int maxChunkY = aabb.maxY >> CHUNK_SHIFT;
		
		for (int cx = minChunkX; cx <= maxChunkX; ++cx) {
			for (int cy = minChunkY; cy <= maxChunkY; ++cy) {
				LevelChunk chunk = world.getChunkAt(cx, cy);
				if (chunk == null) continue;
				newOccupiedChunks.add(chunk);
			}
		}
		
		for (LevelChunk oldChunk : occupiedChunks) {
			if (newOccupiedChunks.contains(oldChunk)) continue;
			oldChunk.entityLeave(this);
		}
		
		this.occupiedChunks = newOccupiedChunks;
		this.occupiedChunks.forEach(this::joinChunk);
	}
	
	public AABB getBoundingBox() {
		return AABB.fromCenteredPositionWithSize(
			x, y, getWidth(), getHeight()
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

	public abstract void tick();
	public abstract int getWidth();
	public abstract int getHeight();
}
