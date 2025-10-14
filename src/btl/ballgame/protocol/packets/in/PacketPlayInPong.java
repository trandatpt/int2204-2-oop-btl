package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayInPong extends NetworkPacket implements IPacketPlayIn {
	public PacketPlayInPong() {}
	
	@Override
	public void write(PacketByteBuf buffer) {}

	@Override
	public void read(PacketByteBuf buffer) {}
 }
