package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayOutClientFlags extends NetworkPacket implements IPacketPlayOut {
	private static final int DISABLE_PADDLE_MASK = 0b01;
	
	private byte data;
	
	public PacketPlayOutClientFlags() {}
	
	public PacketPlayOutClientFlags(
		boolean disablePaddleMovement
	) {
		this.data |= disablePaddleMovement ? DISABLE_PADDLE_MASK : 0;
	}
	
	public boolean isMovementDisabled() {
		return (this.data & DISABLE_PADDLE_MASK) != 0;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt8(this.data);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.data = buffer.readInt8();
	}
 }
