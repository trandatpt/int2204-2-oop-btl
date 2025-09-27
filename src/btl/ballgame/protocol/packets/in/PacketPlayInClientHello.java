package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayInClientHello extends NetworkPacket implements IPacketPlayIn {
	private String userName;
	private int protocolVersion;
	
	public PacketPlayInClientHello() {}; /* for packet decoding */
	
	public PacketPlayInClientHello(String username, int protocolVersion) {
		this.userName = username;
		this.protocolVersion = protocolVersion;
	}
	
	public String who() {
		return this.userName;
	}
	
	public int whatVersion() {
		return this.protocolVersion;
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeU8String(this.userName);
		buffer.writeInt32(this.protocolVersion);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.userName = buffer.readU8String();
		this.protocolVersion = buffer.readInt32();
	}
}
