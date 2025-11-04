package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.libs.DataWatcher;

public class PacketPlayOutEntityMetadata extends NetworkPacket implements IPacketPlayOut {
	private int entityId;
	private DataWatcher metadata;
	
	public PacketPlayOutEntityMetadata() {};
	
	public PacketPlayOutEntityMetadata(int entityId, DataWatcher metadata) {
		this.entityId = entityId;
		this.metadata = metadata;
	}
	
	public int getEntityId() {
		return entityId;
	}
	
	public DataWatcher getWatcher() {
		return metadata;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeVarUInt(this.entityId);
		this.metadata.write(buffer);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.entityId = buffer.readVarUInt();
		this.metadata = new DataWatcher();
		this.metadata.read(buffer);
	}
}
