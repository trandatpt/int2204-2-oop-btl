package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.libs.Constants.RifleMode;

public class PacketPlayInChangeGunPosition extends NetworkPacket implements IPacketPlayIn {
	public PacketPlayInChangeGunPosition() {}; /* for packet decoding */
	
	private boolean rightSide;
	public PacketPlayInChangeGunPosition(boolean right) {
		this.rightSide = right;
	}
	
	public boolean isRightSide() {
		return rightSide;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeBool(rightSide);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.rightSide = buffer.readBool();
	}
}
