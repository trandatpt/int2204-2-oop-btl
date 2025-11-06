package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

// player requests to leave the current room/current game
public class PacketPlayInLeaveContext extends NetworkPacket implements IPacketPlayIn {
	public PacketPlayInLeaveContext() {}

	@Override
	public void write(PacketByteBuf buffer) {}

	@Override
	public void read(PacketByteBuf buffer) {}
}
