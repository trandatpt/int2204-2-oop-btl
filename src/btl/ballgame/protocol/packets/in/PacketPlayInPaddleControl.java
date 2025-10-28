package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayInPaddleControl extends NetworkPacket implements IPacketPlayIn {
	public PacketPlayInPaddleControl() {}; /* for packet decoding */
	
	private int clientX; 
	public PacketPlayInPaddleControl(int clientX) {
		this.clientX = clientX;
	}
	
	public int getClientX() {
		return clientX;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt32(this.clientX);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.clientX = buffer.readInt32();
	}
}
