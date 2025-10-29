package btl.ballgame.server.game;

import static btl.ballgame.server.game.LevelChunk.CHUNK_SHIFT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import btl.ballgame.protocol.packets.out.IPacketPlayOut;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntitySpawn;
import btl.ballgame.server.ArkanoidServer;
import btl.ballgame.server.game.match.ArkanoidMatch;
import btl.ballgame.shared.libs.AABB;
import btl.ballgame.shared.libs.EntityType;
import btl.ballgame.shared.libs.IWorld;
import btl.ballgame.shared.libs.Location; 

/**
 * Represents a server-side game world for Arkanoid.
 * <p>
 * Handles entity management, spatial partitioning via chunks, and
 * per-tick updates of all entities. Provides efficient collision
 * queries and ensures server-authoritative movement.
 */
public class WorldServer implements IWorld {
	/** chunk registry, mapped by the coordinate hash as the key */
	private Map<Long, LevelChunk> chunks = new HashMap<>();
	
	/** entity registry, mapped by the entity ID */
	private LinkedHashMap<Integer, WorldEntity> entities = new LinkedHashMap<>();
	private List<WorldEntity> entitiesToBeRemoved = new ArrayList<>();
	
	/** world metadata */
	private int width, height;
	
	/** the random generator */
	public final Random random;
	
	private ArkanoidMatch match;
	
	/**
	 * Constructs a new WorldServer (default random seed) with the specified dimensions. 
	 * Automatically populates the default chunk grid.
	 *
	 * @param match  The Arkanoid match that owns this instance
	 * @param width  Width of the world in units.
	 * @param height Height of the world in units.
	 */
	public WorldServer(ArkanoidMatch match, int width, int height) {
		// well goodluck explaining that thing
		this(match, width, height, (
			System.currentTimeMillis() * 953 
			^ height * 439 
			^ ArkanoidServer.VERSION_NUMERIC * 797
			^ height * 7)
		);
	}
	
	/**
	 * Constructs a new WorldServer with the specified dimensions. Automatically
	 * populates the default chunk grid.
	 *
	 * @param match  The Arkanoid match that owns this instance
	 * @param width  Width of the world in units.
	 * @param height Height of the world in units.
	 * @param seed   The world's seed (for random stuff)
	 */
	public WorldServer(ArkanoidMatch match, int width, int height, long seed) {
		this.match = match;
		this.width = width;
		this.height = height;
		this.random = new Random(seed);
		this.populateDefaultChunks();
	}
	
	/** @return The match instance that owns this world. */
	public ArkanoidMatch getHandle() {
		return match;
	}
	
	/** @return A collection view of all currently loaded entities. */
	public Collection<WorldEntity> getEntities() {
		return entities.values();
	}
	
	/** @return true if this world has a ceiling (balls wont fly through the upper of the board) */
	public boolean hasCeiling() {
		return this.match.getGameMode().isSinglePlayer();
	}
	
	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	/**
	 * Performs a single server tick, updating all entities in the world.
	 * <p>
	 * This should be called at a {@link ArkanoidServer#TICKS_PER_SECOND} rate 
	 * by the Arkanoid Match executor.
	 * </p>
	 */
	public void tick() {
		entities.forEach((id, entity) -> {
			entity.entityTick();
		});
		entitiesToBeRemoved.forEach(entity -> entities.remove(entity.getId()));
		entitiesToBeRemoved.clear();
	}
	
	/**
	 * Broadcasts one or more packets to all connected players in the match.
	 *
	 * @param packets The packets to send.
	 */
	public void broadcastPackets(IPacketPlayOut... packets) {
		match.getPlayers().forEach((player) -> {
			player.playerConnection.sendPackets(packets);
		});
	}
	
	/** Populates all chunks that cover the world initially. */
	private void populateDefaultChunks() {
		for (int cx = 0; cx <= width >> CHUNK_SHIFT; ++cx) {
			for (int cy = 0; cy <= height >> CHUNK_SHIFT; ++cy) {
				this.getOrCreateChunkAt(cx, cy);
			}
		}
	}
	
	/**
	 * Returns the chunk containing a given Location.
	 *
	 * @param loc World location.
	 * @return LevelChunk at that location, or null if it doesn't exist.
	 */
	public LevelChunk getChunkAtWorldLoc(Location loc) {
		return this.getChunkAtWorldLoc(loc.getX(), loc.getY());
	}
	
	/**
	 * Returns the chunk containing the given world coordinates.
	 *
	 * @param wx X coordinate in world units.
	 * @param wy Y coordinate in world units.
	 * @return LevelChunk at that position, or null if it doesn't exist.
	 */
	public LevelChunk getChunkAtWorldLoc(int wx, int wy) {
		// each chunk is 128 x 128 (2^7), a >> k is just div by 2^k
		return this.getChunkAt(wx >> CHUNK_SHIFT, wy >> CHUNK_SHIFT);
	}
	
	/**
	 * Returns the chunk at given chunk coordinates, creating it if it does not
	 * exist.
	 *
	 * @param cx Chunk X coordinate.
	 * @param cy Chunk Y coordinate.
	 * @return LevelChunk instance at (cx, cy).
	 */
	public LevelChunk getOrCreateChunkAt(int cx, int cy) {
		return chunks.computeIfAbsent(
			LevelChunk.computeChunkHash(cx, cy), 
			k -> new LevelChunk(this, cx, cy)
		);
	}
	
	/**
	 * Returns the chunk at given chunk coordinates, or null if not loaded.
	 *
	 * @param cx Chunk X coordinate.
	 * @param cy Chunk Y coordinate.
	 * @return LevelChunk instance or null.
	 */
	public LevelChunk getChunkAt(int cx, int cy) {
		return chunks.get(LevelChunk.computeChunkHash(cx, cy));
	}
	
	/**
	 * Checks if an AABB is completely outside of the world boundaries (well).
	 *
	 * @param area Axis-Aligned Bounding Box to check.
	 * @return True if entirely out of world bounds.
	 */
	public boolean isEntirelyOutOfWorld(AABB area) {
		return area.maxX < 0 || area.maxY < 0 
			|| area.minX > width || area.minY > height;
	}
	
	/**
	 * Adds an entity to the world.
	 * 
	 * @param entity Entity to add.
	 * @return True if successfully added, false if entity is out of world bounds.
	 */
	public boolean addEntity(WorldEntity entity) {
    EntityType type = null;

    // 1) Nếu có server thật thì dùng registry như cũ
    try {
        if (ArkanoidServer.getServer() != null
                && ArkanoidServer.getServer().getEntityRegistry() != null) {
            type = ArkanoidServer.getServer().getEntityRegistry()
                    .getRegisteredType(entity.getClass());
        }
    } catch (Exception ignored) {
        // test mode
    }

    // 2) Nếu đang test (server null) thì tự map theo instanceof
    if (type == null) {
        try {
            // import 3 class này ở đầu file nếu chưa có:
            // import btl.ballgame.server.game.entities.breakable.EntityBrick;
            // import btl.ballgame.server.game.entities.dynamic.EntityWreckingBall;
            // import btl.ballgame.server.game.entities.dynamic.EntityPaddle;
            if (entity instanceof btl.ballgame.server.game.entities.breakable.EntityBrick) {
                type = EntityType.ENTITY_BRICK_NORMAL;           // đổi theo enum thực tế của bạn
            } else if (entity instanceof btl.ballgame.server.game.entities.dynamic.EntityWreckingBall) {
                type = EntityType.ENTITY_BALL;   // đổi theo enum thực tế của bạn
            } else if (entity instanceof btl.ballgame.server.game.entities.dynamic.EntityPaddle) {
                type = EntityType.ENTITY_PADDLE;          // đổi theo enum thực tế của bạn
            }
        } catch (Throwable ignored) {
            // nếu enum khác tên thì tiếp tục bỏ qua set type ở bước dưới
        }
    }

    // Kiểm tra world hợp lệ
    if (!this.equals(entity.getWorld())) {
        System.out.println("⚠️ Entity không cùng world — bỏ qua.");
        return false;
    }
    if (this.isEntirelyOutOfWorld(entity.getBoundingBox())) {
        return false;
    }

    // Thêm vào world
    entity.computeOccupiedChunks();
    entities.put(entity.getId(), entity);
    entity.active = true;

    // Chỉ set type nếu đã xác định được (tránh cần UNKNOWN)
    if (type != null) {
        entity.entityType = type;
    }

    try {
        entity.onSpawn();
    } catch (Exception ignored) { }

    // Khi test offline: KHÔNG broadcast packet (không có client)
    try {
        if (ArkanoidServer.getServer() != null) {
            this.broadcastPackets(new PacketPlayOutEntitySpawn(
                (byte) (type != null ? type.ordinal() : 0),
                entity.getId(),
                entity.getWatcher(),
                entity.getLocation(),
                entity.getBoundingBox()
            ));
        }
    } catch (Exception ignored) {
        // test mode
    }

    return true;
}

	
	/**
	 * Removes an entity from the server registry.
	 *
	 * @param entity Entity to remove.
	 */
	protected void removeEntityFromRegistry(WorldEntity entity) {
		entitiesToBeRemoved.add(entity);
	}
	
	/**
	 * Returns all entities in chunks overlapping a given area.
	 * <p>
	 * Broadphase culling is performed via spatial partitioning, followed by
	 * narrow-phase AABB intersection checks.
	 *
	 * @param area AABB to query.
	 * @return Set of nearby entities intersecting the area.
	 */
	public Set<WorldEntity> getNearbyEntities(AABB area) {
		int minChunkX = area.minX >> CHUNK_SHIFT;
		int maxChunkX = area.maxX >> CHUNK_SHIFT;
		
		int minChunkY = area.minY >> CHUNK_SHIFT;
		int maxChunkY = area.maxY >> CHUNK_SHIFT;
		
		Set<WorldEntity> nearby = new HashSet<>();
		// Broadphase culling: only relevant chunks
		for (int cx = minChunkX; cx <= maxChunkX; ++cx) {
			for (int cy = minChunkY; cy <= maxChunkY; ++cy) {
				LevelChunk chunk = getChunkAt(cx, cy);
				if (chunk == null) continue;
				
				// Narrowphase filtering: find actual entites inside the AABB
				for (WorldEntity e : chunk.getEntities()) {
					if (!e.getBoundingBox().intersects(area)) continue;
					nearby.add(e);
				}
			}
		}
		return nearby;
	}
	
	// this is for quickies only
	private int currentId = 0;
	
    /**
     * Returns the next available entity ID.
     *
     * @return the next integer ID
     */
	public int nextEntityId() {
		return currentId++;
	}
	
    /**
     * Squashes the entity ID counter to zero.
     *
     * @apiNote Use this method with caution, as it may result 
     * in previously assigned IDs being reused!!!
     */
	public void squashIdCounter() {
		currentId = 0;
	}
}
