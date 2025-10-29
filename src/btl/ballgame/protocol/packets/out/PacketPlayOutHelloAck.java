package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayOutHelloAck extends NetworkPacket implements IPacketPlayOut {
	public PacketPlayOutHelloAck() {}
	
	private String rejectReason;
	
	public PacketPlayOutHelloAck(String rejectReason) {
		this.rejectReason = rejectReason;
	}
	
	public boolean isSuccessful() {
		return rejectReason == null;
	}
	
	public String getRejectReason() {
		return rejectReason;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeU8String(rejectReason);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.rejectReason = buffer.readU8String();
	}
 }
