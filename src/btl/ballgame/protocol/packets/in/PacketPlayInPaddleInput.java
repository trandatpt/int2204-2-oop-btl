package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayInPaddleInput extends NetworkPacket implements IPacketPlayIn {
	public PacketPlayInPaddleInput() {}; /* for packet decoding */
	
	private boolean left, right; 
	public PacketPlayInPaddleInput(boolean left, boolean right) {
		this.left = left;
		this.right = right;
	}
	
	public boolean isLeft() {
		return left;
	}
	
	public boolean isRight() {
		return right;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeBool(this.left);
		buffer.writeBool(this.right);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.left = buffer.readBool();
		this.right = buffer.readBool();
	}
}
