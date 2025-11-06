package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayInJoinRoom extends NetworkPacket implements IPacketPlayIn {
	private String roomId;
	public PacketPlayInJoinRoom() {}

	// nullable
	public PacketPlayInJoinRoom(String secretCode) {
		this.roomId = secretCode;
	}

	public String getRoomID() {
		return roomId;
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeU8String(this.roomId);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.roomId = buffer.readU8String();
	}
}
