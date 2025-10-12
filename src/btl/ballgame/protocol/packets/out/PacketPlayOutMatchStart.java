package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayOutMatchStart extends NetworkPacket implements IPacketPlayOut {
	private int worldHeight, worldWidth;
	
	public PacketPlayOutMatchStart() {}
	
	public PacketPlayOutMatchStart(String reason) {
		this.reasonString = reason;
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
