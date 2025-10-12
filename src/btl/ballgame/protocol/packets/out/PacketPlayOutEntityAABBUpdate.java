package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.libs.AABB;

public class PacketPlayOutEntityAABBUpdate extends NetworkPacket implements IPacketPlayOut {
	private int entityId;
	private AABB boundingBox;
	
	public PacketPlayOutEntityAABBUpdate() {};
	
	public PacketPlayOutEntityAABBUpdate(int entityId, AABB boundingBox) {
		this.entityId = entityId;
		this.boundingBox = boundingBox;
	}
	
	public int getEntityId() {
		return entityId;
	}
	
	public AABB getBoundingBox() {
		return boundingBox;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt32(this.entityId);
		// write AABB bounds
		buffer.writeInt16((short) this.boundingBox.minX);
		buffer.writeInt16((short) this.boundingBox.minY);
		buffer.writeInt16((short) this.boundingBox.maxX);
		buffer.writeInt16((short) this.boundingBox.maxY);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.entityId = buffer.readInt32();
		this.boundingBox = new AABB(
			buffer.readInt16(), // read minX
			buffer.readInt16(), // read minY
			buffer.readInt16(), // read maxX
			buffer.readInt16() // read maxY
		);
	}
 }
