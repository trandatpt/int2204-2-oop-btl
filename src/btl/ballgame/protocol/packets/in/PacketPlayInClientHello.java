package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayInClientHello extends NetworkPacket implements IPacketPlayIn {
	private int protocolVersion;
	
	public PacketPlayInClientHello() {}; /* for packet decoding */
	
	public PacketPlayInClientHello(int protocolVersion) {
		this.protocolVersion = protocolVersion;
	}
	
	public int getProtocolVersion() {
		return protocolVersion;
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt32(protocolVersion);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.protocolVersion = buffer.readInt32();
	}
}
