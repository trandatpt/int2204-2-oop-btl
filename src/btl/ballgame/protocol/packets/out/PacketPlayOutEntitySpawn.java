package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.libs.AABB;
import btl.ballgame.shared.libs.DataWatcher;
import btl.ballgame.shared.libs.Location;

public class PacketPlayOutEntitySpawn extends NetworkPacket implements IPacketPlayOut {
	private byte entityTypeId;
	private int entityId;
	private DataWatcher metadata;
	private Location spawnLocation;
	private int entityWidth, entityHeight;
	
	public PacketPlayOutEntitySpawn() {};
	
	public PacketPlayOutEntitySpawn(
		byte entityTypeId, int entityId, 
		DataWatcher dataWatcher, 
		Location location, AABB boundingBox
	) {
		this.entityTypeId = entityTypeId;
		this.entityId = entityId;
		this.metadata = dataWatcher;
		this.spawnLocation = location;
		this.entityWidth = (int) boundingBox.getWidth();
		this.entityHeight = (int) boundingBox.getHeight();
	}
	
	public byte getEntityTypeId() {
		return entityTypeId;
	}
	
	public int getEntityId() {
		return entityId;
	}
	
	public DataWatcher getDataWatcher() {
		return metadata;
	}
	
	public Location getSpawnLocation() {
		return spawnLocation;
	}
	
	public int getEntityWidth() {
		return entityWidth;
	}
	
	public int getEntityHeight() {
		return entityHeight;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt8(this.entityTypeId);
		buffer.writeVarUInt(this.entityId);
		this.metadata.write(buffer);
		// write spawn location
		buffer.writeInt16((short) this.spawnLocation.getX());
		buffer.writeInt16((short) this.spawnLocation.getY());
		buffer.writeInt16((short) this.spawnLocation.getRotation());
		// write entity W/H
		buffer.writeInt16((short) this.entityWidth);
		buffer.writeInt16((short) this.entityHeight);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.entityTypeId = buffer.readInt8();
		this.entityId = buffer.readVarUInt();
		this.metadata = new DataWatcher();
		this.metadata.read(buffer);
		this.spawnLocation = new Location(null, 
			(int) buffer.readInt16(), // read X position
			(int) buffer.readInt16(), // read Y position
			(int) buffer.readInt16() // read rotation
		);
		this.entityWidth = buffer.readInt16();
		this.entityHeight = buffer.readInt16();
	}
 }
