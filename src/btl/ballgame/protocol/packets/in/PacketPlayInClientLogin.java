package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayInClientLogin extends NetworkPacket implements IPacketPlayIn {
	private String userName;
	private String password;
	
	public PacketPlayInClientLogin() {}; /* for packet decoding */
	
	public PacketPlayInClientLogin(String username, String passwordHash) {
		this.userName = username;
		this.password = passwordHash;
	}
	
	public String who() {
		return this.userName;
	}
	
	public String getPassword() {
		return password;
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeU8String(this.userName);
		buffer.writeU8String(this.password);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.userName = buffer.readU8String();
		this.password = buffer.readU8String();
	}
}
