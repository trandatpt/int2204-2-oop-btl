package btl.ballgame.server.game;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single chunk in the game world.
 * <p>
 * Chunks are used to spatially partition the world for efficient entity
 * management and collision detection. Each chunk tracks the entities currently
 * occupying it.
 * <p>
 * Chunk size is fixed (CHUNK_SIZE) and chunk coordinates are used for indexing
 * and hashing.
 */
public class LevelChunk {
	/** size of a single chunk in world units (128x128) */
	public static final int CHUNK_SIZE = 128;
	public static final int CHUNK_SHIFT = (int) (Math.log(CHUNK_SIZE) / Math.log(2));
	
	/** chunk metadata */
	private int x, y;
	private WorldServer world;
	
	/** set of entities currently in this chunk */
	private Set<WorldEntity> entities = new HashSet<>();
	
	/**
	 * Constructs a new LevelChunk at the specified chunk coordinates.
	 *
	 * @param level Reference to the world this chunk belongs to.
	 * @param cx    Chunk X coordinate.
	 * @param cy    Chunk Y coordinate.
	 */
	public LevelChunk(WorldServer level, int cx, int cy) {
		this.world = level;
		this.x = cx;
		this.y = cy;
	}
	
	/** @return The world this chunk belongs to. */
	public WorldServer getWorld() {
		return world;
	}
	
	/**
	 * @return The collection of entities in this chunk
	 */
	public Collection<WorldEntity> getEntities() {
		return entities;
	}
	
	/**
	 * Adds an entity to this chunk.
	 * @param worldEntity Entity to add.
	 */
	public void entityJoin(WorldEntity worldEntity) {
		entities.add(worldEntity);
	}
	

	/**
	 * Removes an entity from this chunk.
	 * @param worldEntity Entity to remove.
	 */
	public void entityLeave(WorldEntity worldEntity) {
		entities.remove(worldEntity);
	}
	
	/**
	 * Computes a unique 64-bit hash for a chunk given its coordinates.
	 *
	 * @param chunkX x coordinate.
	 * @param chunkY y coordinate.
	 * @return 64-bit (ulong) chunk hash.
	 */
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
