package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.libs.Location;

public class PacketPlayOutEntityPosition extends NetworkPacket implements IPacketPlayOut {
	private int entityId;
	private Location location;
	
	public PacketPlayOutEntityPosition() {};
	
	public PacketPlayOutEntityPosition(int entityId, Location pos) {
		this.entityId = entityId;
		this.location = pos;
	}
	
	public int getEntityId() {
		return entityId;
	}
	
	public Location getLocation() {
		return location;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt32(this.entityId);
		buffer.writeInt16((short) this.location.getX());
		buffer.writeInt16((short) this.location.getY());
		buffer.writeInt16((short) this.location.getRotation());
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.entityId = buffer.readInt32();
		this.location = new Location(null, 
			(int) buffer.readInt16(), // read X position
			(int) buffer.readInt16(), // read Y position
			(int) buffer.readInt16() // read rotation
		);
	}
 }
