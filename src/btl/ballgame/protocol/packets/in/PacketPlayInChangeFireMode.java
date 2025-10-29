package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.libs.Constants.RifleMode;

public class PacketPlayInChangeFireMode extends NetworkPacket implements IPacketPlayIn {
	public PacketPlayInChangeFireMode() {}; /* for packet decoding */
	
	private RifleMode rifleMode; 
	public PacketPlayInChangeFireMode(RifleMode mode) {
		this.rifleMode = mode;
	}
	
	public RifleMode getRifleMode() {
		return rifleMode;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt8((byte) rifleMode.ordinal());
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.rifleMode = RifleMode.of(buffer.readInt8());
	}
}
