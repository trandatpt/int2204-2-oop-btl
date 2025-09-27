package btl.ballgame.protocol.packets;

import btl.ballgame.protocol.PacketByteBuf;

public abstract class NetworkPacket {
	public abstract void write(PacketByteBuf buffer);
	public abstract void read(PacketByteBuf buffer);
}
