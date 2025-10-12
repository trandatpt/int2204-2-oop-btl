package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayInClientLogin extends NetworkPacket implements IPacketPlayIn {
	private String userName;
	private String passwordHash;
	private int protocolVersion;
	
	public PacketPlayInClientLogin() {}; /* for packet decoding */
	
	public PacketPlayInClientLogin(String username, String passwordHash, int protocolVersion) {
		this.userName = username;
		this.passwordHash = passwordHash;
		this.protocolVersion = protocolVersion;
	}
	
	public String who() {
		return this.userName;
	}
	
	public String getPasswordHash() {
		return passwordHash;
	}
	
	public int whatVersion() {
		return this.protocolVersion;
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt32(this.protocolVersion);
		buffer.writeU8String(this.userName);
		buffer.writeU8String(this.passwordHash);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.protocolVersion = buffer.readInt32();
		this.userName = buffer.readU8String();
		this.passwordHash = buffer.readU8String();
	}
}
