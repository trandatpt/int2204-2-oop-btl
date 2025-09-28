package btl.ballgame.server.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import btl.ballgame.shared.libs.AABB;
import btl.ballgame.shared.libs.IWorld;
import btl.ballgame.shared.libs.Location;

import static btl.ballgame.server.game.LevelChunk.CHUNK_SHIFT; 

public class WorldServer implements IWorld {
	
	private Map<Long, LevelChunk> chunks = new HashMap<>();
	private Map<Integer, WorldEntity> entities = new HashMap<>();
	private int width, height;
	
	public WorldServer(int width, int height) {
		this.width = width;
		this.height = height;
		this.populateDefaultChunks();
	}
	
	private void populateDefaultChunks() {
		for (int cx = 0; cx < width >> CHUNK_SHIFT; ++cx) {
			for (int cy = 0; cy < height >> CHUNK_SHIFT; ++cy) {
				this.getOrCreateChunkAt(cx, cy);
			}
		}
	}
	
	public LevelChunk getChunkAtWorldLoc(Location loc) {
		return this.getChunkAtWorldLoc(loc.getX(), loc.getY());
	}
	
	public LevelChunk getChunkAtWorldLoc(int wx, int wy) {
		// each chunk is 128 x 128 (2^7)
		return this.getChunkAt(wx >> CHUNK_SHIFT, wy >> CHUNK_SHIFT);
	}
	
	public LevelChunk getOrCreateChunkAt(int cx, int cy) {
		return chunks.computeIfAbsent(
			LevelChunk.computeChunkHash(cx, cy), 
			k -> new LevelChunk(this, cx, cy)
		);
	}
	
	public LevelChunk getChunkAt(int cx, int cy) {
		return chunks.get(LevelChunk.computeChunkHash(cx, cy));
	}
	
	public boolean isEntirelyOutOfWorld(AABB area) {
		return area.maxX < 0 || area.maxY < 0 
			|| area.minX > width || area.minY > height;
	}
	
	public boolean addEntity(WorldEntity entity) {
		if (isEntirelyOutOfWorld(entity.getBoundingBox())) {
			return false;
		}
		
		int minChunkX = entity.getBoundingBox().minX >> CHUNK_SHIFT;
		int maxChunkX = entity.getBoundingBox().maxX >> CHUNK_SHIFT;
		
		int minChunkY = entity.getBoundingBox().minY >> CHUNK_SHIFT;
		int maxChunkY = entity.getBoundingBox().maxY >> CHUNK_SHIFT;
		
		for (int cx = minChunkX; cx < maxChunkX; ++cx) {
			for (int cy = minChunkY; cy < maxChunkY; ++cy) {
				LevelChunk chunk = getChunkAt(cx, cy);
				if (chunk == null) continue;
				entity.joinChunk(chunk);
			}
		}
		entities.put(entity.getId(), entity);
		entity.getLocation().setWorld(this);
		return true;
	}
	
	protected void removeEntityFromRegistry(WorldEntity entity) {
		entities.remove(entity.getId());
	}
	
	public Set<WorldEntity> getNearbyEntities(AABB area) {
		int minChunkX = area.minX >> CHUNK_SHIFT;
		int maxChunkX = area.maxX >> CHUNK_SHIFT;
		
		int minChunkY = area.minY >> CHUNK_SHIFT;
		int maxChunkY = area.maxY >> CHUNK_SHIFT;
		
		Set<WorldEntity> nearby = new HashSet<>();
		for (int cx = minChunkX; cx < maxChunkX; ++cx) {
			for (int cy = minChunkY; cy < maxChunkY; ++cy) {
				LevelChunk chunk = getChunkAt(cx, cy);
				if (chunk == null) continue;
				
				for (WorldEntity e : chunk.getEntities()) {
					if (!e.getBoundingBox().intersects(area)) continue;
					nearby.add(e);
				}
			}
		}
		return nearby;
	}
}
