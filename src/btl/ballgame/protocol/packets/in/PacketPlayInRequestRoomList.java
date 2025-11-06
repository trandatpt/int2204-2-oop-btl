package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayInRequestRoomList extends NetworkPacket implements IPacketPlayIn {
	public PacketPlayInRequestRoomList() {}
	
	@Override
	public void write(PacketByteBuf buffer) {}
	
	@Override
	public void read(PacketByteBuf buffer) {}
}
