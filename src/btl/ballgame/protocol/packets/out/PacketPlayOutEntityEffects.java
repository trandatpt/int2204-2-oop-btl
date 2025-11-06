package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayOutEntityEffects extends NetworkPacket implements IPacketPlayOut {
	static final int IS_DAMAGED_MASK = 0b01;
	
	private int entityId;
	private byte flags;
	
	public PacketPlayOutEntityEffects() {};
	
	public PacketPlayOutEntityEffects(int entityId, boolean isDamaged) {
		this.entityId = entityId;
		if (isDamaged) flags |= IS_DAMAGED_MASK;
	}
	
	public int getEntityId() {
		return entityId;
	}
	
	public boolean isDamaged() {
		return (flags & IS_DAMAGED_MASK) != 0;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeVarUInt(this.entityId);
		buffer.writeInt8(flags);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.entityId = buffer.readVarUInt();
		this.flags = buffer.readInt8();
	}
}
