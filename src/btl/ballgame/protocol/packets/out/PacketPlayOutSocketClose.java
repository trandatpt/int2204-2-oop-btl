package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayOutSocketClose extends NetworkPacket implements IPacketPlayOut {
	private String reasonString;
	
	public PacketPlayOutSocketClose() {}
	
	public PacketPlayOutSocketClose(String reason) {
		this.reasonString = reason;
	}
	
	public String getReason() {
		return this.reasonString;
	}

	@Override
	public void write(PacketByteBuf buffer) {
		
	}

	@Override
	public void read(PacketByteBuf buffer) {
		
	}
 }
