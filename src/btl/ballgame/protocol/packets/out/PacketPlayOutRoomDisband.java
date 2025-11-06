package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayOutRoomDisband extends NetworkPacket implements IPacketPlayOut {
	public PacketPlayOutRoomDisband() {}

	@Override
	public void write(PacketByteBuf buf) {}

	@Override
	public void read(PacketByteBuf buf) {}
}
