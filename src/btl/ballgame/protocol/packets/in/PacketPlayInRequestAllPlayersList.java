package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayInRequestAllPlayersList extends NetworkPacket implements IPacketPlayIn {
	public PacketPlayInRequestAllPlayersList() {}
	
	@Override
	public void write(PacketByteBuf buffer) {}
	
	@Override
	public void read(PacketByteBuf buffer) {}
}
