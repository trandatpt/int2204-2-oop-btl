package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayInClientUserCreation extends NetworkPacket implements IPacketPlayIn {
	private String userName;
	private String passwordHash;
	
	public PacketPlayInClientUserCreation() {}; /* for packet decoding */
	
	public PacketPlayInClientUserCreation(String username, String passwordHash) {
		this.userName = username;
		this.passwordHash = passwordHash;
	}
	
	public String who() {
		return this.userName;
	}
	
	public String getPasswordHash() {
		return passwordHash;
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeU8String(this.userName);
		buffer.writeU8String(this.passwordHash);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.userName = buffer.readU8String();
		this.passwordHash = buffer.readU8String();
	}
}
