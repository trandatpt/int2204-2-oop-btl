package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayInRoomSetReady extends NetworkPacket implements IPacketPlayIn {
	private boolean ready;
	public PacketPlayInRoomSetReady() {}

	public PacketPlayInRoomSetReady(boolean ready) {
		this.ready = ready;
	}

	public boolean isReady() {
		return ready;
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeBool(ready);
	}
	
	@Override
	public void read(PacketByteBuf buffer) {
		this.ready = buffer.readBool();
	}
}
