package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayOutRoomJoinError extends NetworkPacket implements IPacketPlayOut {
	private String error;
	public PacketPlayOutRoomJoinError() {}
	
	public PacketPlayOutRoomJoinError(String error) {
		this.error = error;
	}
	
	@Override
	public void write(PacketByteBuf buf) {
		buf.writeU8String(error);
	}

	@Override
	public void read(PacketByteBuf buf) {
		this.error = buf.readU8String();
	}
}
