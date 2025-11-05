package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayOutEntityBBSizeUpdate extends NetworkPacket implements IPacketPlayOut {
	private int entityId;
	private int entityWidth, entityHeight;
	
	public PacketPlayOutEntityBBSizeUpdate() {};
	
	public PacketPlayOutEntityBBSizeUpdate(int entityId, int width, int height) {
		this.entityId = entityId;
		this.entityWidth = width;
		this.entityHeight = height;
	}
	
	public int getEntityId() {
		return entityId;
	}
	
	public int getEntityWidth() {
		return entityWidth;
	}
	
	public int getEntityHeight() {
		return entityHeight;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt16((short) this.entityId);
		buffer.writeInt16((short) this.entityWidth);
		buffer.writeInt16((short) this.entityHeight);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.entityId = buffer.readInt16();
		this.entityWidth = buffer.readInt16();
		this.entityHeight = buffer.readInt16();
	}
 }
