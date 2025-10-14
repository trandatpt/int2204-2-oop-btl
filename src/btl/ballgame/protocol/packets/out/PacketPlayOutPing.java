package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayOutPing extends NetworkPacket implements IPacketPlayOut {
	public PacketPlayOutPing() {}
	
	@Override
	public void write(PacketByteBuf buffer) {}

	@Override
	public void read(PacketByteBuf buffer) {}
 }
