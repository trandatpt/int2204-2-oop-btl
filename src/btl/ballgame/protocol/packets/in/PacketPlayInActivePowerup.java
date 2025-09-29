package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayInActivePowerup extends NetworkPacket implements IPacketPlayIn {
	public PacketPlayInActivePowerup() {}; /* for packet decoding */
	
	private int powerupId; 
	public PacketPlayInActivePowerup(int powerupId) {
	}
	
	public int getPowerupId() {
		return this.powerupId;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt32(this.powerupId);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.powerupId = buffer.readInt32();
	}
}
