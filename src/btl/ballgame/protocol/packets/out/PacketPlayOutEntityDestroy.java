package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayOutEntityDestroy extends NetworkPacket implements IPacketPlayOut {
	private int entityId;
	
	public PacketPlayOutEntityDestroy() {};
	
	public PacketPlayOutEntityDestroy(int entityId) {
		this.entityId = entityId;
	}
	
	public int getEntityId() {
		return entityId;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt32(this.entityId);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.entityId = buffer.readInt32();
	}
 }
