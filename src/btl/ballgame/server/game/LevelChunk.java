package btl.ballgame.server.game;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LevelChunk {
	public static final int CHUNK_SIZE = 128;
	public static final int CHUNK_SHIFT = (int) (Math.log(CHUNK_SIZE) / Math.log(2));
	
	private int x, y;
	private WorldServer world;
	private Set<WorldEntity> entities = new HashSet<>();
	
	public LevelChunk(WorldServer level, int cx, int cy) {
		this.world = level;
		this.x = cx;
		this.y = cy;
	}
	
	public Collection<WorldEntity> getEntities() {
		return entities;
	}
	
	public void entityJoin(WorldEntity worldEntity) {
		entities.add(worldEntity);
	}
	
	public void entityLeave(WorldEntity worldEntity) {
		entities.remove(worldEntity);
	}
	
	public static long computeChunkHash(int chunkX, int chunkY) {
		return (((long) chunkX) & 0xFFFFFFFFL) << 32 | (chunkY & 0xFFFFFFFFL);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof LevelChunk otherChunk)) {
			return false;
		}
		return otherChunk.x == this.x && otherChunk.y == this.y;
	}
}
