package btl.ballgame.client.net.systems;

import btl.ballgame.protocol.packets.out.PacketPlayOutEntityBBSizeUpdate;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityMetadata;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityPosition;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntitySpawn;
import btl.ballgame.shared.libs.AABB;
import btl.ballgame.shared.libs.DataWatcher;
import btl.ballgame.shared.libs.Location;

import javafx.scene.canvas.GraphicsContext;

/**
 * Represents a client-side entity synchronized from the server.
 * 
 * This class defines the base structure and update logic for any visible or
 * interactive object in the client world that is managed by the networking
 * layer
 */
public abstract class CSEntity implements IRenderInfo {
	/** The unique server-assigned entity ID. */
	private int id;
	/** Contains metadata values for this entity (e.g., health, status, etc). */
	private DataWatcher entityDataWatcher;
	/** The latest location of this entity as sent by the server. */
	private Location serverLocation;
	/** The entity’s AABB. */
	private AABB boundingBox;
	/** The dimensions of the entity */
	private int width, height;
	
	/**
	 * Recalculates the bounding box based on 
	 * the entity's current position and size.
	 */
	private void updateBoundingBox() {
		this.boundingBox = AABB.fromCenteredLocWithSize(
			this.serverLocation, 
			this.width, this.height
		);
	}

	/**
	 * Updates the entity’s metadata from a server packet.
	 * 
	 * @param packet The packet containing updated metadata information.
	 */
	public void updateDataWatcherFrom(PacketPlayOutEntityMetadata packet) {
		this.onBeforeWatcherUpdate();
		this.entityDataWatcher = packet.getWatcher();
		this.onAfterWatcherUpdate();
	}
	
	/**
	 * Updates the entity’s bounding box size from a server packet.
	 * 
	 * @param packet The packet containing the new entity width and height.
	 */
	public void updateBBSizeFrom(PacketPlayOutEntityBBSizeUpdate packet) {
		this.onBeforeBBSizeUpdate();
		this.width = packet.getEntityWidth();
		this.height = packet.getEntityHeight();
		this.updateBoundingBox();
		this.onAfterBBSizeUpdate();
	}
	
	/**
	 * Updates the entity’s position from a server packet.
	 * 
	 * @param packet The packet containing the new location data.
	 */
	public void updatePositionFrom(PacketPlayOutEntityPosition packet) {
		Location newLocation = packet.getLocation();
		this.onBeforeLocationUpdate();
		this.serverLocation
			.setX(newLocation.getX())
			.setY(newLocation.getY())
			.setRotation(newLocation.getRotation())
		;
		this.updateBoundingBox();
		this.onAfterLocationUpdate();
	}
	
	/**
	 * Copies initial spawn properties from a spawn packet.
	 * 
	 * @param packet The packet containing spawn data for this entity.
	 */
	public void copyPropertiesFrom(PacketPlayOutEntitySpawn packet) {
		this.id = packet.getEntityId();
		this.entityDataWatcher = packet.getDataWatcher();
		this.serverLocation = packet.getSpawnLocation();
		this.width = packet.getEntityWidth();
		this.height = packet.getEntityHeight();
		this.updateBoundingBox();
	}

	/**
	 * Binds this entity to a specific world instance.
	 * 
	 * @param world The world this entity exists in.
	 */
	public void bindWorld(CSWorld world) {
		this.serverLocation.setWorld(world);
	}
	
	public int getId() {
		return id;
	}
	
	public DataWatcher getWatcher() {
		return entityDataWatcher;
	}
	
	public Location getServerLocation() {
		return serverLocation.clone();
	}
	
	public Location getMutableServerLocation() {
		return serverLocation;
	}
	
	public AABB getBoundingBox() {
		return boundingBox;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	// --- IRENDER: supply the renderer with easy-to-access essential data
	// for rendering ----
	@Override
	public int getRenderX() {
		return this.serverLocation.getX() - (this.getWidth() >> 1);
	}
	
	@Override
	public int getRenderY() {
		return this.serverLocation.getY() - (this.getWidth() >> 1);
	}
	
	@Override
	public int getRenderRotation() {
		return this.serverLocation.getRotation();
	}
	
	@Override
	public int getRenderWidth() {
		return this.getWidth();
	}
	
	@Override
	public int getRenderHeight() {
		return this.getHeight();
	}
	
	// --- EVENTS CALL ---
	/** Called before the entity's DataWatcher is updated. */
	public void onBeforeWatcherUpdate() {};
	/** Called after the entity's DataWatcher has been updated. */
	public void onAfterWatcherUpdate() {};

	/** Called before the entity's location is updated. */
	public void onBeforeLocationUpdate() {};
	/** Called after the entity's location has been updated. */
	public void onAfterLocationUpdate() {};

	/** Called before the entity's bounding box size is updated. */
	public void onBeforeBBSizeUpdate() {};
	/** Called after the entity's bounding box size has been updated. */
	public void onAfterBBSizeUpdate() {};

	/** Called when the entity is first spawned into the world. */
	public void onEntitySpawn() {};
	
	/** Called when the entity is removed or despawned from the world. */
	public void onEntityDespawn() {};
	
	// --- HIER ---
	/**
	 * Renders this entity onto the given {@link GraphicsContext}, runs
	 * as fast as the hardware allows, do not rely on this for timing
	 *
	 * @param gc the {@link GraphicsContext} to draw on; never NULL
	 */
	public abstract void render(GraphicsContext cv);
}
