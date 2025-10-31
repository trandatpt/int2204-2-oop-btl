package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayInClientUserCreation extends NetworkPacket implements IPacketPlayIn {
	private String userName;
	private String password;
	
	public PacketPlayInClientUserCreation() {}; /* for packet decoding */
	
	public PacketPlayInClientUserCreation(String username, String password) {
		this.userName = username;
		this.password = password;
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
