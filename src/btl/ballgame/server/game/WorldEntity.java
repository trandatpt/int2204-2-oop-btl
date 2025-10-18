package btl.ballgame.server.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import btl.ballgame.protocol.packets.out.PacketPlayOutEntityBBSizeUpdate;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityDestroy;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityMetadata;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityPosition;
import btl.ballgame.shared.libs.AABB;
import btl.ballgame.shared.libs.DataWatcher;
import btl.ballgame.shared.libs.EntityType;
import btl.ballgame.shared.libs.Location;

import static btl.ballgame.server.game.LevelChunk.CHUNK_SHIFT;

/**
 * Represents a dynamic/static entity in the {@link WorldServer}
 * <p>
 * Each WorldEntity knows its location, size, and which chunks it occupies.
 * Supports server-side collision detection, chunk management, and
 * tick-based updates. This is an abstract base class for entities like
 * balls, paddles, bricks, and other static/dynamic objects.
 * <p>
 */
public abstract class WorldEntity {
	/** unique ID for the entity, tracked by the Client */
	private final int id;
	
	/** whether the entity is currently active (spawned) in the world */
	protected boolean active = false;
	
	/** set of chunks this entity currently occupies */
	private Set<LevelChunk> occupiedChunks = new HashSet<>();
	
	/** entity location and size metadata */
	protected WorldServer world;	
	protected int x, y, rot; // NOTE: the location stored here is the center of the entity
	// to compute the upper left corner, use x - width / 2
	protected DataWatcher dataWatcher;
	// dimensions
	protected int width, height;
	
	/** mark this entity as a collider object */
	protected boolean collidable = true;

	/** the current bounding box of this entity, dependent on location and size */
	private AABB boundingBox;
	
	/** the spawned type of this entity, simply NULL if not spawned yet */
	protected EntityType entityType = null;
	
	/**
	 * Constructs a new WorldEntity with a given ID and initial location.
	 *
	 * @param id       Unique entity ID.
	 * @param location Initial world location of the entity (center point).
	 */
	public WorldEntity(int id, Location location) {
		this.id = id;
		this.world = (WorldServer) location.getWorld();
		this.x = location.getX();
		this.y = location.getY();
		this.rot = location.getRotation();
		this.dataWatcher = new DataWatcher();
	}
	
	/** @return The type of this entity, {@code null} if not yet added to a WorldServer */
	public EntityType getType() {
		return entityType;
	}
	
	/** @return Unique ID of the entity. */
	public int getId() {
		return id;
	}
	
	/** @return {@code true} if the entity has been removed or is inactive. */
	public boolean isDead() {
		return !active;
	}
	
	/** @return {@code true} if the entity is active. */
	public boolean isActive() {
		return active;
	}
	
	/** @return The internal datawatcher of the entity. */
	public DataWatcher getWatcher() {
		return dataWatcher;
	}
	
	/** Broadcasts metadata updates to all clients in the world. */
	public void updateMetadata() {
		if (!this.isActive()) return;
		this.getWorld().broadcastPackets(
			new PacketPlayOutEntityMetadata(getId(), this.dataWatcher)
		);
	}
	
	/** Tracks whether a location update should be broadcasted. */
	private boolean shouldUpdate = false;
	
	/** Sends a position update packet if the entity moved/rotated this tick. */
	private void dispatchLocationUpdate() {
		if (!this.shouldUpdate) return;
		this.getWorld().broadcastPackets(
			new PacketPlayOutEntityPosition(getId(), this.getLocation())
		);
		this.shouldUpdate = false;
	}
	
	/**
	 * Gets the current location of the entity.
	 * <p>
	 * Location is immutable; always returns a new Location object.
	 *
	 * @return Location representing the entity's center position and rotation.
	 */
	public Location getLocation() {
		return new Location(world, x, y, rot);
	}
	
	/** @return The world this entity belongs to. */
	public WorldServer getWorld() {
		return this.world;
	}
	
	/**
	 * Performs a collision query within a given axis-aligned bounding box (AABB).
	 * The entity itself is excluded from the result set.
	 *
	 * @param aabb the axis-aligned bounding box to check for collisions inside
	 * @return a list of {@link WorldEntity} instances whose bounding boxes
	 *         intersect with the given AABB
	 */
	protected List<WorldEntity> queryCollisionsInsideAABB(AABB aabb) {
		List<WorldEntity> collided = new ArrayList<>();
		// broadphase check, get all entities around the expanded region (64 units)
		// so we dont need to check EVERY entity in the scene
		Set<WorldEntity> nearby = getWorld().getNearbyEntities(aabb.expand(64));
		nearby.remove(this); // exclude SELF as a potential collider
		
		// skip since there's nothing around it
		if (nearby.size() == 0) {
			return collided;
		}
		
		// narrowphase check, filter out exactly which entity collided with this one
		for (WorldEntity entity : nearby) {
			if (!aabb.intersects(entity.getBoundingBox())) { // this is cheap
				continue;
			}
			collided.add(entity);
		}
		
		return collided;
	}
	
	/**
	 * Queries all entities this entity is currently colliding with.
	 * <p>
	 * Uses a broadphase culling via spatial partitioning (chunks) and a narrowphase
	 * AABB intersection check. Excludes itself from the results.
	 * </p>
	 *
	 * @see {@link WorldEntity#queryCollisionsInsideAABB(AABB)}
	 * @return List of WorldEntities currently colliding with this entity.
	 */
	public List<WorldEntity> queryCollisions() {
		return this.queryCollisionsInsideAABB(getBoundingBox());
	}
	/**
	 * Updates the entity's location.
	 * <p>
	 * Automatically recomputes occupied chunks and 
	 * bounding box if the position has changed.
	 * </p>
	 * Marks the entity for position update broadcast if anything changed.
	 *
	 * @param loc New location.
	 */
	protected void setLocation(Location loc) {
		int oldX = this.x, oldY = this.y, oldRot = this.rot;
		
		this.x = loc.getX();
		this.y = loc.getY();
		this.rot = loc.getRotation();
		
		// the most stupid broadphase check ever
		if (oldX != x || oldY != y) {
			this.computeOccupiedChunks();
		}
		this.computeBoundingBox();
		
		this.shouldUpdate = (oldX != x || oldY != y || oldRot != rot);
	}
	
	/**
	 * Teleports this entity instantly to a new location 
	 * and syncs it to clients.
	 * 
	 * @param location New world location.
	 */
	public void teleport(Location location) {
		this.setLocation(location);
		this.dispatchLocationUpdate();
	}
	
	/**
	 * Recomputes which chunks this entity occupies based on its bounding box.
	 * <p>
	 * Removes the entity from old chunks and joins new ones. Ensures
	 * `getNearbyEntities()` always returns correct results.
	 */
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
	
	/**
	 * Gets the Axis-Aligned Bounding Box of the entity.
	 * <p>
	 * Automatically computed if not already initialized.
	 *
	 * @return Current bounding box.
	 */
	public AABB getBoundingBox() {
		if (this.boundingBox == null) {
			this.computeBoundingBox();
		}
		return this.boundingBox;
	}
	
	/**
	 * Sets the width and height of the entity and updates its bounding box.
	 *
	 * @param width  Width in world units.
	 * @param height Height in world units.
	 */
	public void setBoundingBox(int width, int height) {
		this.width = width;
		this.height = height;
		this.computeBoundingBox();
		
		// broadcast the dimensions change
		this.getWorld().broadcastPackets(new PacketPlayOutEntityBBSizeUpdate(
			getId(), this.width, this.height
		));
	}
	
	/** Recomputes the bounding box centered on the entity's current location. */
	private void computeBoundingBox() {
		this.boundingBox = AABB.fromCenteredPositionWithSize(
			x, y, getWidth(), getHeight()
		);
	}
	
	/**
	 * Joins a specific chunk and marks it as occupied by this entity.
	 *
	 * @param chunk Chunk to join.
	 */
	public void joinChunk(LevelChunk chunk) {
		chunk.entityJoin(this);
		occupiedChunks.add(chunk);
	}
	
	/**
	 * Leaves a specific chunk and removes occupancy.
	 *
	 * @param chunk Chunk to leave.
	 */
	public void leaveChunk(LevelChunk chunk) {
		chunk.entityLeave(this);
		occupiedChunks.remove(chunk);
	}
	
	/**
	 * Checks whether this entity is currently inside a given chunk.
	 *
	 * @param chunk Chunk to check.
	 * @return True if the entity occupies the chunk.
	 */
	public boolean insideChunk(LevelChunk chunk) {
		return occupiedChunks.contains(chunk);
	}
	
	/**
	 * Removes the entity from the world and all chunks.
	 * <p>
	 * Marks it inactive, removes from occupied chunks, and unregisters it from the
	 * world's entity registry.
	 */
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
		
		// notifies clients to despawn the entity
		this.getWorld().broadcastPackets(new PacketPlayOutEntityDestroy(getId()));
	}
	
	/**
	 * Called every server tick to update this entity.
	 * <p>
	 * Invokes subclass logic via {@link #tick()}, then dispatches any pending
	 * position updates to clients.
	 * </p>
	 */
	public final void entityTick() {
		this.tick();
		this.dispatchLocationUpdate();
	}
	
	/** @return Width of the entity. */
	public int getWidth() { return this.width; }
	/** @return Height of the entity. */
	public int getHeight() { return this.height; }
	/** @return true if this entity should be collidable by other entities (just a suggestion). */
	public boolean isCollidable() { return this.collidable; };
	
	/** Called on BEFORE entity spawn (to a specific WorldServer) */
	public void onSpawn() {};
	/** Called every server tick to update entity logic. */
	public abstract void tick();
}
