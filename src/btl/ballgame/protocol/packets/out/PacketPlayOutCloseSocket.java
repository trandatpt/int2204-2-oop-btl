package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayOutCloseSocket extends NetworkPacket implements IPacketPlayOut {
	private String reasonString;
	
	public PacketPlayOutCloseSocket() {}
	
	public PacketPlayOutCloseSocket(String reason) {
		this.reasonString = reason;
	}
	
	public String getReason() {
		return this.reasonString;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeU16String(this.reasonString);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.reasonString = buffer.readU16String();
	}
 }
