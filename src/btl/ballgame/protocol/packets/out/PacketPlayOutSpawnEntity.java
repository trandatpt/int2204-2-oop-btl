package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.libs.DataWatcher;
import btl.ballgame.shared.libs.Location;

public class PacketPlayOutSpawnEntity extends NetworkPacket implements IPacketPlayOut {
	private byte entityTypeId;
	private int entityId;
	private DataWatcher metadata;
	private Location spawnLocation;
	
	public PacketPlayOutSpawnEntity() {};
	
	public PacketPlayOutSpawnEntity(
		byte entityTypeId, int entityId, 
		DataWatcher dataWatcher, Location location
	) {
		this.entityTypeId = entityTypeId;
		this.entityId = entityId;
		this.metadata = dataWatcher;
		this.spawnLocation = location;
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
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt8(this.entityTypeId);
		buffer.writeInt32(this.entityId);
		this.metadata.write(buffer);
		buffer.writeInt16((short) this.spawnLocation.getX());
		buffer.writeInt16((short) this.spawnLocation.getY());
		buffer.writeInt16((short) this.spawnLocation.getRotation());
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.entityTypeId = buffer.readInt8();
		this.entityId = buffer.readInt32();
		this.metadata = new DataWatcher();
		this.metadata.read(buffer);
		this.spawnLocation = new Location(null, 
			(int) buffer.readInt16(), // read X position
			(int) buffer.readInt16(), // read Y position
			(int) buffer.readInt16() // read rotation
		);
	}
 }
