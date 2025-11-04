package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayInPaddleControl extends NetworkPacket implements IPacketPlayIn {
	public PacketPlayInPaddleControl() {}; /* for packet decoding */
	
	static final byte LEFT_MASK = 0b10, RIGHT_MASK = 0b01, SHOOT_MASK = 0b100;
	
	private byte inputs; 
	public PacketPlayInPaddleControl(boolean left, boolean right, boolean shoot) {
		if (left) inputs |= LEFT_MASK;
		if (right) inputs |= RIGHT_MASK; 
		if (shoot) inputs |= SHOOT_MASK;
	}
	
	public PacketPlayInPaddleControl(boolean shoot) {
		if (shoot) inputs |= SHOOT_MASK;
	}
	
	public boolean isLeft() {
		return (inputs & LEFT_MASK) != 0;
	}
	
	public boolean isRight() {
		return (inputs & RIGHT_MASK) != 0;
	}
	
	public boolean isShoot() {
		return (inputs & SHOOT_MASK) != 0;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt8(this.inputs);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.inputs = buffer.readInt8();
	}
}
